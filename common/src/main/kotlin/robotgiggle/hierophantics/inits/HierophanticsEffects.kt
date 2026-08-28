package robotgiggle.hierophantics.inits

import at.petrak.hexcasting.common.lib.HexAttributes
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.Registries
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory

object HierophanticsEffects : HierophanticsRegistrar<StatusEffect>(RegistryKeys.STATUS_EFFECT, { Registries.STATUS_EFFECT }) {
    @JvmField
    val MEDIA_DISCOUNT = register("media_discount", { MediaDiscountEffect() })
    @JvmField
    val SLEEP_ANYWHERE = register("sleep_anywhere", { SleepAnywhereEffect() })
}

class SleepAnywhereEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0)

class MediaDiscountEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0x64fbff) {
    init {
        this.addAttributeModifier(
            HexAttributes.MEDIA_CONSUMPTION_MODIFIER,
            "6845b7c1-3b9c-4164-bb37-e9d35283198b",
            (HierophanticsConfig.server.mediaDiscount - 1),
            Operation.MULTIPLY_TOTAL
        )
    }
}