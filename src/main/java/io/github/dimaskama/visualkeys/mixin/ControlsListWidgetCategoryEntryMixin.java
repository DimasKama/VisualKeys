package io.github.dimaskama.visualkeys.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.dimaskama.visualkeys.client.VisualKeys;
import io.github.dimaskama.visualkeys.duck.ControlsListWidgetDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ControlsListWidget.CategoryEntry.class)
abstract class ControlsListWidgetCategoryEntryMixin extends ControlsListWidget.Entry {

    @Shadow(aliases = "field_2738") @Final private ControlsListWidget outer;
    @Shadow @Final Text text;

    @Unique
    private Boolean visualkeys_isCollapsed;

    @Unique
    private boolean visualkeys_isCollapsed() {
        if (visualkeys_isCollapsed == null) {
            visualkeys_isCollapsed = VisualKeys.CONFIG.getData().collapsedCategories.contains(visualkeys_getCategory());
        }
        return visualkeys_isCollapsed;
    }

    @Unique
    private String visualkeys_getCategory() {
        if (text.getContent() instanceof TranslatableTextContent translatable) {
            return translatable.getKey();
        }
        return "";
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"
            ),
            index = 5
    )
    private boolean makeTextWithShadow(boolean shadow, @Local(argsOnly = true) boolean hovered) {
        return shadow || hovered;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTail(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                visualkeys_isCollapsed() ? ">" : "∨",
                x + 3,
                y + ((entryHeight - 9) >> 1),
                0xFFFFFFFF,
                hovered
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Set<String> cats = VisualKeys.CONFIG.getData().collapsedCategories;
        String cat = visualkeys_getCategory();
        if (cats.add(cat)) {
            visualkeys_isCollapsed = true;
        } else {
            visualkeys_isCollapsed = false;
            cats.remove(cat);
        }
        ((ControlsListWidgetDuck) outer).visualkeys_refill();
        VisualKeys.CONFIG.markDirty();

        return true;
    }

}
