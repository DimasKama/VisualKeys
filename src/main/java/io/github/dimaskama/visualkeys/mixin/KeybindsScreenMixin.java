package io.github.dimaskama.visualkeys.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.dimaskama.visualkeys.client.VisualKeys;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeybindsScreen.class)
abstract class KeybindsScreenMixin extends GameOptionsScreen {

    private KeybindsScreenMixin() {
        super(null, null, null);
        throw new AssertionError();
    }

    @Inject(method = "initFooter", at = @At("TAIL"))
    private void initFooterTail(CallbackInfo ci, @Local DirectionalLayoutWidget layout) {
        layout.add(VisualKeys.createOpenKeyboardButton(client, this));
    }

}
