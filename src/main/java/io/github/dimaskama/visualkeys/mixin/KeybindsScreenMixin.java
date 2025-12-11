package io.github.dimaskama.visualkeys.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.dimaskama.visualkeys.client.VisualKeys;
import io.github.dimaskama.visualkeys.duck.ControlsListWidgetDuck;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsScreen.class)
abstract class KeybindsScreenMixin extends OptionsSubScreen {

    @Shadow private KeyBindsList keyBindsList;

    private KeybindsScreenMixin() {
        super(null, null, null);
        throw new AssertionError();
    }

    @Override
    protected void addTitle() {
        layout.setHeaderHeight(layout.getHeaderHeight() + 10);
        layout.addToHeader(new StringWidget(title, font), positioner -> positioner
                .alignHorizontallyCenter()
                .alignVertically(0.15F));
        Component searchText = Component.translatable("visualkeys.search_keybinds").withStyle(ChatFormatting.GRAY);
        EditBox searchField = new EditBox(font, 200, 20, searchText);
        searchField.setHint(searchText);
        searchField.setResponder(string -> {
            ((ControlsListWidgetDuck) keyBindsList).visualkeys_setSearchInput(string);
            ((ControlsListWidgetDuck) keyBindsList).visualkeys_refill();
        });
        layout.addToHeader(searchField, positioner -> positioner
                .alignHorizontallyCenter()
                .alignVertically(0.75F));
    }

    @Inject(method = "addFooter", at = @At("TAIL"))
    private void initFooterTail(CallbackInfo ci, @Local LinearLayout layout) {
        layout.addChild(VisualKeys.createOpenKeyboardButton(minecraft, this));
    }

    @Override
    public void removed() {
        super.removed();
        VisualKeys.CONFIG.saveIfDirty();
    }

}
