package io.github.dimaskama.visualkeys.client.screen;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import io.github.dimaskama.visualkeys.config.ModConfig;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ModConfigScreen extends Screen {
    private final Screen parent;

    public ModConfigScreen(Screen parent) {
        super(Component.translatable(VisualKeys.MOD_ID));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = 160;
        int leftX = (width - w - 10 - w) >> 1;
        int rightX = leftX + w + 10;
        ModConfig.Data data = VisualKeys.CONFIG.getData();
        addRenderableWidget(new BoolButton(leftX, 50, w, 20, "visualkeys.enabled", () -> data.enabled, b -> data.enabled = b));
        addRenderableWidget(new BoolButton(rightX, 50, w, 20, "visualkeys.save_enabled_state", () -> data.saveEnabledState, b -> data.saveEnabledState = b));
        addRenderableWidget(new Slider(leftX, 75, w, 20, "visualkeys.keyboard_x", data.hudRenderOptions.keyboardX, width, f -> data.hudRenderOptions.keyboardX = (int) f));
        addRenderableWidget(new Slider(rightX, 75, w, 20, "visualkeys.keyboard_y", data.hudRenderOptions.keyboardY, height, f -> data.hudRenderOptions.keyboardY = (int) f));
        addRenderableWidget(new Slider(leftX, 100, w, 20, "visualkeys.keyboard_scale", data.hudRenderOptions.keyboardScale, 1.0, f -> data.hudRenderOptions.keyboardScale = f));
        addRenderableWidget(new BoolButton(rightX, 100, w, 20, "visualkeys.function_keys_visible", () -> data.hudRenderOptions.functionVisible, b -> data.hudRenderOptions.functionVisible = b));
        addRenderableWidget(new BoolButton(leftX, 125, w, 20, "visualkeys.mid_keys_visible", () -> data.hudRenderOptions.midVisible, b -> data.hudRenderOptions.midVisible = b));
        addRenderableWidget(new BoolButton(rightX, 125, w, 20, "visualkeys.numpad_keys_visible", () -> data.hudRenderOptions.numpadVisible, b -> data.hudRenderOptions.numpadVisible = b));
        addRenderableWidget(new BoolButton(leftX, 150, w, 20, "visualkeys.keyboard_textured", () -> data.keyboardTextured, b -> data.keyboardTextured = b));
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose()).bounds((width - w) >> 1, height - 50, w, 20).build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(font, title, width >>> 1, 20, 0xFFFFFFFF);
    }

    @Override
    public void removed() {
        VisualKeys.CONFIG.saveIfDirty();
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private static class Slider extends AbstractSliderButton {
        private final String translationKey;
        private final double max;
        private final FloatConsumer consumer;
        private float exactValue;

        public Slider(int x, int y, int width, int height, String translationKey, double value, double max, FloatConsumer consumer) {
            super(x, y, width, height, CommonComponents.EMPTY, value / max);
            this.translationKey = translationKey;
            this.max = max;
            this.consumer = consumer;
            applyValue();
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable(translationKey, String.format("%.1f", exactValue)));
        }

        @Override
        protected void applyValue() {
            exactValue = (float) (value * max);
            consumer.accept(exactValue);
            VisualKeys.CONFIG.markDirty();
        }
    }

    private static class BoolButton extends AbstractButton {
        private final String translationKey;
        private final BooleanSupplier supplier;
        private final BooleanConsumer consumer;

        public BoolButton(int x, int y, int width, int height, String translationKey, BooleanSupplier supplier, BooleanConsumer consumer) {
            super(x, y, width, height, CommonComponents.EMPTY);
            this.translationKey = translationKey;
            this.supplier = supplier;
            this.consumer = consumer;
            updateMessage();
        }

        @Override
        public void onPress(InputWithModifiers input) {
            consumer.accept(!supplier.getAsBoolean());
            updateMessage();
            VisualKeys.CONFIG.markDirty();
        }

        @Override
        protected void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
            renderDefaultSprite(guiGraphics);
            renderDefaultLabel(guiGraphics.textRenderer());
        }

        private void updateMessage() {
            setMessage(Component.translatable(translationKey, supplier.getAsBoolean() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput builder) {
            defaultButtonNarrationText(builder);
        }
    }
}
