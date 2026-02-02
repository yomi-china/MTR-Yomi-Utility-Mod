package com.yomi.mtryum.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.yomi.mtryum.Mtryum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class OptimizedFontRenderer {
    private static OptimizedFontRenderer instance;
    private ResourceLocation fontAtlas;
    private final Map<Character, CharInfo> charMap = new HashMap<>();

    private static final String CHAR_SET =
            "0123456789" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz";

    public static OptimizedFontRenderer getInstance() {
        if (instance == null) {
            instance = new OptimizedFontRenderer();
        }
        return instance;
    }

    public void initialize() {
        if (fontAtlas != null) return;

        try {
            generateFontAtlas();
            Mtryum.LOGGER.info("Font atlas initialized with {} characters", charMap.size());
        } catch (Exception e) {
            Mtryum.LOGGER.error("Failed to initialize font atlas: {}", e.getMessage());
        }
    }

    private void generateFontAtlas() {
        DynamicFontTextureManager fontManager = DynamicFontTextureManager.getInstance();
        fontManager.initialize();

        int atlasWidth = 1024;
        int atlasHeight = 1024;
        NativeImage atlasImage = new NativeImage(
                NativeImage.Format.RGBA,
                atlasWidth,
                atlasHeight,
                false
        );

        for (int y = 0; y < atlasHeight; y++) {
            for (int x = 0; x < atlasWidth; x++) {
                atlasImage.setPixelRGBA(x, y, 0);
            }
        }

        int currentX = 0;
        int currentY = 0;
        int maxRowHeight = 0;

        for (char c : CHAR_SET.toCharArray()) {
            DynamicFontTextureManager.FontTexture charTexture =
                    fontManager.getStringTexture(String.valueOf(c), 0xFFFFFFFF);

            if (charTexture != null && charTexture.getImage() != null) {
                NativeImage charImage = charTexture.getImage();
                int charW = charTexture.getWidth();
                int charH = charTexture.getHeight();

                // 检查是否需要换行
                if (currentX + charW > atlasWidth) {
                    currentX = 0;
                    currentY += maxRowHeight + 1;
                    maxRowHeight = 0;
                }

                for (int y = 0; y < charH; y++) {
                    for (int x = 0; x < charW; x++) {
                        int pixel = charImage.getPixelRGBA(x, y);
                        atlasImage.setPixelRGBA(currentX + x, currentY + y, pixel);
                    }
                }

                float u1 = (float) currentX / atlasWidth;
                float v1 = (float) currentY / atlasHeight;
                float u2 = (float) (currentX + charW) / atlasWidth;
                float v2 = (float) (currentY + charH) / atlasHeight;

                charMap.put(c, new CharInfo(u1, v1, u2, v2, charW, charH));

                currentX += charW + 1;
                maxRowHeight = Math.max(maxRowHeight, charH);

                charTexture.close();
            }
        }

        DynamicTexture texture = new DynamicTexture(atlasImage);
        fontAtlas = Minecraft.getInstance()
                .getTextureManager()
                .register("font_atlas_" + System.currentTimeMillis(), texture);

        atlasImage.close();
    }

    public static void renderText(
            PoseStack poseStack,
            MultiBufferSource buffer,
            String text,
            int color,
            float x, float y, float z,
            float scale,
            int light,
            boolean centered // 居中
    ) {
        if (text == null || text.isEmpty()) return;

        OptimizedFontRenderer renderer = getInstance();
        if (renderer.fontAtlas == null) {
            renderer.initialize();
        }

        int alpha = (color >>> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        if (alpha == 0) alpha = 255;

        // 计算文本总宽度
        float totalWidth = 0;
        float spacingFactor = 0.8f;

        for (char c : text.toCharArray()) {
            CharInfo info = renderer.charMap.get(c);
            if (info == null) continue;
            totalWidth += info.width * scale * spacingFactor;
        }

        // 调整起始位置
        float currentX = centered ? x - totalWidth / 2 : x;

        // 设置渲染状态
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, renderer.fontAtlas);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.text(renderer.fontAtlas));

        // 渲染
        for (char c : text.toCharArray()) {
            CharInfo info = renderer.charMap.get(c);
            if (info == null) continue;

            float charWidth = info.width * scale;
            float charHeight = info.height * scale;

            // 单个字符
            vertexConsumer.vertex(matrix, currentX, y + charHeight, z)
                    .color(red, green, blue, alpha)
                    .uv(info.u1, info.v2)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(light)
                    .endVertex();

            vertexConsumer.vertex(matrix, currentX + charWidth, y + charHeight, z)
                    .color(red, green, blue, alpha)
                    .uv(info.u2, info.v2)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(light)
                    .endVertex();

            vertexConsumer.vertex(matrix, currentX + charWidth, y, z)
                    .color(red, green, blue, alpha)
                    .uv(info.u2, info.v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(light)
                    .endVertex();

            vertexConsumer.vertex(matrix, currentX, y, z)
                    .color(red, green, blue, alpha)
                    .uv(info.u1, info.v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(light)
                    .endVertex();

            currentX += charWidth * spacingFactor; // 字符间距
        }
    }

    private static class CharInfo {
        final float u1, v1, u2, v2;
        final int width, height;

        CharInfo(float u1, float v1, float u2, float v2, int width, int height) {
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
            this.width = width;
            this.height = height;
        }
    }
}