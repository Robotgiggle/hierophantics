package robotgiggle.hierophantics.inits

import robotgiggle.hierophantics.HierophanticsUtils.id
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent

object HierophanticsSounds {
	// lateinit var CASSETTE_EJECT: SoundEvent
	// lateinit var CASSETTE_FAIL: SoundEvent
	// lateinit var CASSETTE_INSERT: SoundEvent
	// lateinit var CASSETTE_LOOP: SoundEvent

	fun init() {
		// CASSETTE_EJECT = register("cassette_eject")
		// CASSETTE_FAIL = register("cassette_fail")
		// CASSETTE_INSERT = register("cassette_insert")
		// CASSETTE_LOOP = register("cassette_loop")
	}

	private fun register(name: String): SoundEvent {
		val event = SoundEvent.of(id(name))
		Registry.register(Registries.SOUND_EVENT, id(name), event)
		return event
	}
}