package robotgiggle.hierophantics.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
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
    @Inject(method = "setLastHurtMob", at = @At("TAIL"))
	private void fireAttackTriggers(Entity target, CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            if (player.level().isClientSide)
			    return;
			if (target instanceof EnderDragonPart part)
				target = part.parentMob;
		    HieroServerState.getPlayerState(player).triggerMinds((ServerPlayer) player, "attack", new EntityIota(target));
        }
	}

	@Inject(method = "checkBedExists", at = @At("HEAD"), cancellable = true)
	private void allowSleepingAnywhere(CallbackInfoReturnable<Boolean> ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity.hasEffect(HierophanticsEffects.SLEEP_ANYWHERE.getValue())) {
			ci.setReturnValue(true);
		}
	}
}
