package com.hwpok.commons.util.system;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描工具类（支持文件系统和JAR）
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public final class ClassScanner {

    private static final String CLASS_SUFFIX = ".class";
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String JAR_PROTOCOL = "jar";
    private static final String FILE_PROTOCOL = "file";

    private ClassScanner() {
    }

    /**
     * 扫描指定包下的所有类名
     *
     * @param packageName        包名，不能为空
     * @param includeSubPackages 是否包含子包
     * @return 类全名列表
     */
    public static List<String> scanClasses(String packageName, boolean includeSubPackages) {
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Package name must not be null or empty");
        }

        List<String> result = new ArrayList<>();
        String packagePath = packageName.replace(PACKAGE_SEPARATOR, "/");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> urls = loader.getResources(packagePath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url == null) continue;

                String protocol = url.getProtocol();
                if (FILE_PROTOCOL.equals(protocol)) {
                    File dir = new File(url.toURI());
                    result.addAll(scanClassesFromFile(dir, packageName, includeSubPackages));
                } else if (JAR_PROTOCOL.equals(protocol)) {
                    result.addAll(scanClassesFromJar(url, packageName, includeSubPackages));
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to scan classes in package: " + packageName + " from URL", e);
        }

        return result;
    }

    private static List<String> scanClassesFromFile(File dir, String packageName, boolean includeSubPackages) {
        List<String> result = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) {
            return result;
        }

        File[] files = dir.listFiles();
        if (files == null) return result;

        for (File file : files) {
            if (file.isDirectory()) {
                if (includeSubPackages) {
                    // 递归子包
                    String subPackageName = packageName + "." + file.getName();
                    result.addAll(scanClassesFromFile(file, subPackageName, true));
                }
                // else: ignore subdirectories when includeSubPackages=false
            } else if (file.getName().endsWith(CLASS_SUFFIX)) {
                String simpleClassName = file.getName().substring(0, file.getName().length() - CLASS_SUFFIX.length());
                // 过滤内部类
                if (!simpleClassName.contains("$")) {
                    result.add(packageName + "." + simpleClassName);
                }
            }
        }
        return result;
    }

    private static List<String> scanClassesFromJar(URL url, String packageName, boolean includeSubPackages) {
        List<String> result = new ArrayList<>();
        try (JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (!entryName.endsWith(CLASS_SUFFIX) || entryName.contains("$")) {
                    continue;
                }

                String className = entryName.substring(0, entryName.length() - CLASS_SUFFIX.length())
                        .replace("/", PACKAGE_SEPARATOR);

                if (isValidClass(className, packageName, includeSubPackages)) {
                    result.add(className);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan JAR: " + url, e);
        }
        return result;
    }

    private static boolean isValidClass(String className, String packageName, boolean includeSubPackages) {
        if (includeSubPackages) {
            return className.startsWith(packageName + ".");
        } else {
            int lastDot = className.lastIndexOf(PACKAGE_SEPARATOR);
            if (lastDot == -1) return false;
            String pkg = className.substring(0, lastDot);
            return packageName.equals(pkg);
        }
    }
}