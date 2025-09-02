package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import robotgiggle.hierophantics.HierophanticsClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @ModifyArg(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V"), index = 0)
    private ItemStack hallucinateItemInSlot(ItemStack original) {
        return HierophanticsClient.hallucinateItem(original);
    }

    @ModifyArg(method = "drawItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;II)V"), index = 0)
    private ItemStack hallucinateItemInCursor(ItemStack original) {
        return HierophanticsClient.hallucinateItem(original);
    }
}
