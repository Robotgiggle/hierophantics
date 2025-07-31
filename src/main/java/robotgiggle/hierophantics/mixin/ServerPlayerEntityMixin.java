package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFacingBlock;
import robotgiggle.hierophantics.HierophanticsAPI;
import robotgiggle.hierophantics.blocks.FlayBedBlock;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.EntityIota;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
		super(world, blockPos, f, gameProfile);
	}

	@Inject(method = "dropItem", at = @At("TAIL"))
	private void fireDropTriggers(ItemStack itemStack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemStack> ci, @Local ItemEntity droppedEntity) {
        if (!throwRandomly && retainOwnership) {
			ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
			HierophanticsAPI.getPlayerState(player).triggerMinds((ServerPlayerEntity) player, "drop", new EntityIota(droppedEntity));
		}
    }
	
	@Inject(method = "tick", at = @At("HEAD"))
	private void checkThresholdTriggers(CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		HierophanticsAPI.getPlayerState(player).tick(player);
	}

	@Inject(method = "onDeath", at = @At("HEAD"))
	private void disableMindsOnDeath(CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		var state = HierophanticsAPI.getPlayerState(player);
		if (state.getOwnedMinds() > 0) state.setDisabled(true);
		state.setSkipTeleTrigger(true);
	}

	@Inject(method = "moveToWorld", at = @At("HEAD"))
	private void skipTeleTriggerWhenChangingDims(CallbackInfoReturnable ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		var state = HierophanticsAPI.getPlayerState(player);
		state.setSkipTeleTrigger(true);
	}

	@Inject(method = "trySleep", at = @At("Head"), cancellable = true)
	private void trySleepInFlayBed(BlockPos blockPos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		Block block = player.getWorld().getBlockState(blockPos).getBlock();
		if (block instanceof FlayBedBlock) {
			ci.setReturnValue(trimmedTrySleep(player, blockPos));
		}
	}

	@Shadow
	private boolean isBedTooFarAway(BlockPos blockPos, Direction direction) { return false; }

	@Shadow
	private boolean isBedObstructed(BlockPos blockPos, Direction direction) { return false; }

	public Either<PlayerEntity.SleepFailureReason, Unit> trimmedTrySleep(ServerPlayerEntity player, BlockPos blockPos) {
        Direction direction = player.getWorld().getBlockState(blockPos).get(HorizontalFacingBlock.FACING);
        if (player.isSleeping() || !player.isAlive()) {
            return Either.left(PlayerEntity.SleepFailureReason.OTHER_PROBLEM);
        }
        if (!this.isBedTooFarAway(blockPos, direction)) {
            return Either.left(PlayerEntity.SleepFailureReason.TOO_FAR_AWAY);
        }
        if (this.isBedObstructed(blockPos, direction)) {
            return Either.left(PlayerEntity.SleepFailureReason.OBSTRUCTED);
        }
        return super.trySleep(blockPos);
    }
}
