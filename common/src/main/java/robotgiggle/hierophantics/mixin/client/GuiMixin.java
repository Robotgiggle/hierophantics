package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.item.ItemStack;
import robotgiggle.hierophantics.HierophanticsClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Gui.class)
public class GuiMixin {
    @ModifyArg(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V"), index = 1)
    private ItemStack hallucinateHotbarItem(ItemStack original) {
        return HierophanticsClient.hallucinateItem(original);
    }
}
