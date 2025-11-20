package com.hwpok.commons.util.identity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 机器唯一标识工具类
 * <p>
 * 获取硬件唯一标识，用于软件授权、设备识别等场景。
 * 支持缓存机制，避免重复执行系统命令。
 * </p>
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public final class MachineId {
    public static final String OS_MAC = "mac";
    public static final String OS_LINUX = "linux";
    public static final String OS_WINDOWS = "windows";

    /**
     * 命令执行超时时间（秒）
     */
    private static final int COMMAND_TIMEOUT_SECONDS = 10;

    /**
     * 预编译正则表达式
     */
    private static final Pattern LINE_SEPARATOR = Pattern.compile("\\r?\\n");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private static final class Commands {
        // Linux 命令
        static final String LINUX_CPU_ID = "dmidecode -t 4 | grep 'ID' | head -n1";
        static final String LINUX_CPU_INFO = "cat /proc/cpuinfo | grep 'model name' | head -n1";
        static final String LINUX_BOARD_SN = "dmidecode -s baseboard-serial-number";
        static final String LINUX_PRODUCT_UUID = "cat /sys/class/dmi/id/product_uuid 2>/dev/null";

        // Windows 命令
        static final String WMIC_CPU = "wmic cpu get ProcessorId";
        static final String WMIC_BOARD = "wmic baseboard get SerialNumber";

        // macOS 命令
        static final String MAC_SERIAL = "system_profiler SPHardwareDataType | grep 'Serial Number'";
        static final String MAC_CPU_BRAND = "sysctl -n machdep.cpu.brand_string";
        static final String MAC_HARDWARE_UUID = "system_profiler SPHardwareDataType | grep 'Hardware UUID'";

        private Commands() {
        }
    }

    private static volatile String cachedCpuId;
    private static volatile String cachedSecurityCode;

    private MachineId() {
    }

    /**
     * 获取操作系统类型
     *
     * @return 操作系统类型（mac/linux/windows）
     */
    public static String getOsType() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains(OS_LINUX)) return OS_LINUX;
        if (os.contains(OS_MAC)) return OS_MAC;
        return OS_WINDOWS;
    }

    /**
     * 获取CPU唯一标识（带缓存）
     *
     * @return CPU ID，获取失败返回空字符串
     */
    public static String getCpuId() {
        if (cachedCpuId == null) {
            synchronized (MachineId.class) {
                if (cachedCpuId == null) {
                    cachedCpuId = WHITESPACE.matcher(fetchCpuId()).replaceAll("");
                }
            }
        }
        return cachedCpuId;
    }

    /**
     * 获取设备安全码（带缓存）
     *
     * @return 安全码，获取失败返回空字符串
     */
    public static String getSecurityCode() {
        if (cachedSecurityCode == null) {
            synchronized (MachineId.class) {
                if (cachedSecurityCode == null) {
                    cachedSecurityCode = WHITESPACE.matcher(fetchSecurityCode()).replaceAll("");
                }
            }
        }
        return cachedSecurityCode;
    }


    /**
     * 获取CPU ID（实际执行逻辑）
     */
    private static String fetchCpuId() {
        String os = getOsType();
        switch (os) {
            case OS_LINUX:
                // 优先尝试 dmidecode（需 root 权限）
                String cpuId = executeAndExtract(Commands.LINUX_CPU_ID, ":");
                if (!cpuId.isEmpty()) return cpuId;
                // 权限不足时回退到 /proc/cpuinfo
                return executeAndExtract(Commands.LINUX_CPU_INFO, ":");

            case OS_WINDOWS:
                return executeWindowsWmic("cpu", "ProcessorId");

            case OS_MAC:
                // macOS 优先获取序列号
                String serial = executeAndExtract(Commands.MAC_SERIAL, ":");
                if (!serial.isEmpty()) return serial;
                // 备选方案：硬件UUID
                return executeAndExtract(Commands.MAC_HARDWARE_UUID, ":");

            default:
                return "";
        }
    }

    /**
     * 获取安全码（实际执行逻辑）
     */
    private static String fetchSecurityCode() {
        String os = getOsType();
        switch (os) {
            case OS_LINUX:
                // 尝试获取主板序列号
                String sn = executeAndExtract(Commands.LINUX_BOARD_SN, null);
                if (!sn.isEmpty()) return sn;
                // 若失败，尝试系统 UUID
                sn = executeAndExtract(Commands.LINUX_PRODUCT_UUID, null);
                return sn.toLowerCase().contains("none") ? "" : sn;

            case OS_WINDOWS:
                return executeWindowsWmic("baseboard", "SerialNumber");

            case OS_MAC:
                // macOS 使用 Hardware UUID 作为唯一标识
                return executeAndExtract(Commands.MAC_HARDWARE_UUID, ":");

            default:
                return "";
        }
    }

    /**
     * 执行 Windows WMIC 命令并提取值
     *
     * @param wmiClass WMI类名
     * @param property 属性名
     * @return 提取的值
     */
    private static String executeWindowsWmic(String wmiClass, String property) {
        String cmd = "wmic " + wmiClass + " get " + property;
        String output = executeCommand(new String[]{"cmd", "/c", cmd});
        if (output.isEmpty()) return "";

        return LINE_SEPARATOR.splitAsStream(output)
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.equalsIgnoreCase(property))
                .findFirst()
                .orElse("");
    }

    /**
     * 执行 shell 命令并按分隔符提取值
     *
     * @param command   命令字符串
     * @param separator 分隔符，null表示直接返回整行
     * @return 提取的值
     */
    private static String executeAndExtract(String command, String separator) {
        String output = executeCommand(new String[]{"sh", "-c", command});
        if (output.isEmpty()) return "";

        return LINE_SEPARATOR.splitAsStream(output)
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> {
                    if (separator != null && line.contains(separator)) {
                        String[] parts = line.split(Pattern.quote(separator), 2);
                        return parts.length > 1 ? parts[1].trim() : "";
                    }
                    return separator == null ? line : "";
                })
                .filter(result -> !result.isEmpty())
                .findFirst()
                .orElse("");
    }

    /**
     * 执行系统命令（带超时控制）
     *
     * @param command 命令数组
     * @return 命令输出，失败返回空字符串
     */
    private static String executeCommand(String[] command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true); // 合并错误流

        try {
            Process process = pb.start();

            // 防止命令卡死
            if (!process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                System.err.println("Command timeout: " + String.join(" ", command));
                return "";
            }

            // 检查退出码
            if (process.exitValue() != 0) {
                return "";
            }

            // 读取输出
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Command failed: " + String.join(" ", command));
            return "";
        }
    }
}
