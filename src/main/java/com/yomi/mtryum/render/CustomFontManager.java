package com.yomi.mtryum.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.yomi.mtryum.Mtryum;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CustomFontManager implements ResourceManagerReloadListener {
    private static CustomFontManager instance;
    private final Map<String, FontTexture> textureCache = new HashMap<>();
    private final Map<String, Font> fontCache = new HashMap<>();
    private boolean initialized = false;

    public static CustomFontManager getInstance() {
        if (instance == null) {
            instance = new CustomFontManager();
        }
        return instance;
    }

    public synchronized void initialize() {
        if (initialized) return;

        try {
            Mtryum.LOGGER.info("Initializing custom fonts...");

            // 加载字体
            loadFont("mitsubishi-modern", "assets/mtryum/fonts/mitsubishi-modern.ttf", 80);
            loadFont("old-thyssenkrupp", "assets/mtryum/fonts/old-thyssenkrupp.ttf", 60);
            loadFont("otis-series-1", "assets/mtryum/fonts/otis-series-1.ttf", 60);

            Mtryum.LOGGER.info("Font initialization complete! Loaded {} fonts", fontCache.size());
        } catch (Exception e) {
            Mtryum.LOGGER.error("Failed to initialize fonts: {}", e.getMessage());
            Mtryum.LOGGER.error("Using fallback font...");
            fontCache.put("default", new Font("SansSerif", Font.PLAIN, 72));
        } finally {
            initialized = true;
        }
    }

    /**
     * 加载自定义字体
     * @param fontName 字体名称
     * @param fontPath 字体文件路径
     * @param baseSize 基础字体大小
     */
    public synchronized void loadFont(String fontName, String fontPath, int baseSize) {
        try {
            Font font;

            if (fontPath != null) {
                InputStream fontStream = getClass().getClassLoader().getResourceAsStream(fontPath);
                if (fontStream == null) {
                    // 尝试备用路径
                    fontStream = getClass().getClassLoader().getResourceAsStream(fontPath.substring(fontPath.lastIndexOf('/') + 1));
                }

                if (fontStream != null) {
                    font = Font.createFont(Font.TRUETYPE_FONT, fontStream)
                            .deriveFont(Font.PLAIN, baseSize);
                    fontStream.close();

                    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    ge.registerFont(font);
                    Mtryum.LOGGER.info("Loaded custom font: {} from {}", fontName, fontPath);
                } else {
                    throw new RuntimeException("Font file not found: " + fontPath);
                }
            } else {
                // 使用系统字体
                font = new Font(fontName, Font.PLAIN, baseSize);
                Mtryum.LOGGER.info("Using system font: {}", fontName);
            }

            fontCache.put(fontName, font);
        } catch (Exception e) {
            Mtryum.LOGGER.error("Failed to load font {}: {}", fontName, e.getMessage());
            // 回退
            fontCache.put(fontName, new Font("SansSerif", Font.PLAIN, baseSize));
        }
    }

    public Font getFont(String fontName) {
        ensureInitialized();
        return fontCache.getOrDefault(fontName, fontCache.get("default"));
    }

    private synchronized void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }

    public FontTexture getStringTexture(String text, int color, String fontName) {
        ensureInitialized();

        String key = fontName + ":" + text + ":" + color;

        if (textureCache.containsKey(key)) {
            return textureCache.get(key);
        }

        FontTexture texture = generateStringTexture(text, color, fontName);
        if (texture != null) {
            textureCache.put(key, texture);
        }
        return texture;
    }

    private FontTexture generateStringTexture(String text, int color, String fontName) {
        try {
            Font font = getFont(fontName);
            if (font == null) {
                Mtryum.LOGGER.warn("Font {} not found, using default", fontName);
                font = getFont("default");
            }

            if (text == null || text.isEmpty()) {
                text = " ";
            }

            BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = tempImg.createGraphics();
            g2d.setFont(font);
            FontRenderContext frc = g2d.getFontRenderContext();

            Rectangle2D bounds = font.getStringBounds(text, frc);
            FontMetrics metrics = g2d.getFontMetrics();

            int ascent = metrics.getAscent();
            int descent = metrics.getDescent();
            int leading = metrics.getLeading();

            int width = (int) Math.ceil(bounds.getWidth());
            int height = Math.max(ascent + descent, (int) Math.ceil(bounds.getHeight()));

            //不知道为什么这个字体高一点
            if (fontName.contains("old-thyssenkrupp")) {
                height = Math.max(height, ascent + descent + leading + 4);
                width = Math.max(width, metrics.stringWidth(text) + 4);
            }

            // 确保最小尺寸
            width = Math.max(width, 1);
            height = Math.max(height, 1);

            // 创建图像
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = bufferedImage.createGraphics();

            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 设置颜色
            Color textColor = new Color(color, true);
            graphics.setColor(textColor);
            graphics.setFont(font);

            // 绘制文本
            graphics.drawString(text, 0, ascent);

            graphics.dispose();
            g2d.dispose();

            NativeImage nativeImage = convertToNativeImage(bufferedImage, color);
            return new FontTexture(nativeImage, width, height);
        } catch (Exception e) {
            Mtryum.LOGGER.error("Error generating texture for text '{}' with font {}: {}", text, fontName, e.getMessage());
            return null;
        }
    }

    private NativeImage convertToNativeImage(BufferedImage bufferedImage, int color) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, true);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = bufferedImage.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;

                int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                nativeImage.setPixelRGBA(x, y, rgba);
            }
        }

        return nativeImage;
    }

    public void clearCache() {
        for (FontTexture texture : textureCache.values()) {
            if (texture != null) {
                texture.close();
            }
        }
        textureCache.clear();
        Mtryum.LOGGER.info("Font texture cache cleared");
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Mtryum.LOGGER.info("Resource reload detected, clearing font texture cache...");
        clearCache();
    }

    public static class FontTexture {
        private final NativeImage image;
        private final int width;
        private final int height;

        public FontTexture(NativeImage image, int width, int height) {
            this.image = image;
            this.width = width;
            this.height = height;
        }

        public NativeImage getImage() { return image; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }

        public void close() {
            if (image != null) {
                image.close();
            }
        }
    }
}