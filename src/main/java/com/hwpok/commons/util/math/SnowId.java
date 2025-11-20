package com.hwpok.commons.util.math;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Twitter Snowflake ID 生成器（64位 long）
 * <p>
 * 结构：1bit(0) + 41bit(时间戳) + 5bit(数据中心) + 5bit(机器ID) + 12bit(序列号)
 * 默认起始时间：2022-01-01 00:00:00 UTC (1640966400000L)
 * </p>
 *
 * @author wanpeng.hui
 * @since 2022-01-01
 */
@SuppressWarnings("unused")
public final class SnowId {

    /**
     * 默认起始时间戳（2022-01-01 00:00:00 UTC）
     */
    private static final long DEFAULT_EPOCH = 1640966400000L;

    /**
     * 各部分位数
     */
    private static final int SEQUENCE_BITS = 12;
    private static final int WORKER_ID_BITS = 5;
    private static final int DATACENTER_ID_BITS = 5;

    /**
     * 最大值
     */
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;      // 4095
    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;    // 31
    private static final long MAX_DATACENTER_ID = (1L << DATACENTER_ID_BITS) - 1; // 31

    /**
     * 位移量
     */
    private static final int WORKER_ID_SHIFT = SEQUENCE_BITS;                     // 12
    private static final int DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS; // 17
    private static final int TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS; // 22

    /**
     * 时钟回拨容忍阈值（毫秒）
     */
    private static final long MAX_CLOCK_BACKWARD_MS = 5000L;

    private final long epoch;
    private final long datacenterId;
    private final long workerId;
    private final long maxClockBackwardMs;

    /**
     * 使用AtomicLong保证原子性
     */
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);
    private final AtomicLong sequence = new AtomicLong(0L);

    /**
     * 构造器（使用默认起始时间）
     *
     * @param datacenterId 数据中心ID (0-31)
     * @param workerId     机器ID (0-31)
     */
    public SnowId(long datacenterId, long workerId) {
        this(datacenterId, workerId, DEFAULT_EPOCH, MAX_CLOCK_BACKWARD_MS);
    }

    /**
     * 构造器
     *
     * @param datacenterId       数据中心ID (0-31)
     * @param workerId           机器ID (0-31)
     * @param epoch              起始时间戳
     * @param maxClockBackwardMs 最大容忍时钟回拨时间
     */
    public SnowId(long datacenterId, long workerId, long epoch, long maxClockBackwardMs) {
        validateId(datacenterId, MAX_DATACENTER_ID, "datacenterId");
        validateId(workerId, MAX_WORKER_ID, "workerId");

        this.datacenterId = datacenterId;
        this.workerId = workerId;
        this.epoch = epoch;
        this.maxClockBackwardMs = maxClockBackwardMs;
    }

    /**
     * 生成下一个ID
     *
     * @return 64位长整型ID
     * @throws ClockBackwardException 时钟回拨异常
     */
    public long nextId() {
        long currentTimestamp = timeGen();
        long lastTimestampValue = lastTimestamp.get();

        // 处理时钟回拨
        if (currentTimestamp < lastTimestampValue) {
            handleClockBackward(currentTimestamp, lastTimestampValue);
        }

        // 同一时间戳内，序列号递增
        if (currentTimestamp == lastTimestampValue) {
            long sequenceValue = sequence.incrementAndGet() & MAX_SEQUENCE;
            // 序列号溢出，等待下一毫秒
            if (sequenceValue == 0) {
                currentTimestamp = tilNextMillis(lastTimestampValue);
            }
        } else {
            // 新的时间戳，重置序列号
            sequence.set(0);
        }

        // 更新最后时间戳
        lastTimestamp.set(currentTimestamp);

        // 组装ID
        return assembleId(currentTimestamp);
    }

    /**
     * 批量生成ID
     *
     * @param count 生成数量
     * @return ID数组
     */
    public long[] nextIds(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }

        long[] ids = new long[count];
        for (int i = 0; i < count; i++) {
            ids[i] = nextId();
        }
        return ids;
    }

    /**
     * 解析ID
     *
     * @param id 雪花ID
     * @return 解析结果
     */
    public IdInfo parseId(long id) {
        long timestamp = (id >>> TIMESTAMP_SHIFT) + epoch;
        long datacenterId = (id >>> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
        long workerId = (id >>> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long sequence = id & MAX_SEQUENCE;

        return new IdInfo(id, timestamp, datacenterId, workerId, sequence);
    }

    /**
     * 组装ID
     */
    private long assembleId(long timestamp) {
        return ((timestamp - epoch) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence.get();
    }

    /**
     * 处理时钟回拨
     */
    private void handleClockBackward(long currentTimestamp, long lastTimestampValue) {
        long backwardMs = lastTimestampValue - currentTimestamp;

        if (backwardMs > maxClockBackwardMs) {
            throw new ClockBackwardException(
                    String.format("Clock moved backwards too much. Current: %d, Last: %d, Backward: %dms",
                            currentTimestamp, lastTimestampValue, backwardMs));
        }

        // 在容忍范围内，等待时钟追上
        try {
            Thread.sleep(backwardMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ClockBackwardException("Interrupted while waiting for clock recovery", e);
        }
    }

    /**
     * 等待下一毫秒
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 验证ID参数
     */
    private void validateId(long id, long maxId, String name) {
        if (id < 0 || id > maxId) {
            throw new IllegalArgumentException(
                    String.format("%s must be between 0 and %d", name, maxId));
        }
    }

    /**
     * 创建默认实例（自动分配workerId）
     */
    public static SnowId createDefault() {
        return createDefault(0);
    }

    /**
     * 创建默认实例（自动分配workerId）
     */
    public static SnowId createDefault(long datacenterId) {
        // 使用MAC地址或随机数生成workerId
        long workerId = ThreadLocalRandom.current().nextLong(0, MAX_WORKER_ID + 1);
        return new SnowId(datacenterId, workerId);
    }


    /**
     * ID信息
     */
    @Getter
    public static class IdInfo {
        private final long id;
        private final long timestamp;
        private final long datacenterId;
        private final long workerId;
        private final long sequence;

        public IdInfo(long id, long timestamp, long datacenterId, long workerId, long sequence) {
            this.id = id;
            this.timestamp = timestamp;
            this.datacenterId = datacenterId;
            this.workerId = workerId;
            this.sequence = sequence;
        }

        public LocalDateTime getDateTime() {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        }

        @Override
        public String toString() {
            return String.format(
                    "IdInfo{id=%d, timestamp=%d, datacenterId=%d, workerId=%d, sequence=%d, dateTime=%s}",
                    id, timestamp, datacenterId, workerId, sequence, getDateTime()
            );
        }
    }

    /**
     * 时钟回拨异常
     */
    public static class ClockBackwardException extends RuntimeException {
        public ClockBackwardException(String message) {
            super(message);
        }

        public ClockBackwardException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}