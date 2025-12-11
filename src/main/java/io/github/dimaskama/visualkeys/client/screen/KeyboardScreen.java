package io.github.dimaskama.visualkeys.client.screen;

import io.github.dimaskama.visualkeys.client.KeyEntry;
import io.github.dimaskama.visualkeys.client.KeyboardRenderOptions;
import io.github.dimaskama.visualkeys.client.KeyboardRenderer;
import io.github.dimaskama.visualkeys.client.VisualKeys;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class KeyboardScreen extends Screen {
    private static final KeyboardRenderOptions RENDER_OPTIONS = new KeyboardRenderOptions(
            8, 32,
            0.25F,
            true, true, true
    );
    @Nullable
    private final Screen parent;

    public KeyboardScreen(@Nullable Screen parent) {
        super(Component.translatable("visualkeys.keyboard"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button ->
                onClose()).bounds((width - 150) >> 1, height - 40, 120, 20).build());
        RENDER_OPTIONS.keyboardScale = (width - 16) / 2250.0F;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Iterable<KeyEntry> keys = VisualKeys.QWERTY.map.values();
        KeyboardRenderer.render(context, keys, RENDER_OPTIONS);
        KeyboardRenderer.renderMouseOverlay(context, keys, RENDER_OPTIONS, mouseX, mouseY);
        context.drawCenteredString(font, title, width >>> 1, 8, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
