package com.hwpok.commons.util.system;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * 确保应用中只有一个实例
 * <p>
 * public static void main(String[] args) {
 *     // 启用应用时调用, 只能启用一个实例
 *     SingleAppLock.ensure("MyBilling");
 * }
 * @author wanpeng.hui
 */
@SuppressWarnings("unused")
public class SingleAppLock {
    private static RandomAccessFile lockFile;
    private static FileLock lock;

    /**
     * 尝试以 singleId 为标识启动单实例应用
     * @param singleId 应用唯一标识（建议使用应用名）
     * @throws IllegalStateException 如果已有实例在运行
     */
    public static void ensure(String singleId) {
        try {
            String tmpDir = System.getProperty("java.io.tmpdir");
            File lockFileObj = new File(tmpDir, singleId + ".lock");

            // 保持引用！防止 GC 或自动关闭
            lockFile = new RandomAccessFile(lockFileObj, "rw");
            FileChannel channel = lockFile.getChannel();
            lock = channel.tryLock();

            if (lock == null) {
                lockFile.close(); // 释放资源
                throw new IllegalStateException("Another instance is already running.");
            }

            // 可选：注册 shutdown hook 清理（非必须，OS 会回收）
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (lock != null) lock.release();
                    if (lockFile != null) lockFile.close();
                    boolean status = lockFileObj.delete();
                } catch (Exception ignored) {}
            }));

        } catch (Exception e) {
            throw new RuntimeException("Failed to acquire single-instance lock", e);
        }
    }
}
