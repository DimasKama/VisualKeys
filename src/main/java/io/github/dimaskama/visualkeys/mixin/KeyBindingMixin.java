package io.github.dimaskama.visualkeys.mixin;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
abstract class KeyBindingMixin {
    @Shadow @Final private static Map<String, KeyBinding> KEYS_BY_ID;

    @Inject(method = "updateKeysByCode", at = @At("TAIL"))
    private static void updateKeysByCodeTail(CallbackInfo ci) {
        VisualKeys.onUpdateKeyBindings(KEYS_BY_ID.values());
    }
}
