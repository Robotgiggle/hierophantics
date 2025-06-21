package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import robotgiggle.hierophantics.HierophanticsAPI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Shadow PlayerEntity player;

    @Inject(method = "dropSelectedItem", at = @At("TAIL"))
    private void fireDropTriggers(CallbackInfoReturnable<ItemStack> ci) {
        if (player.getWorld().isClient)
			return;
		HierophanticsAPI.getPlayerState(player).triggerMinds((ServerPlayerEntity) player, 7);
    }
}
