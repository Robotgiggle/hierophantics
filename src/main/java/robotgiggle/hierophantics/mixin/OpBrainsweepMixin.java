package robotgiggle.hierophantics.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.sugar.Local;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBrainsweep;
import at.petrak.hexcasting.common.recipe.BrainsweepRecipe;
import at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep;
import at.petrak.hexcasting.api.casting.castables.SpellAction;

import robotgiggle.hierophantics.blocks.FlayBedBlock;
import robotgiggle.hierophantics.blocks.FlayBedBlockEntity;

import java.util.List;

@Mixin(value = OpBrainsweep.class, remap = false)
public class OpBrainsweepMixin {
    @Inject(method = "execute", at = @At("TAIL"))
    private void validateImbuementRecipe(List<Iota> args, CastingEnvironment env, CallbackInfoReturnable<SpellAction.Result> ci, @Local MobEntity sacrifice, @Local BlockPos pos, @Local BlockState state) throws MishapBadBrainsweep {
        if (state.getBlock() instanceof FlayBedBlock && sacrifice instanceof VillagerEntity villager) {
            // if player on bed and sacrifice isn't a master, mishap
            BlockEntity bed = env.getWorld().getBlockEntity(pos);
            if (bed instanceof FlayBedBlockEntity flaybed) {
                PlayerEntity player = flaybed.getSleeperByClass(PlayerEntity.class, env.getWorld());
                if (player != null && villager.getVillagerData().getLevel() < 5) {
                    throw new MishapBadBrainsweep(sacrifice, pos);
                }
            }
        }
    }
}
