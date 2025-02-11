package io.github.dimaskama.visualkeys.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeyboardRenderer {
    private static final Identifier KEYS_TEXTURE = Identifier.of(VisualKeys.MOD_ID, "textures/gui/key_buttons.png");

    public static void render(DrawContext context, Iterable<KeyEntry> keys, KeyboardRenderOptions options) {
        int texPadding = 2;
        int padding = 5;
        int x = options.keyboardX;
        int y = options.keyboardY;
        float scale = options.keyboardScale;

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);

        // Rectangles
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        if (VisualKeys.CONFIG.getData().keyboardTextured) {
            RenderSystem.setShaderTexture(0, KEYS_TEXTURE);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
            BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            for (KeyEntry key : keys) {
                if (!key.type.isVisible(options)) continue;
                int code = key.code;
                boolean pressed = code >= 0 && InputUtil.isKeyPressed(windowHandle, code);
                int w = key.width;
                int h = key.height;
                float x1 = (key.getX(options) + texPadding) * scale;
                float y1 = (key.getY(options) + texPadding) * scale;
                float x2 = x1 + (w - (texPadding << 1)) * scale;
                float y2 = y1 + (h - (texPadding << 1)) * scale;
                float u1 = pressed ? w * 0.00078125F : 0.0F;
                float v1 = (h == 200 ? 160.0F : switch (w) {
                    default: yield 0.0F;
                    case 125: yield 20.0F;
                    case 150: yield 40.0F;
                    case 175: yield 60.0F;
                    case 200: yield 80.0F;
                    case 225: yield 100.0F;
                    case 275: yield 120.0F;
                    case 625: yield 140.0F;
                }) * 0.00390625F;
                float u2 = u1 + w * 0.00078125F;
                float v2 = v1 + h * 0.00078125F;
                builder.vertex(matrix, x1, y1, 0.0F).texture(u1, v1);
                builder.vertex(matrix, x1, y2, 0.0F).texture(u1, v2);
                builder.vertex(matrix, x2, y2, 0.0F).texture(u2, v2);
                builder.vertex(matrix, x2, y1, 0.0F).texture(u2, v1);
            }
            BufferRenderer.drawWithGlobalProgram(builder.end());
        } else {
            context.draw(consumerProvider -> {
                VertexConsumer colorConsumer = consumerProvider.getBuffer(RenderLayer.getGui());
                for (KeyEntry key : keys) {
                    if (!key.type.isVisible(options)) continue;
                    int code = key.code;
                    boolean pressed = code >= 0 && InputUtil.isKeyPressed(windowHandle, code);
                    float x1 = (key.getX(options) + padding) * scale;
                    float y1 = (key.getY(options) + padding) * scale;
                    float x2 = x1 + (key.width - (padding << 1)) * scale;
                    float y2 = y1 + (key.height - (padding << 1)) * scale;
                    float r = pressed ? 0.5F : 0.2F;
                    float g = pressed ? 0.5F : 0.2F;
                    float b = 0.7F;
                    float a = pressed ? 0.6F : 1.0F;
                    colorConsumer.vertex(matrix, x1, y1, 0).color(r, g, b, a);
                    colorConsumer.vertex(matrix, x1, y2, 0).color(r, g, b, a);
                    colorConsumer.vertex(matrix, x2, y2, 0).color(r, g, b, a);
                    colorConsumer.vertex(matrix, x2, y1, 0).color(r, g, b, a);
                }
            });
        }

        // Text
        float textScale = scale * 2.0F;
        context.getMatrices().scale(textScale, textScale, textScale);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        long time = Util.getMeasuringTimeMs();
        for (KeyEntry key : keys) {
            if (!key.type.isVisible(options)) continue;
            int sX = (key.getX(options) + padding) >> 1;
            int sY = (key.getY(options) + padding) >> 1;
            context.drawTextWithShadow(textRenderer, key.name, sX + 5, sY + 4, 0xFFFFFFFF);
            if (key.firstBindText == null) continue;
            int sW = (key.width - (padding << 1)) >> 1;
            int sH = (key.height - (padding << 1)) >> 1;
            renderScrollingText(context, textRenderer, key.firstBindText, sX + 5, sY + 15, sW - 8, sH - 20, time, x, y, textScale);
        }

        context.getMatrices().pop();
    }

    public static void renderMouseOverlay(DrawContext context, Iterable<KeyEntry> keys, KeyboardRenderOptions options, int mouseX, int mouseY) {
        int mX = (int) ((mouseX - options.keyboardX) / options.keyboardScale);
        int mY = (int) ((mouseY - options.keyboardY) / options.keyboardScale);
        if (mX < 0 || mY < 0 || mX >= options.getCurrentWidth() || mY >= options.getCurrentHeight()) {
            return;
        }
        for (KeyEntry key : keys) {
            ArrayList<KeyEntry.Bind> binds = key.binds;
            if (binds.isEmpty()) continue;
            int x = key.getX(options);
            int y = key.getY(options);
            if (mX < x || mY < y || mX >= x + key.width || mY >= y + key.height) continue;

            context.drawTooltip(
                    MinecraftClient.getInstance().textRenderer,
                    Objects.requireNonNullElseGet(
                            key.tooltipTexts,
                            () -> key.tooltipTexts = binds.stream().map(KeyEntry.Bind::textWithCategory).toList()
                    ),
                    mouseX,
                    mouseY
            );
            break;
        }
    }

    private static void renderScrollingText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int width, int height, long time, int tX, int tY, float tS) {
        int scrollTime = 1000;

        int textWidth = textRenderer.getWidth(text);
        if (textWidth > width) {
            List<OrderedText> list = textRenderer.wrapLines(text, width);
            int h = list.size() * 10 - 1;
            boolean scissor = h > height;
            if (scissor) {
                context.enableScissor(
                        (int) (x * tS) + tX, (int) (y * tS) + tY, (int) ((x + width) * tS) + tX + 1, (int) ((y + height) * tS) + tY
                );
                int mod = (int) (time % (scrollTime << 2));
                int maxScroll = h - height;
                int scroll = mod < scrollTime
                        ? 0
                        : mod < (scrollTime << 1)
                                ? (int) ((float) (mod - scrollTime) / scrollTime * maxScroll)
                                : mod < (scrollTime << 1) + scrollTime
                                        ? maxScroll
                                        : (int) ((float) ((scrollTime << 2) - mod) / scrollTime * maxScroll);
                y -= scroll;
            } else {
                y += height - h;
            }
            for (OrderedText t : list) {
                context.drawTextWithShadow(textRenderer, t, x + (width - textRenderer.getWidth(t)), y, 0xFFFFFFFF);
                y += 10;
            }
            if (scissor) {
                context.disableScissor();
            }
        } else {
            context.drawTextWithShadow(textRenderer, text, x + (width - textWidth), y + height - 9, 0xFFFFFFFF);
        }
    }
}
