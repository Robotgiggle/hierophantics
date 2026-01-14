package robotgiggle.hierophantics.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AllayEntity;
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
import me.shedaniel.autoconfig.AutoConfig;
import at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep;
import at.petrak.hexcasting.api.casting.castables.SpellAction;

import robotgiggle.hierophantics.data.HieroServerState;
import robotgiggle.hierophantics.blocks.FlayBedBlock;
import robotgiggle.hierophantics.blocks.FlayBedBlockEntity;
import robotgiggle.hierophantics.mishaps.NitwitImbuementMishap;
import robotgiggle.hierophantics.mishaps.MindsCappedMishap;
import robotgiggle.hierophantics.inits.HierophanticsConfig;

import java.util.List;

@Mixin(value = OpBrainsweep.class, remap = false)
public class OpBrainsweepMixin {
    @Inject(method = "execute", at = @At("TAIL"))
    private void validateImbuementRecipe(List<Iota> args, CastingEnvironment env, CallbackInfoReturnable<SpellAction.Result> ci, @Local MobEntity sacrifice, @Local BlockPos pos, @Local BlockState state) throws MishapBadBrainsweep, MindsCappedMishap, NitwitImbuementMishap {
        if (state.getBlock() instanceof FlayBedBlock) {
            BlockEntity bed = env.getWorld().getBlockEntity(pos);
            if (bed instanceof FlayBedBlockEntity flaybed) {
                Entity sleeper = flaybed.getSleeper(env.getWorld());
                // when imbuing player, mishap if:
                // - the sacrifice isn't a villager or an allay
                // - the sacrifice is a villager but not an apprentice
                // - the sacrifice is a villager but the player has reached the mind cap
                if (sleeper instanceof PlayerEntity pSleeper) {
                    if (sacrifice instanceof VillagerEntity vSacrifice) {
                        int maxMinds = HierophanticsConfig.getServer().getMaxMinds();
                        if (vSacrifice.getVillagerData().getLevel() < 2) {
                            throw new MishapBadBrainsweep(sacrifice, pos);
                        } else if (HieroServerState.getPlayerState(pSleeper).getOwnedMinds() >= maxMinds) {
                            throw new MindsCappedMishap(pSleeper);
                        }
                    } else if (!(sacrifice instanceof AllayEntity)) {
                        throw new MishapBadBrainsweep(sacrifice, pos);
                    }
                } 
                // when imbuing villager, mishap if:
                // - the sacrifice isn't a villager
                // - either the sleeper or the sacrifice is a nitwit
                if (sleeper instanceof VillagerEntity vSleeper) {
                    if (sacrifice instanceof VillagerEntity vSacrifice) {
                        if (isNitwit(vSleeper)) throw new NitwitImbuementMishap(vSleeper);
                        if (isNitwit(vSacrifice)) throw new NitwitImbuementMishap(vSacrifice);
                    } else {
                        throw new MishapBadBrainsweep(sacrifice, pos);
                    }
                }
            }
        }
    }

    private boolean isNitwit(VillagerEntity v) {
        return v.getVillagerData().getProfession() == VillagerProfession.NITWIT;
    }
}
