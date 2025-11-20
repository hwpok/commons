package com.hwp.commons.time;


import com.hwp.commons.util.time.DateUtils;
import com.hwp.commons.util.time.WebTimeUtils;
import org.junit.jupiter.api.Test;

public class WebTimeUtilsTestCase {
    @Test
    public void testTime() {
        long time = WebTimeUtils.getCurrentTime();
        System.out.println(DateUtils.ofEpochMilli(time));
    }
}
