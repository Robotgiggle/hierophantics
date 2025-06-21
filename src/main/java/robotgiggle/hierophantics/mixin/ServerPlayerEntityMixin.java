package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import robotgiggle.hierophantics.HierophanticsAPI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import at.petrak.hexcasting.api.HexAPI;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
	private void checkThresholdTriggers(CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		HierophanticsAPI.getPlayerState(player).tick(player);
	}

	@Inject(method = "onDeath", at = @At("HEAD"))
	private void disableMindsOnDeath(CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		HierophanticsAPI.getPlayerState(player).setDisabled(true);
	}
}
