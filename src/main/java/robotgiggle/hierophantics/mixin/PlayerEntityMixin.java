package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.Block;
import robotgiggle.hierophantics.HierophanticsAPI;
import robotgiggle.hierophantics.blocks.FlayBedBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.lib.HexDamageTypes;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "applyDamage", at = @At("TAIL"))
	private void fireDamageTriggers(DamageSource source, float amount, CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient)
            return;
        String dmgType = source.getName();
        if (!dmgType.equals("hexcasting.overcast"))
            HierophanticsAPI.getPlayerState(player).triggerMinds((ServerPlayerEntity) player, "damage");
        HierophanticsAPI.getPlayerState(player).checkTypedDamage((ServerPlayerEntity) player, dmgType);
	}

    @Inject(method = "jump", at = @At("TAIL"))
	private void fireJumpTriggers(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient)
            return;
        HierophanticsAPI.getPlayerState(player).triggerMinds((ServerPlayerEntity) player, "jump");
	}

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSleeping()Z"))
    private boolean notSleepingIfOnFlayBed(PlayerEntity checkingPlayer) {
        Block block = checkingPlayer.getWorld().getBlockState(checkingPlayer.getBlockPos()).getBlock();
        if (block instanceof FlayBedBlock) return false;
        return checkingPlayer.isSleeping();
    }
}
