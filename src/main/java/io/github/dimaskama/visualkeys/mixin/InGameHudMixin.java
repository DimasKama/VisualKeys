package io.github.dimaskama.visualkeys.mixin;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @Inject(
            method = "render",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private void beforeDebugRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        VisualKeys.onRender(context);
    }
}
