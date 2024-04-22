package io.github.dimaskama.visualkeys.mixin;

import io.github.dimaskama.visualkeys.client.screen.KeyboardScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeybindsScreen.class)
abstract class KeybindsScreenMixin extends GameOptionsScreen {
    public KeybindsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void initTail(CallbackInfo ci) {
        addDrawableChild(ButtonWidget.builder(Text.translatable("visualkeys.open_keyboard"), button -> {
            client.setScreen(new KeyboardScreen(this));
        }).dimensions(width - 109, height - 29, 100, 20).build());
    }
}
