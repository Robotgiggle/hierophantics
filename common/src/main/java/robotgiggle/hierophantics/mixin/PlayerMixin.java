package robotgiggle.hierophantics.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import robotgiggle.hierophantics.data.HieroServerState;
import robotgiggle.hierophantics.data.Trigger;
import robotgiggle.hierophantics.blocks.FlayBedBlock;
import robotgiggle.hierophantics.inits.HierophanticsConfig;
import robotgiggle.hierophantics.HierophanticsClient;
import robotgiggle.hierophantics.Hierophantics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import at.petrak.hexcasting.api.casting.iota.DoubleIota;

import java.util.function.Predicate;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "actuallyHurt", at = @At("HEAD"))
	private void fireTriggersBeforeDamage(DamageSource source, float amount, CallbackInfo ci) {
		if (HierophanticsConfig.getServer().getEarlyDamageTriggers()) {
            hierophantics$fireDamageTriggers(source, amount);
        }
	}
    
    @Inject(method = "actuallyHurt", at = @At("RETURN"))
	private void fireTriggersAfterDamage(DamageSource source, float amount, CallbackInfo ci) {
		if (!HierophanticsConfig.getServer().getEarlyDamageTriggers()) {
            hierophantics$fireDamageTriggers(source, amount);
        }
        // this allows health threshold triggers to fire as the player dies but before they drop the media in their inv
        Player player = (Player) (Object) this;
        if (player.isDeadOrDying()) {
            Predicate<Trigger> isDownwardHealthTrigger = t -> t.type().equals("health") && !t.inverted();
            HieroServerState.getPlayerState(player).triggerMinds((ServerPlayer) player, isDownwardHealthTrigger);
        }
	}

    @Unique
    private void hierophantics$fireDamageTriggers(DamageSource source, float amount) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide || player.isInvulnerableTo(source) || source.is(Hierophantics.BYPASSES_DAMAGE_TRIGGER))
            return;
        String dmgType = source.getMsgId();
        var initialIota = new DoubleIota(amount);
        // generic damage triggers ignore overcast damage to avoid infinite loops
        if (!dmgType.equals("hexcasting.overcast"))
            HieroServerState.getPlayerState(player).triggerMinds((ServerPlayer) player, "damage", initialIota);
        // typed damage triggers don't, in case you actually want that for some reason
        HieroServerState.getPlayerState(player).checkTypedDamage((ServerPlayer) player, dmgType, initialIota);
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
	private void fireJumpTriggers(CallbackInfo ci) {
		Player player = (Player) (Object) this;
        if (player.level().isClientSide)
            return;
        HieroServerState.getPlayerState(player).triggerMinds((ServerPlayer) player, "jump");
	}

    @Inject(method = "getHurtSound", at = @At("TAIL"), cancellable = true)
    private void playVillagerHurtNoise(DamageSource damageSource, CallbackInfoReturnable<SoundEvent> ci) {
        if (ci.getReturnValue() == SoundEvents.PLAYER_HURT) {
            Player player = (Player) (Object) this;
            int minds;
            if (player.level().isClientSide()) minds = HierophanticsClient.clientOwnedMinds;
            else minds = HieroServerState.getPlayerState(player).getOwnedMinds();
            if (damageSource.getMsgId().equals("hexcasting.overcast")) minds *= 2;
            if (player.getRandom().nextDouble() < 0.3 - 1.0/(minds + 3)) {
                if (Hierophantics.isAprilFools()) {
                    ci.setReturnValue(SoundEvents.SALMON_HURT);
                } else {
                    ci.setReturnValue(SoundEvents.VILLAGER_HURT);
                }
            }
        }
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSleeping()Z"))
    private boolean notSleepingIfOnFlayBed(Player instance, Operation<Boolean> original) {
        Block sleepingOn = instance.level().getBlockState(instance.blockPosition()).getBlock();
        if (sleepingOn instanceof FlayBedBlock) return false;
        return original.call(instance);
    }

    // not needed in vanilla, but some mods that modify sleep will break the flaybed if this isn't here
    @Inject(method = "isSleepingLongEnough", at = @At("HEAD"), cancellable = true)
    private void cantResetTimeIfOnFlayBed(CallbackInfoReturnable<Boolean> ci) {
        Player player = (Player) (Object) this;
        Block sleepingOn = player.level().getBlockState(player.blockPosition()).getBlock();
        if (sleepingOn instanceof FlayBedBlock) ci.setReturnValue(false);
    }
}
