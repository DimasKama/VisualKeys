package io.github.dimaskama.visualkeys.mixin.controlling;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com/blamejared/controlling/client/NewKeyBindsScreen")
abstract class NewKeyBindsScreenMixin extends KeybindsScreen {

    @Unique
    private ButtonWidget visualkeys_button;

    private NewKeyBindsScreenMixin() {
        super(null, null);
        throw new AssertionError();
    }

    @Inject(method = "method_48640", at = @At("TAIL"), remap = false)
    private void initFooterTail(CallbackInfo ci) {
        if (visualkeys_button == null) {
            visualkeys_button = VisualKeys.createOpenKeyboardButton(client, this);
            addDrawableChild(visualkeys_button);
        }
        visualkeys_button.setPosition(width - 105, 5);
    }

}
