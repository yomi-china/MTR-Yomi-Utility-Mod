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

public class DynamicFontTextureManager implements ResourceManagerReloadListener {
    private static DynamicFontTextureManager instance;
    private final Map<String, FontTexture> textureCache = new HashMap<>();
    private Font mitsubishiFont = null;
    private boolean initialized = false;

    public static DynamicFontTextureManager getInstance() {
        if (instance == null) {
            instance = new DynamicFontTextureManager();
        }
        return instance;
    }

    public synchronized void initialize() {
        if (initialized) return;

        try {
            Mtryum.LOGGER.info("Initializing custom fonts...");

            String fontPath = "assets/mtryum/fonts/mitsubishi-modern.ttf";
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream(fontPath);

            if (fontStream != null) {
                mitsubishiFont = Font.createFont(Font.TRUETYPE_FONT, fontStream)
                        .deriveFont(Font.PLAIN, 80);
                fontStream.close();

                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(mitsubishiFont);
                Mtryum.LOGGER.info("Font loaded and registered successfully");
            } else {
                Mtryum.LOGGER.error("Font file not found at: " + fontPath);
                fontPath = "mitsubishi-modern.ttf";
                fontStream = getClass().getClassLoader().getResourceAsStream(fontPath);

                if (fontStream != null) {
                    mitsubishiFont = Font.createFont(Font.TRUETYPE_FONT, fontStream)
                            .deriveFont(Font.PLAIN, 80);
                    fontStream.close();
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(mitsubishiFont);
                    Mtryum.LOGGER.info("Font loaded from alternate path");
                } else {
                    throw new RuntimeException("Failed to load fonts from any path");
                }
            }
        } catch (Exception e) {
            Mtryum.LOGGER.error("Failed to load custom fonts: " + e.getMessage() + ".Using fallback font...");
            Mtryum.LOGGER.error("s");
            mitsubishiFont = new Font("SansSerif", Font.PLAIN, 80);
        } finally {
            initialized = true;
            Mtryum.LOGGER.info("Font initialization complete!");
        }
    }

    private synchronized void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }

    public FontTexture getStringTexture(String text, int color) {
        ensureInitialized();

        String key = text;

        if (textureCache.containsKey(key)) {
            return textureCache.get(key);
        }

        FontTexture texture = generateStringTexture(text, color);
        if (texture != null) {
            textureCache.put(key, texture);
        }
        return texture;
    }

    private FontTexture generateStringTexture(String text, int color) {
        try {
            if (mitsubishiFont == null) {
                ensureInitialized();
                if (mitsubishiFont == null) {
                    mitsubishiFont = new Font("SansSerif", Font.PLAIN, 80);
                }
            }

            BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = tempImg.createGraphics();
            g2d.setFont(mitsubishiFont);
            FontRenderContext frc = g2d.getFontRenderContext();

            if (text == null || text.isEmpty()) {
                text = " ";
            }
            Rectangle2D bounds = mitsubishiFont.getStringBounds(text, frc);
            int width = (int) Math.ceil(bounds.getWidth());
            int height = (int) Math.ceil(bounds.getHeight());

            // 确保最小尺寸
            width = Math.max(width, 1);
            height = Math.max(height, 1);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.setColor(new Color(255, 255, 255, 255));
            graphics.setFont(mitsubishiFont);

            // 绘制文本
            graphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
            );
            graphics.setRenderingHint(
                    RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON
            );

            FontMetrics metrics = graphics.getFontMetrics();
            int y = metrics.getAscent() - metrics.getDescent();
            graphics.drawString(text, 0, y);

            // 解决字体边缘问题
            if (width > 0 && height > 0) {
                for (int py = 0; py < height; py++) {
                    for (int px = 0; px < width; px++) {
                        int pixel = bufferedImage.getRGB(px, py);
                        int alpha = (pixel >> 24) & 0xFF;
                        if (alpha > 0 && alpha < 255) {
                            bufferedImage.setRGB(px, py, (alpha << 24) | 0x00FFFFFF);
                        }
                    }
                }
            }

            graphics.dispose();
            g2d.dispose();

            NativeImage nativeImage = convertToNativeImage(bufferedImage);

            return new FontTexture(nativeImage, width, height);
        } catch (Exception e) {
            Mtryum.LOGGER.error("Generating texture for text: " + text);
            e.printStackTrace();
            return null;
        }
    }

    private NativeImage convertToNativeImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        NativeImage nativeImage = new NativeImage(
                NativeImage.Format.RGBA,
                width,
                height,
                true
        );

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = bufferedImage.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                int whiteWithAlpha = (a << 24) | 0x00FFFFFF; // ARGB: A + 白色
                nativeImage.setPixelRGBA(x, y, whiteWithAlpha);
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
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Mtryum.LOGGER.info("Clearing fonts texture cache...");
        clearCache();
    }

    // 纹理包装类
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