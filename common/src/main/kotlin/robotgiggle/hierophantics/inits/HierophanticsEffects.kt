package robotgiggle.hierophantics.inits

import at.petrak.hexcasting.common.lib.HexAttributes
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation
import net.minecraft.core.registries.Registries
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory

object HierophanticsEffects : HierophanticsRegistrar<MobEffect>(Registries.MOB_EFFECT, { BuiltInRegistries.MOB_EFFECT }) {
    @JvmField
    val MEDIA_DISCOUNT = register("media_discount", { MediaDiscountEffect() })
    @JvmField
    val SLEEP_ANYWHERE = register("sleep_anywhere", { SleepAnywhereEffect() })
}

class SleepAnywhereEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0)

class MediaDiscountEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0x64fbff) {
    init {
        this.addAttributeModifier(
            HexAttributes.MEDIA_CONSUMPTION_MODIFIER,
            "6845b7c1-3b9c-4164-bb37-e9d35283198b",
            (HierophanticsConfig.server.mediaDiscount - 1),
            Operation.MULTIPLY_TOTAL
        )
    }
}