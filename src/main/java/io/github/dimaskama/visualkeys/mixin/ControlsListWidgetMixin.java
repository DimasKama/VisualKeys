package io.github.dimaskama.visualkeys.mixin;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import io.github.dimaskama.visualkeys.duck.ControlsListWidgetDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Locale;

@Mixin(ControlsListWidget.class)
abstract class ControlsListWidgetMixin extends EntryListWidget<ControlsListWidget.Entry> implements ControlsListWidgetDuck {

    @Shadow private int maxKeyNameLength;
    @Unique
    private String visualkeys_searchInput;

    private ControlsListWidgetMixin(MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void doRefillOnInit(CallbackInfo ci) {
        visualkeys_refill();
    }

    @Override
    public void visualkeys_setSearchInput(String searchInput) {
        visualkeys_searchInput = searchInput;
    }

    @Override
    public void visualkeys_refill() {
        ControlsListWidget list = (ControlsListWidget) (Object) this;

        clearEntries();
        KeyBinding[] keyBindings = ArrayUtils.clone(client.options.allKeys);
        Arrays.sort(keyBindings);
        String lastCat = null;
        String searchInput = visualkeys_searchInput;
        boolean doSearch = searchInput != null && !searchInput.isEmpty();
        if (doSearch) {
            searchInput = searchInput.toLowerCase(Locale.ROOT);
        }
        for (KeyBinding keyBinding : keyBindings) {
            if (doSearch) {
                if (
                        !keyBinding.getBoundKeyLocalizedText().getString().toLowerCase(Locale.ROOT).contains(searchInput)
                        && !I18n.translate(keyBinding.getTranslationKey()).toLowerCase(Locale.ROOT).contains(searchInput)
                        && !I18n.translate(keyBinding.getCategory()).toLowerCase(Locale.ROOT).contains(searchInput)
                ) {
                    continue;
                }
            }
            String cat = keyBinding.getCategory();
            if (!cat.equals(lastCat)) {
                lastCat = cat;
                addEntry(list.new CategoryEntry(Text.translatable(cat)));
            }
            if (!doSearch && VisualKeys.CONFIG.getData().collapsedCategories.contains(cat)) {
                continue;
            }

            Text text = Text.translatable(keyBinding.getTranslationKey());
            int i = client.textRenderer.getWidth(text);
            if (i > maxKeyNameLength) {
                maxKeyNameLength = i;
            }

            addEntry(list.new KeyBindingEntry(keyBinding, text));
        }
    }

}
