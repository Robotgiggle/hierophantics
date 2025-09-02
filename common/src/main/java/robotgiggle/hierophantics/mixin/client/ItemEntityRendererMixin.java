package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.item.ItemStack;
import robotgiggle.hierophantics.HierophanticsClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;"), index = 0)
    private ItemStack hallucinateItemEntity(ItemStack original) {
        return HierophanticsClient.hallucinateItem(original);
    }
}
