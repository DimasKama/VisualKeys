package io.github.dimaskama.visualkeys.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.dimaskama.visualkeys.client.KeyboardRenderOptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModConfig extends JsonConfig<ModConfig.Data> {

    public static final Codec<Data> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("enabled").forGetter(d -> d.enabled),
                    Codec.BOOL.fieldOf("save_enabled_state").forGetter(d -> d.saveEnabledState),
                    Codec.BOOL.fieldOf("keyboard_textured").forGetter(d -> d.keyboardTextured),
                    KeyboardRenderOptions.CODEC.fieldOf("hud_render_options").forGetter(d -> d.hudRenderOptions),
                    Codec.STRING.listOf().optionalFieldOf("collapsed_categories", List.of()).forGetter(Data::getCollapsedCategoriesAsList)
            ).apply(instance, Data::new)
    );
    private boolean dirty;

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
                ),
                List.of()
        );
    }

    public void markDirty() {
        dirty = true;
    }

    public void saveIfDirty() {
        if (dirty) {
            save();
            dirty = false;
        }
    }

    public static class Data {

        public boolean enabled;
        public boolean saveEnabledState;
        public boolean keyboardTextured;
        public KeyboardRenderOptions hudRenderOptions;
        public Set<String> collapsedCategories;

        public Data(boolean enabled, boolean saveEnabledState, boolean keyboardTextured, KeyboardRenderOptions hudRenderOptions, List<String> collapsedCategories) {
            this.enabled = enabled;
            this.saveEnabledState = saveEnabledState;
            this.keyboardTextured = keyboardTextured;
            this.hudRenderOptions = hudRenderOptions;
            this.collapsedCategories = new HashSet<>(collapsedCategories);
        }

        public List<String> getCollapsedCategoriesAsList() {
            return List.copyOf(collapsedCategories);
        }
    }

}
