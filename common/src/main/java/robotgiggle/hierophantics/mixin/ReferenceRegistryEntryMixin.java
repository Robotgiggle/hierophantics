package robotgiggle.hierophantics.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

import robotgiggle.hierophantics.HierophanticsVillagers;

@Mixin(Reference.class)
public class ReferenceRegistryEntryMixin {
    // why on earth isn't there a better way to make something count as a bed for villagers
    @Inject(method = "is", at = @At("HEAD"), cancellable = true)
    private void flayBedIsVillagerHome(ResourceKey<?> key, CallbackInfoReturnable<Boolean> ci) {
        Reference<?> entry = (Reference<?>) (Object) this;
        if (key == PoiTypes.HOME && entry.key() == HierophanticsVillagers.FLAY_BED_POI_KEY) {
            ci.setReturnValue(true);
        }
    }
}
