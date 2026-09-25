package robotgiggle.hierophantics.inits

import robotgiggle.hierophantics.Hierophantics.id
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKeys
import net.minecraft.sounds.SoundEvent

object HierophanticsSounds : HierophanticsRegistrar<SoundEvent>(RegistryKeys.SOUND_EVENT, { Registries.SOUND_EVENT }) {
	val HIEROMIND_CAST = register("hieromind_cast")

	private fun register(name: String): HierophanticsRegistrar<SoundEvent>.Entry<SoundEvent> {
		val event = SoundEvent.of(id(name))
		return register(name, { event })
	}
}