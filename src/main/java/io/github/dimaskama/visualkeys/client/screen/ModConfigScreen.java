package io.github.dimaskama.visualkeys.client.screen;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import io.github.dimaskama.visualkeys.config.ModConfig;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;

public class ModConfigScreen extends Screen {
    private final Screen parent;

    public ModConfigScreen(Screen parent) {
        super(Text.translatable(VisualKeys.MOD_ID));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = 160;
        int leftX = (width - w - 10 - w) >> 1;
        int rightX = leftX + w + 10;
        ModConfig.Data data = VisualKeys.CONFIG.getData();
        addDrawableChild(new BoolButton(leftX, 50, w, 20, "visualkeys.enabled", () -> data.enabled, b -> data.enabled = b));
        addDrawableChild(new BoolButton(rightX, 50, w, 20, "visualkeys.save_enabled_state", () -> data.saveEnabledState, b -> data.saveEnabledState = b));
        addDrawableChild(new Slider(leftX, 75, w, 20, "visualkeys.keyboard_x", data.hudRenderOptions.keyboardX, width, f -> data.hudRenderOptions.keyboardX = (int) f));
        addDrawableChild(new Slider(rightX, 75, w, 20, "visualkeys.keyboard_y", data.hudRenderOptions.keyboardY, height, f -> data.hudRenderOptions.keyboardY = (int) f));
        addDrawableChild(new Slider(leftX, 100, w, 20, "visualkeys.keyboard_scale", data.hudRenderOptions.keyboardScale, 1.0, f -> data.hudRenderOptions.keyboardScale = f));
        addDrawableChild(new BoolButton(rightX, 100, w, 20, "visualkeys.function_keys_visible", () -> data.hudRenderOptions.functionVisible, b -> data.hudRenderOptions.functionVisible = b));
        addDrawableChild(new BoolButton(leftX, 125, w, 20, "visualkeys.mid_keys_visible", () -> data.hudRenderOptions.midVisible, b -> data.hudRenderOptions.midVisible = b));
        addDrawableChild(new BoolButton(rightX, 125, w, 20, "visualkeys.numpad_keys_visible", () -> data.hudRenderOptions.numpadVisible, b -> data.hudRenderOptions.numpadVisible = b));
        addDrawableChild(new BoolButton(leftX, 150, w, 20, "visualkeys.keyboard_textured", () -> data.keyboardTextured, b -> data.keyboardTextured = b));
        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, b -> close()).dimensions((width - w) >> 1, height - 50, w, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width >>> 1, 20, 0xFFFFFFFF);
    }

    @Override
    public void removed() {
        VisualKeys.CONFIG.saveIfDirty();
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    private static class Slider extends SliderWidget {
        private final String translationKey;
        private final double max;
        private final FloatConsumer consumer;
        private float exactValue;

        public Slider(int x, int y, int width, int height, String translationKey, double value, double max, FloatConsumer consumer) {
            super(x, y, width, height, ScreenTexts.EMPTY, value / max);
            this.translationKey = translationKey;
            this.max = max;
            this.consumer = consumer;
            applyValue();
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.translatable(translationKey, String.format("%.1f", exactValue)));
        }

        @Override
        protected void applyValue() {
            exactValue = (float) (value * max);
            consumer.accept(exactValue);
            VisualKeys.CONFIG.markDirty();
        }
    }

    private static class BoolButton extends PressableWidget {
        private final String translationKey;
        private final BooleanSupplier supplier;
        private final BooleanConsumer consumer;

        public BoolButton(int x, int y, int width, int height, String translationKey, BooleanSupplier supplier, BooleanConsumer consumer) {
            super(x, y, width, height, ScreenTexts.EMPTY);
            this.translationKey = translationKey;
            this.supplier = supplier;
            this.consumer = consumer;
            updateMessage();
        }

        @Override
        public void onPress(AbstractInput input) {
            consumer.accept(!supplier.getAsBoolean());
            updateMessage();
            VisualKeys.CONFIG.markDirty();
        }

        private void updateMessage() {
            setMessage(Text.translatable(translationKey, supplier.getAsBoolean() ? ScreenTexts.ON : ScreenTexts.OFF));
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            appendDefaultNarrations(builder);
        }
    }
}
