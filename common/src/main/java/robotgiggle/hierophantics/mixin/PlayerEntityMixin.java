package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.Block;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import robotgiggle.hierophantics.data.HieroServerState;
import robotgiggle.hierophantics.blocks.FlayBedBlock;
import robotgiggle.hierophantics.HierophanticsClient;

import robotgiggle.hierophantics.Hierophantics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.lib.HexDamageTypes;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;

import java.util.List;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "applyDamage", at = @At("TAIL"))
	private void fireDamageTriggers(DamageSource source, float amount, CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient || player.isInvulnerableTo(source))
            return;
        String dmgType = source.getName();
        var initialIota = new DoubleIota((double)amount);
        if (!dmgType.equals("genericKill")) {
            if (!dmgType.equals("hexcasting.overcast"))
                HieroServerState.getPlayerState(player).triggerMinds((ServerPlayerEntity) player, "damage", initialIota);
            HieroServerState.getPlayerState(player).checkTypedDamage((ServerPlayerEntity) player, dmgType, initialIota);
        }
	}

    @Inject(method = "jump", at = @At("TAIL"))
	private void fireJumpTriggers(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient)
            return;
        HieroServerState.getPlayerState(player).triggerMinds((ServerPlayerEntity) player, "jump");
	}

    @Inject(method = "getHurtSound", at = @At("TAIL"), cancellable = true)
    private void playVillagerHurtNoise(CallbackInfoReturnable<SoundEvent> ci) {
        if (ci.getReturnValue() == SoundEvents.ENTITY_PLAYER_HURT) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            int minds = 0;
            if (player.getWorld().isClient()) minds = HierophanticsClient.getClientOwnedMinds();
            else minds = HieroServerState.getPlayerState(player).getOwnedMinds();
            if (player.getRandom().nextDouble() < 0.3 - 1.0/(minds + 3)) {
                ci.setReturnValue(SoundEvents.ENTITY_VILLAGER_HURT);
            }
        }
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSleeping()Z"))
    private boolean notSleepingIfOnFlayBed(PlayerEntity instance, Operation<Boolean> original) {
        Block sleepingOn = instance.getWorld().getBlockState(instance.getBlockPos()).getBlock();
        if (sleepingOn instanceof FlayBedBlock) return false;
        return original.call(instance);
    }

    // not needed in vanilla, but some mods that modify sleep will break the flaybed if this isn't here
    @Inject(method = "canResetTimeBySleeping", at = @At("HEAD"), cancellable = true)
    private void cantResetTimeIfOnFlayBed(CallbackInfoReturnable<Boolean> ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Block sleepingOn = player.getWorld().getBlockState(player.getBlockPos()).getBlock();
        if (sleepingOn instanceof FlayBedBlock) ci.setReturnValue(false);
    }
}
