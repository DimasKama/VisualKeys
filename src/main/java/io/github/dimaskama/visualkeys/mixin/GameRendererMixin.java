package io.github.dimaskama.visualkeys.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.dimaskama.visualkeys.client.VisualKeys;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {

    @Inject(
            method = "render",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 4
            )
    )
    private void beforeRenderDebugHud(CallbackInfo ci, @Local GuiGraphics context) {
        VisualKeys.onRender(context);
    }

}
