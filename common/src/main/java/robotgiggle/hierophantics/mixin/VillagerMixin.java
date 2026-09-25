package robotgiggle.hierophantics.mixin;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.server.level.ServerLevel;
import robotgiggle.hierophantics.blocks.FlayBedBlock;
import robotgiggle.hierophantics.minterface.VillagerMinterface;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class VillagerMixin implements VillagerMinterface {
    @Unique
    int hierophantics$forcedSleepStatus = 0;

    @Override
    public void hierophantics$setForcedSleepStatus(int value) {
        hierophantics$forcedSleepStatus = value;
    }
    
    @Inject(method = "stopSleeping", at = @At("HEAD"))
    private void fixScheduleAfterForcedSleep(CallbackInfo ci) {
        if (hierophantics$forcedSleepStatus > 0) {
            Villager villager = (Villager) (Object) this;
            villager.getBrain().setSchedule(hierophantics$forcedSleepStatus == 1 ? Schedule.VILLAGER_DEFAULT : Schedule.VILLAGER_BABY);
        }
    }

    @Inject(method = "stopSleeping", at = @At(value = "HEAD", shift = At.Shift.AFTER))
    private void updateBrainAfterMindImbuement(CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        if (villager.level() instanceof ServerLevel slevel) {
            if (slevel.getBlockState(villager.blockPosition()).getBlock() instanceof FlayBedBlock) {
                villager.refreshBrain(slevel);
            } 
        }
    }
}
