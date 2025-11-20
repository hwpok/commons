package com.hwp.commons.enums;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 枚举工具, 缓存枚举类型和枚举值
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public class EnumResolver {
    private static final Map<Class<?>, Map<?, ?>> ENUM_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<?, ?>> NAME_CACHE = new ConcurrentHashMap<>();

    /**
     * 【严格模式】根据code解析出对应的枚举实例。
     * <p>这是一个严格的方法，如果找不到对应的枚举，返回NULL</p>
     *
     * @param enumClass 枚举类的Class对象
     * @param code      状态码，不能为null
     * @param <C>       Code的类型
     * @param <N>       Name的类型
     * @param <E>       枚举的类型
     * @return 对应的枚举实例，可能为NULL
     */
    @SuppressWarnings("unchecked")
    public static <C, N, E extends Enum<E> & IEnum<C, N>> E getByCode(Class<E> enumClass, C code) {
        if (code == null) {
            return null;
        }
        // computeIfAbsent 可以避免多线程的问题
        Map<C, E> enumMap = (Map<C, E>) ENUM_CACHE.computeIfAbsent(enumClass, clazz -> {
            Map<C, E> map = new ConcurrentHashMap<>();
            for (E enumConstant : enumClass.getEnumConstants()) {
                map.put(enumConstant.getCode(), enumConstant);
            }
            // 返回不可变视图
            return Collections.unmodifiableMap(map);
        });
        return enumMap.get(code);
    }

    /**
     * 根据code获取对应的name，如果找不到则返回默认值。
     *
     * @param enumClass 枚举类的Class对象
     * @param code      状态码
     * @param defName   找不到时返回的默认name
     * @param <C>       Code的类型
     * @param <N>       Name的类型
     * @param <E>       枚举的类型
     * @return 对应的name，或默认的defName
     */
    @SuppressWarnings("unchecked")
    public static <C, N, E extends Enum<E> & IEnum<C, N>> N getNameByCode(Class<E> enumClass, C code, N defName) {
        if (code == null) {
            return defName;
        }

        // computeIfAbsent 可以避免多线程的问题
        Map<C, N> nameMap = (Map<C, N>) NAME_CACHE.computeIfAbsent(enumClass, clazz -> {
            Map<C, N> map = new ConcurrentHashMap<>();
            for (E enumConstant : enumClass.getEnumConstants()) {
                map.put(enumConstant.getCode(), enumConstant.getName());
            }
            // 返回不可变视图
            return Collections.unmodifiableMap(map);
        });

        N name = nameMap.get(code);
        return name == null ? defName : name;
    }

    public static <C, N, E extends Enum<E> & IEnum<C, N>> N getNameByCode(Class<E> enumClass, C code) {
        return getNameByCode(enumClass, code, null);
    }

    public static void clearCache() {
        ENUM_CACHE.clear();
        NAME_CACHE.clear();
    }

    public static void clearCache(Class<?> enumClass) {
        ENUM_CACHE.remove(enumClass);
        NAME_CACHE.remove(enumClass);
    }
}
