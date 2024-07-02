package io.github.dimaskama.visualkeys.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.dimaskama.visualkeys.client.KeyboardRenderOptions;

public class ModConfig extends JsonConfig<ModConfig.Data> {
    public static final Codec<Data> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("enabled").forGetter(d -> d.enabled),
                    Codec.BOOL.fieldOf("save_enabled_state").forGetter(d -> d.saveEnabledState),
                    Codec.BOOL.fieldOf("keyboard_textured").forGetter(d -> d.keyboardTextured),
                    KeyboardRenderOptions.CODEC.fieldOf("hud_render_options").forGetter(d -> d.hudRenderOptions)
            ).apply(instance, Data::new)
    );

    public ModConfig(String path) {
        super(path);
    }

    @Override
    protected Codec<Data> getCodec() {
        return CODEC;
    }

    @Override
    protected Data createDefaultData() {
        return new Data(
                false,
                false,
                true,
                new KeyboardRenderOptions(
                        0, 0,
                        0.25F,
                        true, true, true
                )
        );
    }

    public static class Data {

        public boolean enabled;
        public boolean saveEnabledState;
        public boolean keyboardTextured;
        public KeyboardRenderOptions hudRenderOptions;

        public Data(boolean enabled, boolean saveEnabledState, boolean keyboardTextured, KeyboardRenderOptions hudRenderOptions) {
            this.enabled = enabled;
            this.saveEnabledState = saveEnabledState;
            this.keyboardTextured = keyboardTextured;
            this.hudRenderOptions = hudRenderOptions;
        }
    }
}
