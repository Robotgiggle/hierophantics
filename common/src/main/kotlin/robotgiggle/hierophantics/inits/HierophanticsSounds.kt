package robotgiggle.hierophantics.inits

import robotgiggle.hierophantics.Hierophantics.id
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent

object HierophanticsSounds : HierophanticsRegistrar<SoundEvent>(Registries.SOUND_EVENT, { BuiltInRegistries.SOUND_EVENT }) {
	val HIEROMIND_CAST = register("hieromind_cast")

	private fun register(name: String): HierophanticsRegistrar<SoundEvent>.Entry<SoundEvent> {
		val event = SoundEvent.createVariableRangeEvent(id(name))
		return register(name, { event })
	}
}