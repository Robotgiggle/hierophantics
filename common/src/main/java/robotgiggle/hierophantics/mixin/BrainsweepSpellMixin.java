package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import robotgiggle.hierophantics.blocks.FlayBedBlock;
import robotgiggle.hierophantics.blocks.FlayBedBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.common.recipe.BrainsweepRecipe;

@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep$Spell", remap = false)
public class BrainsweepSpellMixin {
    @Final @Shadow private BlockPos pos;
    @Final @Shadow private BlockState state;
    @Final @Shadow private MobEntity sacrifice;
    @Final @Shadow private BrainsweepRecipe recipe;

    @Inject(method = "cast", at = @At("HEAD"), cancellable = true)
    private void activateFlayBed(CastingEnvironment env, CallbackInfo ci) {
        if (state.getBlock() instanceof FlayBedBlock && recipe.result().getBlock() instanceof FlayBedBlock) {
            BlockEntity bed = env.getWorld().getBlockEntity(pos);
            if (bed instanceof FlayBedBlockEntity flaybed) {
                boolean allowFlay = flaybed.activate(env.getWorld(), state, sacrifice, env.getPigment());
                if (!allowFlay) ci.cancel();
            }
        }
    }
}
