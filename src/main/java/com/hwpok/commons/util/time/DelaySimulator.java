package com.hwpok.commons.util.time;

import java.util.concurrent.TimeUnit;

/**
 * <p>线程延迟模拟工具，主要用于测试和开发环境。</p>
 * <p><strong>警告：切勿在生产环境中使用此类。</strong></p>
 *
 * @author Wanpeng.Hui
 * @since 2018/08/25
 */
@SuppressWarnings("unused")
public final class DelaySimulator {

    private DelaySimulator() {
    }

    /**
     * 模拟指定时间的延迟。
     *
     * @param duration 延迟时长
     * @param unit     时间单位
     * @throws InterruptedException 如果当前线程在睡眠期间被中断
     */
    public static void delay(long duration, TimeUnit unit) throws InterruptedException {
        if (duration <= 0) {
            return;
        }
        // 正确处理中断异常，并恢复中断状态
        unit.sleep(duration);
    }

    /**
     * 模拟指定毫秒的延迟。
     *
     * @param millis 延迟的毫秒数
     * @throws InterruptedException 如果当前线程在睡眠期间被中断
     */
    public static void delayMillis(long millis) throws InterruptedException {
        delay(millis, TimeUnit.MILLISECONDS);
    }

    /**
     * 模拟指定秒的延迟。
     *
     * @param seconds 延迟的秒数
     * @throws InterruptedException 如果当前线程在睡眠期间被中断
     */
    public static void delaySeconds(long seconds) throws InterruptedException {
        delay(seconds, TimeUnit.SECONDS);
    }
}

