package robotgiggle.hierophantics.inits

import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.Registries
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory

object HierophanticsEffects : HierophanticsRegistrar<StatusEffect>(RegistryKeys.STATUS_EFFECT, { Registries.STATUS_EFFECT }) {
    @JvmField
    val MEDIA_DISCOUNT = register("media_discount", effectSupplier(StatusEffectCategory.BENEFICIAL, 0x64fbff))
    @JvmField
    val SLEEP_ANYWHERE = register("sleep_anywhere", effectSupplier(StatusEffectCategory.BENEFICIAL, 0))

    // the StatusEffect constructor is protected, so we create an anonymous subclass and return a supplier of that
    private fun effectSupplier(category: StatusEffectCategory, color: Int): ()->StatusEffect {
        return { object : StatusEffect(category, color) {} }
    }
}