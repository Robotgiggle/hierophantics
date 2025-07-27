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
        if (!dmgType.equals("hexcasting.overcast"))
            HierophanticsAPI.getPlayerState(player).triggerMinds((ServerPlayerEntity) player, "damage", initialIota);
        HierophanticsAPI.getPlayerState(player).checkTypedDamage((ServerPlayerEntity) player, dmgType, initialIota);
	}

    @Inject(method = "jump", at = @At("TAIL"))
	private void fireJumpTriggers(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient)
            return;
        HierophanticsAPI.getPlayerState(player).triggerMinds((ServerPlayerEntity) player, "jump");
	}

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSleeping()Z"))
    private boolean notSleepingIfOnFlayBed(PlayerEntity instance, Operation<Boolean> original) {
        Block block = instance.getWorld().getBlockState(instance.getBlockPos()).getBlock();
        if (block instanceof FlayBedBlock) return false;
        return original.call(instance);
    }
}
