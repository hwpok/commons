package com.hwpok.commons.time;


import com.hwpok.commons.util.time.DateUtils;
import com.hwpok.commons.util.time.WebTimeUtils;
import org.junit.jupiter.api.Test;

public class WebTimeUtilsTestCase {
    @Test
    public void testTime() {
        long time = WebTimeUtils.getCurrentTime();
        System.out.println(DateUtils.ofEpochMilli(time));
    }
}
