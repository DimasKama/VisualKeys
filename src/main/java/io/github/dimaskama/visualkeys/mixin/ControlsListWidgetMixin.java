package io.github.dimaskama.visualkeys.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dimaskama.visualkeys.client.VisualKeys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ControlsListWidget.class)
abstract class ControlsListWidgetMixin extends EntryListWidget<ControlsListWidget.Entry> {

    private ControlsListWidgetMixin(MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
    }

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/option/ControlsListWidget;addEntry(Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I",
                    ordinal = 1
            )
    )
    private int cancelAddEntry(ControlsListWidget instance, EntryListWidget.Entry entry, Operation<Integer> original) {
        if (entry instanceof ControlsListWidget.KeyBindingEntry keyBindingEntry && VisualKeys.CONFIG.getData().collapsedCategories.contains(keyBindingEntry.binding.getCategory())) {
            return getEntryCount() - 1;
        }
        return original.call(instance, entry);
    }

}
