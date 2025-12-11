package io.github.dimaskama.visualkeys.mixin;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
abstract class KeyBindingMixin {

    @Inject(method = "resetMapping", at = @At("TAIL"))
    private static void updateKeysByCodeTail(CallbackInfo ci) {
        VisualKeys.onUpdateKeyBindings();
    }

}
