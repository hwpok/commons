package com.hwp.commons.util.system;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * 最近最少使用淘汰策略的缓存
 * </p>
 *
 * @author Wanpeng.Hui
 * @since 2020/12/24 14:32
 */
@SuppressWarnings("unused")
public class LruCache<T> {
    /**
     * 缓存数量
     */
    private final int cacheSize;

    /**
     * 存储数据的Map
     */
    private final Map<String, Item<T>> map;

    /**
     * 读写锁
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 构造方法
     *
     * @param maxCacheSize 缓存大小
     */
    public LruCache(int maxCacheSize) {
        cacheSize = maxCacheSize;
        int initialCapacity = (int) Math.ceil(cacheSize / 0.75f) + 1;
        // linkedHashMap的accessOrder为true, 将按访问顺序排列元素，再重写removeEldestEntry，就可实现LRU策略
        map = new LinkedHashMap<>(initialCapacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Item<T>> eldest) {
                // 删除最老项的条件，在put元素时，会调用此方法
                return size() > cacheSize;
            }
        };
    }

    /**
     * 缓存元素
     * 已实现多线程同步
     *
     * @param key          键
     * @param data         数据
     * @param expireSecond 过期秒数
     */
    public void put(String key, T data, int expireSecond) {
        lock.writeLock().lock();
        try {
            map.put(key, new Item<>(data, expireSecond));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 缓存元素, 默认缓存3600秒
     * 已实现多线程同步
     *
     * @param key  键
     * @param data 数据
     */
    public void put(String key, T data) {
        lock.writeLock().lock();
        try {
            map.put(key, new Item<>(data, 3600));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 获取元素, 为空或过期返回默认值
     * 已实现多线程同步
     *
     * @param key        键
     * @param defaultObj 默认值
     * @return 返回数据
     */
    public T get(String key, T defaultObj) {
        lock.readLock().lock();
        try {
            Item<T> item = map.get(key);
            if (null == item) {
                return defaultObj;
            }
            if (item.isExpired()) {
                // 升级到写锁删除过期项
                lock.readLock().unlock();
                lock.writeLock().lock();
                try {
                    // 双重检查
                    item = map.get(key);
                    if (item != null && item.isExpired()) {
                        map.remove(key);
                        return defaultObj;
                    }
                    return item != null ? item.getData() : defaultObj;
                } finally {
                    lock.writeLock().unlock();
                }
            }
            T data = item.getData();
            return data == null ? defaultObj : data;
        } finally {
            if (lock.getReadHoldCount() > 0) {
                lock.readLock().unlock();
            }
        }
    }

    /**
     * 获取元素，未找到或过期返回NUll
     * 已实现多线程同步
     *
     * @param key 键
     * @return 返回数据
     */
    public T get(String key) {
        return get(key, null);
    }

    /**
     * 已实现多线程同步
     *
     * @param key 键
     */
    public void remove(String key) {
        lock.writeLock().lock();
        try {
            map.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 缓存数据项类
     */
    private static class Item<D> {
        /**
         * 缓存数据对象
         */
        private final D data;
        /**
         * 过期的秒数
         */
        private final long expireTime;

        /**
         * 构造方法
         *
         * @param data         数据
         * @param expireSecond 过期时间
         */
        private Item(D data, int expireSecond) {
            this.data = data;
            long putInTime = System.currentTimeMillis();
            this.expireTime = putInTime + expireSecond * 1000L;
        }

        /**
         * 获取数据
         *
         * @return 返回数据
         */
        private D getData() {
            return data;
        }

        /**
         * 是否已过期
         *
         * @return 是否过期
         */
        private boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
}

