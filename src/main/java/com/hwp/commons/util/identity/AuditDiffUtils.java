package com.hwp.commons.util.identity;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 审计差异工具类
 * 用于比较对象属性差异，支持审计日志记录
 *
 * <p>主要功能：
 * <ul>
 *   <li>比较两个对象的字段差异</li>
 *   <li>生成格式化的审计报告</li>
 *   <li>支持字段忽略和自定义格式化</li>
 *   <li>高性能字段缓存机制</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 比较对象差异
 * List<FieldDiff> diffs = AuditDiffUtils.compareFields(oldUser, newUser, "id", "createTime");
 *
 * // 生成审计报告
 * String report = AuditDiffUtils.formatAuditReport(diffs);
 *
 * // 生成详细报告
 * String detail = AuditDiffUtils.formatAuditDetail(diffs);
 * }</pre>
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class AuditDiffUtils {

    private AuditDiffUtils() {
    }

    /**
     * 字段缓存，提升反射性能
     */
    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 数字格式化器
     */
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 比较两个对象的属性差异
     *
     * @param oldObj 原对象
     * @param newObj 新对象
     * @return 差异列表，如果没有差异返回空列表
     */
    public static List<FieldDiff> compareFields(Object oldObj, Object newObj) {
        return compareFields(oldObj, newObj, new String[0]);
    }

    /**
     * 比较两个对象的属性差异（忽略指定字段）
     *
     * @param oldObj 原对象
     * @param newObj 新对象
     * @param ignoreFields 忽略的字段名
     * @return 差异列表，如果没有差异返回空列表
     */
    public static List<FieldDiff> compareFields(Object oldObj, Object newObj, String... ignoreFields) {
        List<FieldDiff> diffs = new ArrayList<>();

        // 空值检查
        if (oldObj == null && newObj == null) {
            return diffs;
        }

        if (oldObj == null || newObj == null) {
            diffs.add(new FieldDiff("object",
                    formatValue(oldObj),
                    formatValue(newObj),
                    "对象为null"));
            return diffs;
        }

        // 类型检查
        Class<?> clazz = oldObj.getClass();
        if (!clazz.equals(newObj.getClass())) {
            diffs.add(new FieldDiff("class",
                    clazz.getName(),
                    newObj.getClass().getName(),
                    "对象类型不匹配"));
            return diffs;
        }

        // 获取所有字段并比较
        Set<String> ignoreSet = new HashSet<>(Arrays.asList(ignoreFields));
        List<Field> fields = getAllFields(clazz);

        for (Field field : fields) {
            if (ignoreSet.contains(field.getName()) ||
                    Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);

                // 使用 Objects.equals 进行空安全比较
                if (!Objects.equals(oldValue, newValue)) {
                    diffs.add(new FieldDiff(
                            field.getName(),
                            formatValue(oldValue),
                            formatValue(newValue),
                            getFieldDescription(field)
                    ));
                }
            } catch (IllegalAccessException e) {
                diffs.add(new FieldDiff(
                        field.getName(),
                        "ACCESS_ERROR",
                        "ACCESS_ERROR",
                        "字段访问失败: " + e.getMessage()
                ));
            }
        }

        return diffs;
    }

    /**
     * 检测对象变更（语义化方法名）
     *
     * @param before 变更前对象
     * @param after 变更后对象
     * @param ignoreFields 忽略的字段
     * @return 变更列表
     */
    public static List<FieldDiff> detectChanges(Object before, Object after, String... ignoreFields) {
        return compareFields(before, after, ignoreFields);
    }

    /**
     * 格式化差异列表为审计报告
     *
     * @param diffs 差异列表
     * @return 格式化的审计报告字符串
     */
    public static String formatAuditReport(List<FieldDiff> diffs) {
        if (diffs == null || diffs.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < diffs.size(); i++) {
            if (i > 0) sb.append(", ");
            FieldDiff diff = diffs.get(i);
            sb.append("{")
                    .append(diff.getFieldName()).append(": ")
                    .append(diff.getOldValue()).append(" -> ")
                    .append(diff.getNewValue())
                    .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 格式化差异列表为详细审计报告
     *
     * @param diffs 差异列表
     * @return 详细的审计报告字符串
     */
    public static String formatAuditDetail(List<FieldDiff> diffs) {
        if (diffs == null || diffs.isEmpty()) {
            return "无差异";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("发现 ").append(diffs.size()).append(" 处差异:\n");
        sb.append("========================================\n");

        for (int i = 0; i < diffs.size(); i++) {
            FieldDiff diff = diffs.get(i);
            sb.append(String.format("%d. 字段名: %s (类型: %s)\n",
                    i + 1, diff.getFieldName(), diff.getDescription()));
            sb.append(String.format("   旧值: %s\n", diff.getOldValue()));
            sb.append(String.format("   新值: %s\n", diff.getNewValue()));

            if (i < diffs.size() - 1) {
                sb.append("----------------------------------------\n");
            }
        }
        sb.append("========================================\n");

        return sb.toString();
    }

    /**
     * 将对象格式化为字符串
     *
     * @param obj 对象
     * @return 格式化后的字符串
     */
    public static String formatValue(Object obj) {
        if (obj == null) {
            return "null";
        }

        // 字符串类型
        if (obj instanceof String) {
            return (String) obj;
        }

        // 数字类型
        if (obj instanceof Number) {
            return formatNumber((Number) obj);
        }

        // 布尔类型
        if (obj instanceof Boolean) {
            return obj.toString();
        }

        // 日期类型
        if (obj instanceof Date) {
            return DATE_TIME_FORMATTER.format(
                    ((Date) obj).toInstant());
        }

        // LocalDateTime 类型
        if (obj instanceof LocalDateTime) {
            return DATE_TIME_FORMATTER.format((LocalDateTime) obj);
        }

        // 集合类型
        if (obj instanceof Collection) {
            return String.format("Collection[size=%d]", ((Collection<?>) obj).size());
        }

        // 数组类型
        if (obj.getClass().isArray()) {
            return String.format("Array[length=%d]", java.lang.reflect.Array.getLength(obj));
        }

        // 枚举类型
        if (obj instanceof Enum) {
            return ((Enum<?>) obj).name();
        }

        // 默认处理
        return obj.toString();
    }


    /**
     * 获取类的所有字段（包括父类），使用缓存提升性能
     *
     * @param clazz 类对象
     * @return 字段列表
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, k -> {
            List<Field> fields = new ArrayList<>();
            Class<?> currentClass = k;

            while (currentClass != null && currentClass != Object.class) {
                Field[] declaredFields = currentClass.getDeclaredFields();
                for (Field field : declaredFields) {
                    // 过滤静态字段
                    if (!Modifier.isStatic(field.getModifiers())) {
                        fields.add(field);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }

            return fields;
        });
    }

    /**
     * 格式化数字
     *
     * @param number 数字对象
     * @return 格式化后的字符串
     */
    private static String formatNumber(Number number) {
        if (number instanceof BigDecimal bd) {
            return DECIMAL_FORMAT.format(bd);
        }

        if (number instanceof Float || number instanceof Double) {
            return DECIMAL_FORMAT.format(number.doubleValue());
        }

        return number.toString();
    }

    /**
     * 获取字段的类型描述
     *
     * @param field 字段对象
     * @return 类型描述字符串
     */
    private static String getFieldDescription(Field field) {
        Class<?> type = field.getType();

        if (Number.class.isAssignableFrom(type) || type.isPrimitive()) {
            return "数值类型";
        }
        if (type == String.class) {
            return "字符串";
        }
        if (type == Date.class || type == LocalDateTime.class) {
            return "时间类型";
        }
        if (type == Boolean.class || type == boolean.class) {
            return "布尔类型";
        }
        if (type.isEnum()) {
            return "枚举类型";
        }
        if (Collection.class.isAssignableFrom(type)) {
            return "集合类型";
        }
        if (type.isArray()) {
            return "数组类型";
        }

        return "对象类型";
    }


    /**
     * 字段差异信息
     * 封装字段变更的详细信息
     */
    @Getter
    public static class FieldDiff {
        private final String fieldName;
        private final String oldValue;
        private final String newValue;
        private final String description;

        /**
         * 构造字段差异对象
         *
         * @param fieldName 字段名
         * @param oldValue 旧值
         * @param newValue 新值
         * @param description 字段描述
         */
        public FieldDiff(String fieldName, String oldValue, String newValue, String description) {
            this.fieldName = fieldName;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.description = description;
        }

        /**
         * 判断是否有实际变更
         *
         * @return true表示有变更，false表示无变更
         */
        public boolean hasChanged() {
            return !Objects.equals(oldValue, newValue);
        }

        @Override
        public String toString() {
            return String.format("%s: %s -> %s", fieldName, oldValue, newValue);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            FieldDiff fieldDiff = (FieldDiff) obj;
            return Objects.equals(fieldName, fieldDiff.fieldName) &&
                    Objects.equals(oldValue, fieldDiff.oldValue) &&
                    Objects.equals(newValue, fieldDiff.newValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fieldName, oldValue, newValue);
        }
    }
}
