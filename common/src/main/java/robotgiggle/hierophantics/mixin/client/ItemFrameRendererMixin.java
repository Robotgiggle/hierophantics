package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.world.item.ItemStack;
import robotgiggle.hierophantics.HierophanticsClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemFrameRenderer.class)
public class ItemFrameRendererMixin {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V"), index = 0)
    private ItemStack hallucinateItemFrame(ItemStack original) {
        return HierophanticsClient.hallucinateItem(original);
    }
}
