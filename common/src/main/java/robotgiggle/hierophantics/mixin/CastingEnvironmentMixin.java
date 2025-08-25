package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.LivingEntity;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import robotgiggle.hierophantics.Hierophantics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = CastingEnvironment.class, remap = false)
public class CastingEnvironmentMixin {
    // once hexcasting releases an update, I can use the new MEDIA_CONSUMPTION_MODIFIER attribute
    // but for now I just replicate the effect with a mixin
    @ModifyVariable(method = "extractMedia", at = @At("HEAD"), argsOnly = true)
    private long applyMediaDiscount(long original) {
        CastingEnvironment env = (CastingEnvironment) (Object) this;
        if (env.getCastingEntity() != null && env.getCastingEntity().hasStatusEffect(Hierophantics.MEDIA_DISCOUNT_EFFECT.get())) {
            return (long)Math.ceil(0.75 * (double)original);
        }
        return original;
    }
}
