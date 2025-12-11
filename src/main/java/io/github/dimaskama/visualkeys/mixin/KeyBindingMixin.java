package io.github.dimaskama.visualkeys.mixin;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
abstract class KeyBindingMixin {

    @Inject(method = "updateKeysByCode", at = @At("TAIL"))
    private static void updateKeysByCodeTail(CallbackInfo ci) {
        VisualKeys.onUpdateKeyBindings();
    }

}
