package com.hwp.commons.util.security;

import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * 生成随机验证码及图片工具类
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class CaptchaUtils {

    private static final char[] CHARS = {
            '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static final Random RAN = new Random();

    /**
     * 创建验证码及对应图片
     */
    public static Captcha createImage(Config c) {
        validateConfig(c);
        StringBuilder captcha = new StringBuilder();
        BufferedImage image = new BufferedImage(c.width, c.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphic = image.createGraphics();
        graphic.setColor(Color.LIGHT_GRAY);
        graphic.fillRect(0, 0, c.width, c.height);

        int maxCharIdx = c.isFullDigital() ? 10 : CHARS.length;
        for (int i = 0; i < c.getChars(); i++) {
            int n = RAN.nextInt(maxCharIdx);
            String temp = c.isFullLower() ? String.valueOf(CHARS[n]).toLowerCase() : String.valueOf(CHARS[n]);
            graphic.setColor(getRandomColor());
            Font font = new Font("Arial", Font.BOLD + Font.ITALIC, c.getFontSize());
            graphic.setFont(font);
            drawCenteredString(graphic, temp, i * c.width / c.getChars(), 0, c.width / c.getChars(), c.getHeight(), font);
            captcha.append(temp);
        }

        for (int i = 0; i < c.getLines(); i++) {
            graphic.setColor(getRandomColor());
            graphic.drawLine(RAN.nextInt(c.getWidth()), RAN.nextInt(c.getHeight()), RAN.nextInt(c.getWidth()), RAN.nextInt(c.getHeight()));
        }

        graphic.dispose();

        if (Config.ImageFormat.BUFFERED_IMAGE.equals(c.getImageType())) {
            return new Captcha(captcha.toString(), image);
        }
        return new Captcha(captcha.toString(), getBase64(image));
    }

    private static void validateConfig(Config config) {
        if (config.getWidth() <= 0 || config.getHeight() <= 0 || config.getChars() <= 0 || config.getFontSize() <= 0) {
            throw new IllegalArgumentException("Invalid config: width, height, chars and fontSize must be positive");
        }
    }

    private static Color getRandomColor() {
        return new Color(RAN.nextInt(256), RAN.nextInt(256), RAN.nextInt(256));
    }

    private static String getBase64(BufferedImage image) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", stream);
            return java.util.Base64.getEncoder().encodeToString(stream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to encode captcha image to Base64", e);
        }
    }

    public static String saveCaptchaImage(Config config, String fullFilePath) {
        try {
            Captcha captcha = createImage(config);
            File outputfile = new File(fullFilePath);
            ImageIO.write(captcha.getCaptchaImage(), "png", outputfile);
            return captcha.getCaptcha();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write captcha image to file", ex);
        }
    }

    public static Config getDefaultConfig() {
        return new Config();
    }

    public static Config getDefaultConfig(int width, int height) {
        Config config = new Config();
        config.setWidth(width);
        config.setHeight(height);
        return config;
    }

    @Getter
    public static class Captcha {
        private final String captcha;
        private final BufferedImage captchaImage;
        private final String captchaImageBase64;

        public Captcha(String captcha, BufferedImage captchaImage) {
            this.captcha = captcha;
            this.captchaImage = captchaImage;
            this.captchaImageBase64 = null;
        }

        public Captcha(String captcha, String captchaImageBase64) {
            this.captcha = captcha;
            this.captchaImage = null;
            this.captchaImageBase64 = captchaImageBase64;
        }

        // 辅助方法：判断类型
        public boolean isBase64() {
            return captchaImageBase64 != null;
        }

        public boolean isBufferedImage() {
            return captchaImage != null;
        }
    }

    @Getter
    @Setter
    public static class Config {
        public enum ImageFormat {
            BUFFERED_IMAGE,
            BASE64
        }

        private int width = 120;
        private int height = 44;
        private int chars = 4;
        private int lines = 5;
        private int fontSize = 36;
        private boolean fullDigital;
        private boolean fullLower;
        private ImageFormat imageType = ImageFormat.BUFFERED_IMAGE;
    }

    public static void drawCenteredString(Graphics g, String text, int x, int y, int w, int h, Font font) {
        FontMetrics fm = g.getFontMetrics(font);
        int xx = x + (w - fm.stringWidth(text)) / 2;
        int yy = y + (h - (fm.getAscent() + fm.getDescent())) / 2 + fm.getAscent();
        g.drawString(text, xx, yy);
    }
}