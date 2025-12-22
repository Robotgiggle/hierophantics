package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.LivingEntity;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import robotgiggle.hierophantics.Hierophantics;
import robotgiggle.hierophantics.inits.HierophanticsConfig;
import robotgiggle.hierophantics.inits.HierophanticsEffects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = CastingEnvironment.class, remap = false)
public class CastingEnvironmentMixin {
    @ModifyVariable(method = "extractMedia", at = @At("HEAD"), argsOnly = true)
    private long applyMediaDiscount(long original) {
        CastingEnvironment env = (CastingEnvironment) (Object) this;
        if (env instanceof PlayerBasedCastEnv 
         && env.getCastingEntity() != null 
         && env.getCastingEntity().hasStatusEffect(HierophanticsEffects.MEDIA_DISCOUNT.getValue())
        ) {
            return (long)Math.ceil(HierophanticsConfig.getServer().getMediaDiscount() * (double)original);
        }
        return original;
    }
}
