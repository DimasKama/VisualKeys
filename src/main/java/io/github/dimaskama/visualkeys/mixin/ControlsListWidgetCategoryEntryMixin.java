package io.github.dimaskama.visualkeys.mixin;

import io.github.dimaskama.visualkeys.client.VisualKeys;
import io.github.dimaskama.visualkeys.duck.ControlsListWidgetDuck;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.input.MouseButtonEvent;

@Mixin(KeyBindsList.CategoryEntry.class)
abstract class ControlsListWidgetCategoryEntryMixin extends KeyBindsList.Entry {

    @Shadow @Final KeyBindsList this$0;

    @Unique
    private Boolean visualkeys_isCollapsed;

    @Unique
    private String visualkeys_category;

    @Unique
    private boolean visualkeys_isCollapsed() {
        if (visualkeys_isCollapsed == null) {
            visualkeys_isCollapsed = VisualKeys.CONFIG.getData().collapsedCategories.contains(visualkeys_category);
        }
        return visualkeys_isCollapsed;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initTail(KeyBindsList controlsListWidget, KeyMapping.Category category, CallbackInfo ci) {
        visualkeys_category = VisualKeys.keyCategoryToString(category);
    }

    @Inject(method = "extractContent", at = @At("TAIL"))
    private void extractContentTail(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float deltaTicks, CallbackInfo ci) {
        context.text(
                Minecraft.getInstance().font,
                visualkeys_isCollapsed() ? ">" : "∨",
                getContentX() + 3,
                getContentY() + ((getContentHeight() - 9) >> 1),
                0xFFFFFFFF,
                hovered
        );
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent input, boolean doubled) {
        Set<String> cats = VisualKeys.CONFIG.getData().collapsedCategories;
        String cat = visualkeys_category;
        if (cats.add(cat)) {
            visualkeys_isCollapsed = true;
        } else {
            visualkeys_isCollapsed = false;
            cats.remove(cat);
        }
        ((ControlsListWidgetDuck) this$0).visualkeys_refill();
        VisualKeys.CONFIG.markDirty();

        return true;
    }

}
