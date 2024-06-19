package io.github.dimaskama.visualkeys.mixin;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @Inject(
            method = "method_55807",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"
            )
    )
    private void beforeDebugRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        VisualKeys.onRender(context);
    }
}
