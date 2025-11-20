package com.hwpok.commons.util.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;


public class CaptchaGeneratorTestCase {
    @TempDir
    Path tempDir;
    @Test
    void shouldGenerateValidCaptchaWithDefaultConfig() {
        var config = CaptchaUtils.getDefaultConfig();
        config.setImageType(CaptchaUtils.Config.ImageFormat.BASE64);
        var captcha = CaptchaUtils.createImage(config);
        System.out.println(captcha.getCaptchaImageBase64());
    }
}
