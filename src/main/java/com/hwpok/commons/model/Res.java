package com.hwpok.commons.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用响应封装类
 *
 * @param <T> 响应数据
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@Setter
@SuppressWarnings("unused")
@ToString(exclude = {"data"})
public class Res<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 响应代码
     */
    private String code = Const.CODE_SUCCESS;
    /**
     * 响应消息
     */
    private String message = Const.MSG_SUCCESS;
    /**
     * 响应数据
     */
    private T data;

    private Res() {
    }

    public boolean success() {
        return Const.CODE_SUCCESS.equals(this.code);
    }

    public boolean fail() {
        return !success();
    }

    public static <T> Res<T> success(T data) {
        Res<T> res = new Res<>();
        res.data = data;
        return res;
    }

    public static <T> Res<T> success(final T data, final String msgTemplate, final Object... args) {
        Res<T> res = new Res<>();
        res.setCode(Const.CODE_SUCCESS);
        res.data = data;
        res.setMessage(formatMessage(msgTemplate, Const.MSG_SUCCESS, args));
        return res;
    }

    public static <T> Res<T> successIfAffected(int affectedRows) {
        Res<T> res = new Res<>();
        if (affectedRows > 0) {
            res.setCode(Const.CODE_SUCCESS);
            res.setMessage("操作成功(影响行数:" + affectedRows + ")");
        } else {
            res.setCode(Const.CODE_FAIL);
            res.setMessage("操作失败(影响行数:" + affectedRows + ")");
        }
        return res;
    }

    public static <T> Res<T> fail(final String code, final String msgTemplate, final Object... args) {
        Res<T> res = new Res<>();
        res.setCode(code);
        res.setMessage(formatMessage(msgTemplate, Const.MSG_FAIL, args));
        return res;
    }

    public static <T> Res<T> fail(final String msgTemplate, final Object... args) {
        return fail(Const.CODE_FAIL, msgTemplate, args);
    }

    /**
     * 统一的消息格式化方法 - 简化版
     */
    private static String formatMessage(String template, String defaultMsg, Object... args) {
        // 模板为空，返回默认消息
        if (template == null || template.isEmpty()) {
            return defaultMsg;
        }

        // 没有参数，直接返回模板
        if (args == null || args.length == 0) {
            return template;
        }

        // 简单的循环替换 {}
        String result = template;
        for (Object arg : args) {
            // 每次替换第一个 {}
            result = result.replaceFirst("\\{\\}",
                    arg != null ? arg.toString() : "null");
        }
        return result;
    }

    private static class Const {
        public static final String MSG_SUCCESS = "操作成功";
        public static final String MSG_FAIL = "操作失败";
        public static final String CODE_SUCCESS = "SUCCESS";
        public static final String CODE_FAIL = "FAIL";
    }
}