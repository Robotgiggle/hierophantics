package robotgiggle.hierophantics.mixin;

import net.minecraft.util.Identifier;
import net.minecraft.entity.LivingEntity;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import robotgiggle.hierophantics.Hierophantics;
import robotgiggle.hierophantics.inits.HierophanticsConfig;
import robotgiggle.hierophantics.inits.HierophanticsEffects;

import static at.petrak.hexcasting.api.mod.HexConfig.noneMatch;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(value = CastingEnvironment.class, remap = false)
public class CastingEnvironmentMixin {
    @Inject(method = "precheckAction", at = @At("TAIL"))
    private void storeLastAction(PatternShapeMatch match, CallbackInfo ci, @Local Identifier key) {
        Hierophantics.lastAction = key;
    }

    @ModifyVariable(method = "extractMedia", at = @At("HEAD"), argsOnly = true)
    private long applyMediaDiscount(long original) {
        CastingEnvironment env = (CastingEnvironment) (Object) this;
        if (env instanceof PlayerBasedCastEnv 
         && env.getCastingEntity() != null 
         && env.getCastingEntity().hasStatusEffect(HierophanticsEffects.MEDIA_DISCOUNT.getValue())
         && noneMatch(HierophanticsConfig.getServer().getDiscountBlacklist(), Hierophantics.lastAction)
        ) {
            return (long)Math.ceil(HierophanticsConfig.getServer().getMediaDiscount() * (double)original);
        }
        return original;
    }
}
