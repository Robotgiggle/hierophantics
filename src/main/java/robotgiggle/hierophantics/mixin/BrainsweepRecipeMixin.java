package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import at.petrak.hexcasting.common.recipe.BrainsweepRecipe;

@Mixin(BrainsweepRecipe.class)
public class BrainsweepRecipeMixin {
    @Inject(method = "copyProperties", at = @At("HEAD"), cancellable = true)
    private static void changeInfusedValue(BlockState original, BlockState copyTo, CallbackInfoReturnable<BlockState> ci) {
        if (copyTo.get(FlayBedBlock.INFUSED)) {
            ci.setReturnValue(original.with(FlayBedBlock.INFUSED, true));
        }
    }
}
