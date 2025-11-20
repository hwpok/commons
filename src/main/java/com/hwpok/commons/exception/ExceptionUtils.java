package com.hwpok.commons.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常加强
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public class ExceptionUtils {
    /**
     * 打印堆栈字符串
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
