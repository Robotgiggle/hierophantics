package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.server.world.ServerWorld;
import robotgiggle.hierophantics.blocks.FlayBedBlock;
import robotgiggle.hierophantics.minterface.VillagerEntityMinterface;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin implements VillagerEntityMinterface {
    @Unique
    int hierophantics$forcedSleepStatus = 0;

    @Override
    public void hierophantics$setForcedSleepStatus(int value) {
        hierophantics$forcedSleepStatus = value;
    }
    
    @Inject(method = "wakeUp", at = @At("HEAD"))
    private void fixScheduleAfterForcedSleep(CallbackInfo ci) {
        if (hierophantics$forcedSleepStatus > 0) {
            VillagerEntity villager = (VillagerEntity) (Object) this;
            villager.getBrain().setSchedule(hierophantics$forcedSleepStatus == 1 ? Schedule.VILLAGER_DEFAULT : Schedule.VILLAGER_BABY);
        }
    }

    @Inject(method = "wakeUp", at = @At(value = "HEAD", shift = At.Shift.AFTER))
    private void updateBrainAfterMindImbuement(CallbackInfo ci) {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        if (villager.getWorld() instanceof ServerWorld sworld) {
            if (sworld.getBlockState(villager.getBlockPos()).getBlock() instanceof FlayBedBlock) {
                villager.reinitializeBrain(sworld);
            } 
        }
    }
}
