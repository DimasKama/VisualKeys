package io.github.dimaskama.visualkeys.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

public class KeyboardRenderOptions {
    public static final Codec<KeyboardRenderOptions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.NONNEGATIVE_INT.fieldOf("keyboard_x").forGetter(o -> o.keyboardX),
                    Codecs.NONNEGATIVE_INT.fieldOf("keyboard_y").forGetter(o -> o.keyboardY),
                    Codec.floatRange(0.0F, 10.0F).fieldOf("keyboard_scale").forGetter(o -> o.keyboardScale),
                    Codec.BOOL.fieldOf("function_visible").forGetter(o -> o.functionVisible),
                    Codec.BOOL.fieldOf("mid_visible").forGetter(o -> o.midVisible),
                    Codec.BOOL.fieldOf("numpad_visible").forGetter(o -> o.numpadVisible)
            ).apply(instance, KeyboardRenderOptions::new)
    );

    public int keyboardX;
    public int keyboardY;
    public float keyboardScale;
    public boolean functionVisible;
    public boolean midVisible;
    public boolean numpadVisible;

    public KeyboardRenderOptions(int keyboardX, int keyboardY, float keyboardScale, boolean functionVisible, boolean midVisible, boolean numpadVisible) {
        this.keyboardX = keyboardX;
        this.keyboardY = keyboardY;
        this.keyboardScale = keyboardScale;
        this.functionVisible = functionVisible;
        this.midVisible = midVisible;
        this.numpadVisible = numpadVisible;
    }

    public int getCurrentWidth() {
        int w = 1500;
        if (midVisible) w += 325;
        if (numpadVisible) w += 425;
        return w;
    }

    public int getCurrentHeight() {
        return functionVisible || midVisible ? 650 : 500;
    }
}
