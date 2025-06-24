package robotgiggle.hierophantics.inits

import robotgiggle.hierophantics.HierophanticsUtils.id
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent

object HierophanticsSounds {
	lateinit var HIEROMIND_CAST: SoundEvent

	fun init() {
		HIEROMIND_CAST = register("hieromind_cast")
	}

	private fun register(name: String): SoundEvent {
		val event = SoundEvent.of(id(name))
		Registry.register(Registries.SOUND_EVENT, id(name), event)
		return event
	}
}