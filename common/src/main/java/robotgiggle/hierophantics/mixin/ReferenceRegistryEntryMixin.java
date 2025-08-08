package robotgiggle.hierophantics.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.world.poi.PointOfInterestTypes;

import robotgiggle.hierophantics.HierophanticsVillagers;
import robotgiggle.hierophantics.Hierophantics;

@Mixin(Reference.class)
public class ReferenceRegistryEntryMixin {
    // why on earth isn't there a better way to make something count as a bed for villagers
    @Inject(method = "matchesKey", at = @At("HEAD"), cancellable = true)
    private void flayBedIsVillagerHome(RegistryKey<?> key, CallbackInfoReturnable<Boolean> ci) {
        Reference<?> entry = (Reference<?>) (Object) this;
        if (key == PointOfInterestTypes.HOME && entry.registryKey() == HierophanticsVillagers.FLAY_BED_POI_KEY) {
            ci.setReturnValue(true);
        }
    }
}
