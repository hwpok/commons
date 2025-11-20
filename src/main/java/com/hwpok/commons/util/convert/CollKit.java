package com.hwpok.commons.util.convert;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 集合工具类
 * <p>
 * 提供集合的常用操作：空值检查、分割、连接、转换、过滤等。
 * 基于Java 8 Stream API实现，支持函数式编程。
 * </p>
 *
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public final class CollKit {

    private CollKit() {
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @return true-为空或null，false-非空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否非空
     *
     * @param collection 集合
     * @return true-非空，false-为空或null
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断Map是否为空
     *
     * @param map Map对象
     * @return true-为空或null，false-非空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断Map是否非空
     *
     * @param map Map对象
     * @return true-非空，false-为空或null
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 将大集合分割成指定大小的子集合列表
     *
     * @param list      原集合
     * @param batchSize 每批大小
     * @param <T>       泛型类型
     * @return 分割后的集合列表
     * @throws IllegalArgumentException 如果batchSize <= 0
     */
    public static <T> List<List<T>> partition(List<T> list, int batchSize) {
        if (list == null) {
            return new ArrayList<>();
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be positive");
        }

        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            result.add(new ArrayList<>(list.subList(i, Math.min(i + batchSize, list.size()))));
        }
        return result;
    }

    /**
     * 将集合元素用指定分隔符连接成字符串
     *
     * @param collection 集合
     * @param delimiter  分隔符
     * @return 连接后的字符串
     */
    public static String join(Collection<?> collection, String delimiter) {
        if (isEmpty(collection)) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }

        return collection.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * 将集合元素用逗号连接成字符串
     *
     * @param collection 集合
     * @return 连接后的字符串
     */
    public static String join(Collection<?> collection) {
        return join(collection, ",");
    }

    /**
     * 将数组用指定分隔符连接成字符串
     *
     * @param array     数组
     * @param delimiter 分隔符
     * @return 连接后的字符串
     */
    public static String join(Object[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }

        return Arrays.stream(array)
                .map(String::valueOf)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * 将字符串按指定分隔符分割成列表
     *
     * @param source    源字符串
     * @param delimiter 分隔符
     * @return 分割后的列表
     */
    public static List<String> splitToList(String source, String delimiter) {
        if (source == null || source.trim().isEmpty()) {
            return new ArrayList<>();
        }
        if (delimiter == null || delimiter.isEmpty()) {
            return List.of(source);
        }

        return Arrays.stream(source.split(delimiter))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 将字符串按逗号分割成列表
     *
     * @param source 源字符串
     * @return 分割后的列表
     */
    public static List<String> splitToList(String source) {
        return splitToList(source, ",");
    }

    /**
     * 将集合转换为指定类型的列表
     *
     * @param collection 源集合
     * @param mapper     转换函数
     * @param <T>        源类型
     * @param <R>        目标类型
     * @return 转换后的列表
     */
    public static <T, R> List<R> mapToList(Collection<T> collection, Function<T, R> mapper) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }

        return collection.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * 将集合转换为Set
     *
     * @param collection 源集合
     * @param <T>        类型
     * @return Set集合
     */
    public static <T> Set<T> toSet(Collection<T> collection) {
        if (isEmpty(collection)) {
            return new HashSet<>();
        }
        return new HashSet<>(collection);
    }

    /**
     * 过滤集合元素
     *
     * @param collection 源集合
     * @param predicate  过滤条件
     * @param <T>        类型
     * @return 过滤后的列表
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }

        return collection.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 查找第一个匹配的元素
     *
     * @param collection 集合
     * @param predicate  匹配条件
     * @param <T>        类型
     * @return 匹配的元素，未找到返回null
     */
    public static <T> T findFirst(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return null;
        }

        return collection.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断是否存在匹配的元素
     *
     * @param collection 集合
     * @param predicate  匹配条件
     * @param <T>        类型
     * @return true-存在，false-不存在
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return false;
        }

        return collection.stream().anyMatch(predicate);
    }

    /**
     * 判断是否所有元素都匹配
     *
     * @param collection 集合
     * @param predicate  匹配条件
     * @param <T>        类型
     * @return true-都匹配，false-不都匹配
     */
    public static <T> boolean allMatch(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return false;
        }

        return collection.stream().allMatch(predicate);
    }

    /**
     * 获取集合大小（null安全）
     *
     * @param collection 集合
     * @return 大小，null返回0
     */
    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * 获取Map大小（null安全）
     *
     * @param map Map
     * @return 大小，null返回0
     */
    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    /**
     * 创建ArrayList
     *
     * @param elements 元素
     * @param <T>      类型
     * @return ArrayList
     */
    @SafeVarargs
    public static <T> ArrayList<T> newArrayList(T... elements) {
        ArrayList<T> list = new ArrayList<>();
        if (elements != null) {
            Collections.addAll(list, elements);
        }
        return list;
    }

    /**
     * 创建HashSet
     *
     * @param elements 元素
     * @param <T>      类型
     * @return HashSet
     */
    @SafeVarargs
    public static <T> HashSet<T> newHashSet(T... elements) {
        HashSet<T> set = new HashSet<>();
        if (elements != null) {
            Collections.addAll(set, elements);
        }
        return set;
    }

    /**
     * 创建HashMap
     *
     * @param kvs 键值对（奇数位为key，偶数位为value）
     * @return HashMap
     */
    public static HashMap<String, Object> newHashMap(Object... kvs) {
        HashMap<String, Object> map = new HashMap<>();
        if (kvs != null) {
            for (int i = 0; i < kvs.length - 1; i += 2) {
                map.put(String.valueOf(kvs[i]), kvs[i + 1]);
            }
        }
        return map;
    }

    /**
     * 简单的集合转JSON数组字符串（仅适用于简单场景）
     *
     * @param collection 集合
     * @return JSON字符串
     */
    public static String toJsonArray(Collection<?> collection) {
        if (isEmpty(collection)) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        collection.stream()
                .map(obj -> {
                    if (obj == null) {
                        return "null";
                    } else if (obj instanceof String) {
                        return "\"" + obj.toString().replace("\"", "\\\"") + "\"";
                    } else {
                        return obj.toString();
                    }
                })
                .forEach(str -> sb.append(str).append(","));

        // 移除最后的逗号
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * 简单的JSON数组字符串转字符串列表（仅适用于简单场景）
     *
     * @param jsonArray JSON数组字符串
     * @return 字符串列表
     */
    public static List<String> fromJsonArray(String jsonArray) {
        if (jsonArray == null || jsonArray.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 简单处理，实际项目建议使用Jackson或Gson
        String content = jsonArray.trim();
        if (!content.startsWith("[") || !content.endsWith("]")) {
            return new ArrayList<>();
        }

        content = content.substring(1, content.length() - 1);
        if (content.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(content.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    // 移除引号
                    if (s.startsWith("\"") && s.endsWith("\"")) {
                        s = s.substring(1, s.length() - 1);
                    }
                    return s;
                })
                .collect(Collectors.toList());
    }
}
