package io.github.dimaskama.visualkeys.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

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

        context.getMatrices().pushMatrix();
        context.getMatrices().translate(x, y);

        // Rectangles
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        if (VisualKeys.CONFIG.getData().keyboardTextured) {
            for (KeyEntry key : keys) {
                if (!key.type.isVisible(options)) continue;
                int code = key.code;
                boolean pressed = code >= 0 && InputUtil.isKeyPressed(windowHandle, code);
                int w = key.width;
                int h = key.height;
                float kX1 = (key.getX(options) + texPadding) * scale;
                float kY1 = (key.getY(options) + texPadding) * scale;
                float kW = (w - (texPadding << 1)) * scale;
                float kH = (h - (texPadding << 1)) * scale;
                float u = pressed ? w : 0.0F;
                float v = (h == 200 ? 160.0F : switch (w) {
                    case 125: yield 20.0F;
                    case 150: yield 40.0F;
                    case 175: yield 60.0F;
                    case 200: yield 80.0F;
                    case 225: yield 100.0F;
                    case 275: yield 120.0F;
                    case 625: yield 140.0F;
                    default: yield 0.0F;
                });
                context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        KEYS_TEXTURE,
                        (int) kX1,
                        (int) kY1,
                        u,
                        v,
                        (int) kW,
                        (int) kH,
                        (int) (0.2F * w),
                        (int) (0.2F * h),
                        256,
                        256
                );
            }
        } else {
            for (KeyEntry key : keys) {
                if (!key.type.isVisible(options)) continue;
                int code = key.code;
                boolean pressed = code >= 0 && InputUtil.isKeyPressed(windowHandle, code);
                float kX1 = (key.getX(options) + padding) * scale;
                float kY1 = (key.getY(options) + padding) * scale;
                float kX2 = kX1 + (key.width - (padding << 1)) * scale;
                float kY2 = kY1 + (key.height - (padding << 1)) * scale;
                context.fill((int) kX1, (int) kY1, (int) kX2, (int) kY2, pressed ? 0x997F7FB2 : 0xFF3333B2);
            }
        }

        // Text
        float textScale = scale * 2.0F;
        context.getMatrices().scale(textScale, textScale);

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
            renderScrollingText(context, textRenderer, key.firstBindText, sX + 5, sY + 15, sW - 8, sH - 20, time);
        }

        context.getMatrices().popMatrix();
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

    private static void renderScrollingText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int width, int height, long time) {
        int scrollTime = 1000;

        int textWidth = textRenderer.getWidth(text);
        if (textWidth > width) {
            List<OrderedText> list = textRenderer.wrapLines(text, width);
            int h = list.size() * 10 - 1;
            boolean scissor = h > height;
            if (scissor) {
                context.enableScissor(
                        x, y, x + width + 2, y + height + 2
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
