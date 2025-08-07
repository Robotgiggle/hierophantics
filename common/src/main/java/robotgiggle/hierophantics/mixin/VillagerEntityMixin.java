package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.server.world.ServerWorld;

import robotgiggle.hierophantics.blocks.FlayBedBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import at.petrak.hexcasting.api.HexAPI;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {
    @Inject(method = "wakeUp", at = @At("HEAD"))
    private void updateBrainAfterMindImbuement(CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        if (villager.getWorld() instanceof ServerWorld sworld) {
            if (sworld.getBlockState(villager.getBlockPos()).getBlock() instanceof FlayBedBlock) {
                villager.reinitializeBrain(sworld);
            } 
        }
    }
}
