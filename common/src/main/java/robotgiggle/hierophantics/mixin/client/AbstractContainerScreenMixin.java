package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import robotgiggle.hierophantics.HierophanticsClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @ModifyArg(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;III)V"), index = 0)
    private ItemStack hallucinateItemInSlot(ItemStack original) {
        return HierophanticsClient.hallucinateItem(original);
    }

    @ModifyArg(method = "renderFloatingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;II)V"), index = 0)
    private ItemStack hallucinateItemInCursor(ItemStack original) {
        return HierophanticsClient.hallucinateItem(original);
    }
}
