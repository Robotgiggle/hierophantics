package robotgiggle.hierophantics.mixin;

import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
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
			state.skipVelocityTriggers();
			state.setPrevHealth(0);
		}
	}

	@Inject(method = {"teleportTo*", "teleportRelative"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFFLjava/util/Set;)V", shift = At.Shift.AFTER))
	private void fireTeleportTriggers1(CallbackInfo ci) {
		hierophantics$fireTeleportTriggers();
	}

	@Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V", shift = At.Shift.AFTER))
	private void fireTeleportTriggers2(CallbackInfo ci) {
		hierophantics$fireTeleportTriggers();
	}

	@Unique
	private void hierophantics$fireTeleportTriggers() {
		ServerPlayer player = (ServerPlayer) (Object) this;
		var state = HieroServerState.getPlayerState(player);
		if (state.getOwnedMinds() > 0) {
			var offsetVec = player.position().subtract(state.getPrevPos());
			state.triggerMinds(player, "teleport", new Vec3Iota(offsetVec));
			state.skipVelocityTriggers();
		}
	}

	@Inject(method = {"setServerLevel", "stopRiding", "startSleeping", "stopSleepInBed"}, at = @At("HEAD"))
	private void skipVelocityTriggers1(CallbackInfo ci) {
		hierophantics$skipVelocityTriggers();
	}

	@Inject(method = "startRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;positionRider(Lnet/minecraft/world/entity/Entity;)V"))
	private void skipVelocityTriggers2(CallbackInfoReturnable<Boolean> cir) {
		hierophantics$skipVelocityTriggers();
	}

	@Unique
    private void hierophantics$skipVelocityTriggers() {
		ServerPlayer player = (ServerPlayer) (Object) this;
		var state = HieroServerState.getPlayerState(player);
		if (state.getOwnedMinds() > 0) state.skipVelocityTriggers();
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
	private boolean bedInRange(BlockPos pos, Direction direction) { return false; }

	@Shadow
	private boolean bedBlocked(BlockPos pos, Direction direction) { return false; }

	@Unique
    private Either<Player.BedSleepingProblem, Unit> hierophantics$trimmedTrySleep(ServerPlayer player, BlockPos blockPos) {
        Direction direction = player.level().getBlockState(blockPos).getValue(HorizontalDirectionalBlock.FACING);
        if (player.isSleeping() || !player.isAlive()) {
            return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
        }
        if (!this.bedInRange(blockPos, direction)) {
            return Either.left(Player.BedSleepingProblem.TOO_FAR_AWAY);
        }
        if (this.bedBlocked(blockPos, direction)) {
            return Either.left(Player.BedSleepingProblem.OBSTRUCTED);
        }
        return super.startSleepInBed(blockPos);
    }
}
