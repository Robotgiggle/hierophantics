package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import robotgiggle.hierophantics.HierophanticsAPI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import at.petrak.hexcasting.api.HexAPI;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "onDamaged", at = @At("TAIL"))
	private void fireDamageTriggers(CallbackInfo ci) {
		fireTriggersIfServerPlayer(0);
	}

	@Inject(method = "onAttacking", at = @At("TAIL"))
	private void fireAttackTriggers(CallbackInfo ci) {
		fireTriggersIfServerPlayer(8);
	}

	@Inject(method = "jump", at = @At("TAIL"))
	private void fireJumpTriggers(CallbackInfo ci) {
		fireTriggersIfServerPlayer(10);
	}

	private void fireTriggersIfServerPlayer(int triggerId) {
		LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity player) {
            if (player.getWorld().isClient)
			    return;
		    HierophanticsAPI.getPlayerState(player).triggerMinds((ServerPlayerEntity) player, triggerId);
        }
	}
}
