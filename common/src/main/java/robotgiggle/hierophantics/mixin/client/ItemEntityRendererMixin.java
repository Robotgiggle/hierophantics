package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.item.ItemStack;
import robotgiggle.hierophantics.HierophanticsClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getModel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Lnet/minecraft/client/resources/model/BakedModel;"), index = 0)
    private ItemStack hallucinateItemEntity(ItemStack original) {
        return HierophanticsClient.hallucinateItem(original);
    }
}
