package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import robotgiggle.hierophantics.HierophanticsAPI;
import robotgiggle.hierophantics.blocks.FlayBedBlock;
import robotgiggle.hierophantics.blocks.FlayBedBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.common.recipe.BrainsweepRecipe;
import at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep;

@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep$Spell", remap = false)
public class BrainsweepSpellMixin {
    @Shadow private BlockPos pos;
    @Shadow private BlockState state;
    @Shadow private MobEntity sacrifice;
    @Shadow private BrainsweepRecipe recipe;

    @Inject(method = "cast", at = @At("HEAD"))
    private void activateFlayBed(CastingEnvironment env, CallbackInfo ci) {
        if (state.getBlock() instanceof FlayBedBlock && recipe.result().getBlock() instanceof FlayBedBlock) {
            BlockEntity bed = env.getWorld().getBlockEntity(pos);
            if (bed instanceof FlayBedBlockEntity flaybed) {
                flaybed.activate(env.getWorld(), state, sacrifice, env.getPigment());
            }
        }
    }
}
