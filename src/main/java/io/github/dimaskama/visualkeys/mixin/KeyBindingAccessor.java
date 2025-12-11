package io.github.dimaskama.visualkeys.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Map;
import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public interface KeyBindingAccessor {
    @Accessor("key")
    InputConstants.Key visualkeys_getBoundKey();
    @Accessor("ALL")
    static Map<String, KeyMapping> visualkeys_getKeysById() {
        throw new AssertionError();
    }
}
