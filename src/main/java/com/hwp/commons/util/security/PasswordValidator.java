package com.hwp.commons.util.security;


import com.hwp.commons.model.Res;

/**
 * <p>密码强度校验工具类。</p>
 * <p>
 * 提供灵活的密码策略校验，包括长度、字符类型组合等。
 * </p>
 * <p>
 * <strong>字符类型定义：</strong>
 * <ul>
 *   <li>数字：0-9</li>
 *   <li>小写字母：a-z（仅 ASCII）</li>
 *   <li>大写字母：A-Z（仅 ASCII）</li>
 *   <li>特殊字符：非字母、非数字、且非空白字符（如 !@#$%^&* 等）</li>
 * </ul>
 * 空格等空白字符会被忽略（不计入任何类型，也不导致校验失败）。
 * </p>
 *
 * @author Wanpeng.Hui
 * @since 2021-05-26 (重构版本)
 */
@SuppressWarnings("unused")
public final class PasswordValidator {

    // --- 默认策略常量 ---
    public static final int MIN_LENGTH = 6;
    public static final int MAX_LENGTH = 32;

    // --- 字符类型策略常量 (使用位掩码) ---
    public static final int REQUIRE_NUM = 1;          // 必须包含数字
    public static final int REQUIRE_LOWER = 2;        // 必须包含小写字母
    public static final int REQUIRE_UPPER = 4;        // 必须包含大写字母
    public static final int REQUIRE_SPECIAL = 8;      // 必须包含特殊字符

    // --- 常用组合策略 ---
    public static final int STRATEGY_NUM_LOWER_UPPER = REQUIRE_NUM | REQUIRE_LOWER | REQUIRE_UPPER; // 7
    public static final int STRATEGY_NUM_LOWER_UPPER_SPECIAL = REQUIRE_NUM | REQUIRE_LOWER | REQUIRE_UPPER | REQUIRE_SPECIAL; // 15

    private PasswordValidator() {
    }

    /**
     * 校验密码是否符合指定策略。
     *
     * @param password           待校验的密码
     * @param requiredStrategies 所需的策略组合，例如 {@link #STRATEGY_NUM_LOWER_UPPER}
     * @return {@link Res} 包含校验结果和错误信息
     */
    public static Res<Boolean> validate(String password, int requiredStrategies) {
        if (password == null) {
            return Res.fail("密码不能为空");
        }
        int length = password.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            return Res.fail("密码长度必须在 " + MIN_LENGTH + " 到 " + MAX_LENGTH + " 个字符之间");
        }

        int foundTypes = 0;
        for (int i = 0; i < length; i++) {
            char c = password.charAt(i);
            if (Character.isDigit(c)) {
                foundTypes |= REQUIRE_NUM;
            } else if (Character.isLowerCase(c)) {
                foundTypes |= REQUIRE_LOWER;
            } else if (Character.isUpperCase(c)) {
                foundTypes |= REQUIRE_UPPER;
            } else if (!Character.isWhitespace(c)) {
                // 非空白字符且非字母数字 → 视为特殊字符
                foundTypes |= REQUIRE_SPECIAL;
            }
        }

        if ((requiredStrategies & REQUIRE_NUM) != 0 && (foundTypes & REQUIRE_NUM) == 0) {
            return Res.fail("弱密码：必须包含至少一个数字");
        }
        if ((requiredStrategies & REQUIRE_LOWER) != 0 && (foundTypes & REQUIRE_LOWER) == 0) {
            return Res.fail("弱密码：必须包含至少一个小写字母");
        }
        if ((requiredStrategies & REQUIRE_UPPER) != 0 && (foundTypes & REQUIRE_UPPER) == 0) {
            return Res.fail("弱密码：必须包含至少一个大写字母");
        }
        if ((requiredStrategies & REQUIRE_SPECIAL) != 0 && (foundTypes & REQUIRE_SPECIAL) == 0) {
            return Res.fail("弱密码：必须包含至少一个特殊字符");
        }

        return Res.success(true);
    }

    /**
     * 快速校验密码是否有效
     *
     * @param password           待校验的密码
     * @param requiredStrategies 所需的策略组合
     * @return 如果密码有效则返回 true，否则返回 false
     */
    public static boolean isValid(String password, int requiredStrategies) {
        return validate(password, requiredStrategies).success();
    }
}