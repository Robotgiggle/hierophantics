package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import robotgiggle.hierophantics.HexcassettesAPI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	@Inject(method = "tick", at = @At("HEAD"))
	private void runCassettes(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		if (player.getWorld().isClient)
			return;
		HexcassettesAPI.getPlayerState(player).tick((ServerPlayerEntity) player);
	}
}