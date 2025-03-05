package io.github.dimaskama.visualkeys.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.dimaskama.visualkeys.client.VisualKeys;
import io.github.dimaskama.visualkeys.duck.ControlsListWidgetDuck;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeybindsScreen.class)
abstract class KeybindsScreenMixin extends GameOptionsScreen {

    @Shadow private ControlsListWidget controlsList;

    private KeybindsScreenMixin() {
        super(null, null, null);
        throw new AssertionError();
    }

    @Override
    protected void initHeader() {
        layout.setHeaderHeight(layout.getHeaderHeight() + 10);
        layout.addHeader(new TextWidget(title, textRenderer), positioner -> positioner
                .alignHorizontalCenter()
                .relativeY(0.15F));
        Text searchText = Text.translatable("visualkeys.search_keybinds").formatted(Formatting.GRAY);
        TextFieldWidget searchField = new TextFieldWidget(textRenderer, 200, 20, searchText);
        searchField.setPlaceholder(searchText);
        searchField.setChangedListener(string -> {
            ((ControlsListWidgetDuck) controlsList).visualkeys_setSearchInput(string);
            ((ControlsListWidgetDuck) controlsList).visualkeys_refill();
        });
        layout.addHeader(searchField, positioner -> positioner
                .alignHorizontalCenter()
                .relativeY(0.75F));
    }

    @Inject(method = "initFooter", at = @At("TAIL"))
    private void initFooterTail(CallbackInfo ci, @Local DirectionalLayoutWidget layout) {
        layout.add(VisualKeys.createOpenKeyboardButton(client, this));
    }

    @Override
    public void removed() {
        super.removed();
        VisualKeys.CONFIG.saveIfDirty();
    }

}
