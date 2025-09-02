package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import robotgiggle.hierophantics.HierophanticsClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @ModifyArg(method = "renderHotbarItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"), index = 1)
    private ItemStack hallucinateHotbarItem(ItemStack original) {
        return HierophanticsClient.hallucinateItem(original);
    }
}
