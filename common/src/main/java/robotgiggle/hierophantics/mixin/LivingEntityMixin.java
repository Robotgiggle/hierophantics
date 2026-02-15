package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import robotgiggle.hierophantics.data.HieroServerState;
import robotgiggle.hierophantics.inits.HierophanticsEffects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import at.petrak.hexcasting.api.casting.iota.EntityIota;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "onAttacking", at = @At("TAIL"))
	private void fireAttackTriggers(Entity target, CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity player) {
            if (player.getWorld().isClient)
			    return;
		    HieroServerState.getPlayerState(player).triggerMinds((ServerPlayerEntity) player, "attack", new EntityIota(target));
        }
	}

	@Inject(method = "isSleepingInBed", at = @At("HEAD"), cancellable = true)
	private void allowSleepingAnywhere(CallbackInfoReturnable<Boolean> ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity.hasStatusEffect(HierophanticsEffects.SLEEP_ANYWHERE.getValue())) {
			ci.setReturnValue(true);
		}
	}
}
