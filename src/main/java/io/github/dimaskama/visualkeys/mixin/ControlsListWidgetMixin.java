package io.github.dimaskama.visualkeys.mixin;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import io.github.dimaskama.visualkeys.duck.ControlsListWidgetDuck;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;

@Mixin(KeyBindsList.class)
abstract class ControlsListWidgetMixin extends AbstractSelectionList<KeyBindsList.Entry> implements ControlsListWidgetDuck {

    @Shadow private int maxNameWidth;

    @Shadow
    public abstract void resetMappingAndUpdateButtons();

    @Unique
    private String visualkeys_searchInput;

    private ControlsListWidgetMixin(Minecraft client, int width, int height, int y, int itemHeight) {
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
        KeyBindsList list = (KeyBindsList) (Object) this;

        clearEntries();
        KeyMapping[] keyBindings = ArrayUtils.clone(minecraft.options.keyMappings);
        Arrays.sort(keyBindings);
        KeyMapping.Category lastCat = null;
        String searchInput = visualkeys_searchInput;
        boolean doSearch = searchInput != null && !searchInput.isEmpty();
        if (doSearch) {
            searchInput = searchInput.toLowerCase(Locale.ROOT);
        }
        for (KeyMapping keyBinding : keyBindings) {
            Component text = Component.translatable(keyBinding.getName());
            if (doSearch) {
                if (
                        !keyBinding.getTranslatedKeyMessage().getString().toLowerCase(Locale.ROOT).contains(searchInput)
                        && !text.getString().toLowerCase(Locale.ROOT).contains(searchInput)
                        && !keyBinding.getCategory().label().getString().toLowerCase(Locale.ROOT).contains(searchInput)
                ) {
                    continue;
                }
            }
            KeyMapping.Category cat = keyBinding.getCategory();
            if (!cat.equals(lastCat)) {
                lastCat = cat;
                addEntry(list.new CategoryEntry(cat));
            }
            if (!doSearch && VisualKeys.CONFIG.getData().collapsedCategories.contains(VisualKeys.keyCategoryToString(cat))) {
                continue;
            }

            int i = minecraft.font.width(text);
            if (i > maxNameWidth) {
                maxNameWidth = i;
            }

            addEntry(list.new KeyEntry(keyBinding, text));
        }

        resetMappingAndUpdateButtons();
    }

}
