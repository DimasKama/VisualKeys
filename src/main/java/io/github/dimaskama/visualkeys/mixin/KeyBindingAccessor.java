package io.github.dimaskama.visualkeys.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor("boundKey")
    InputUtil.Key visualkeys_getBoundKey();
    @Accessor("KEYS_BY_ID")
    static Map<String, KeyBinding> visualkeys_getKeysById() {
        throw new AssertionError();
    }
}
