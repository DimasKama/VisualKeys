package io.github.dimaskama.visualkeys.mixin.malilib;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "fi/dy/masa/malilib/event/InputEventHandler")
abstract class InputEventHandlerMixin {

    @Inject(method = "updateUsedKeys", at = @At("TAIL"), remap = false)
    private void onUpdateKeyBindings(CallbackInfo ci) {
        VisualKeys.onUpdateKeyBindings();
    }

}
