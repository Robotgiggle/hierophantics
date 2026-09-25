package robotgiggle.hierophantics.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.spongepowered.asm.mixin.Unique;
import robotgiggle.hierophantics.data.HieroServerState;
import robotgiggle.hierophantics.blocks.FlayBedBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;

import at.petrak.hexcasting.api.casting.iota.EntityIota;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    ServerPlayerMixin(Level world, BlockPos blockPos, float f, GameProfile gameProfile) {
		super(world, blockPos, f, gameProfile);
	}

	@Inject(method = "drop", at = @At("TAIL"))
	private void fireDropTriggers(ItemStack itemStack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemStack> ci, @Local ItemEntity droppedEntity) {
        if (!throwRandomly && retainOwnership) {
			ServerPlayer player = (ServerPlayer) (Object) this;
			HieroServerState.getPlayerState(player).triggerMinds(player, "drop", new EntityIota(droppedEntity));
		}
    }
	
	@Inject(method = "tick", at = @At("HEAD"))
	private void checkThresholdTriggers(CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		HieroServerState.getPlayerState(player).tick(player);
	}

	@Inject(method = "die", at = @At("HEAD"))
	private void disableMindsOnDeath(CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		var state = HieroServerState.getPlayerState(player);
		if (state.getOwnedMinds() > 0) {
			state.setDisabled(true);
			state.setSkipTeleTrigger(5);
			state.setPrevHealth(0);
		}
	}

	@Inject(method = "triggerDimensionChangeTriggers", at = @At("HEAD"))
	private void skipTeleTriggerWhenChangingDims(CallbackInfo ci) {
		hierophantics$skipTeleTrigger();
	}

	@Inject(method = "startRiding", at = @At("RETURN"))
	private void skipTeleTriggerWhenMountingEntity(CallbackInfoReturnable<Boolean> ci) {
		if (ci.getReturnValue()) hierophantics$skipTeleTrigger();
	}

	@Inject(method = "stopRiding", at = @At("HEAD"))
	private void skipTeleTriggerWhenDismountingEntity(CallbackInfo ci) {
		hierophantics$skipTeleTrigger();
	}

	@Inject(method = "startSleeping", at = @At("HEAD"))
	private void skipTeleTriggerWhenGettingIntoBed(CallbackInfo ci) {
		hierophantics$skipTeleTrigger();
	}

	@Inject(method = "stopSleepInBed", at = @At("HEAD"))
	private void skipTeleTriggerWhenGettingOutOfBed(CallbackInfo ci) {
		hierophantics$skipTeleTrigger();
	}

	@Unique
    private void hierophantics$skipTeleTrigger() {
		ServerPlayer player = (ServerPlayer) (Object) this;
		var state = HieroServerState.getPlayerState(player);
		if (state.getOwnedMinds() > 0) state.setSkipTeleTrigger(5);
	}
	
	@Inject(method = "startSleepInBed", at = @At("HEAD"), cancellable = true)
	private void trySleepInFlayBed(BlockPos blockPos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		Block block = player.level().getBlockState(blockPos).getBlock();
		if (block instanceof FlayBedBlock) {
			ci.setReturnValue(hierophantics$trimmedTrySleep(player, blockPos));
		}
	}

	@Shadow
	private boolean isBedTooFarAway(BlockPos blockPos, Direction direction) { return false; }

	@Shadow
	private boolean isBedObstructed(BlockPos blockPos, Direction direction) { return false; }

	@Unique
    private Either<Player.BedSleepingProblem, Unit> hierophantics$trimmedTrySleep(ServerPlayer player, BlockPos blockPos) {
        Direction direction = player.level().getBlockState(blockPos).getValue(HorizontalDirectionalBlock.FACING);
        if (player.isSleeping() || !player.isAlive()) {
            return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
        }
        if (!this.isBedTooFarAway(blockPos, direction)) {
            return Either.left(Player.BedSleepingProblem.TOO_FAR_AWAY);
        }
        if (this.isBedObstructed(blockPos, direction)) {
            return Either.left(Player.BedSleepingProblem.OBSTRUCTED);
        }
        return super.startSleepInBed(blockPos);
    }
}
