package robotgiggle.hierophantics.inits

import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.Hierophantics.id
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.SoundEvent

// import dev.architectury.registry.registries.DeferredRegister
// import dev.architectury.registry.registries.RegistrySupplier

object HierophanticsSounds : HierophanticsRegistrar<SoundEvent>(RegistryKeys.SOUND_EVENT, { Registries.SOUND_EVENT }) {
	val HIEROMIND_CAST = register("hieromind_cast")

	private fun register(name: String): HierophanticsRegistrar<SoundEvent>.Entry<SoundEvent> {
		val event = SoundEvent.of(id(name))
		return register(name, { event })
	}
}

// object HierophanticsSounds {
// 	val SOUNDS: DeferredRegister<SoundEvent> = DeferredRegister.create(Hierophantics.MOD_ID, RegistryKeys.SOUND_EVENT)

// 	val HIEROMIND_CAST: RegistrySupplier<SoundEvent> = register("hieromind_cast")

// 	fun init() {
// 		SOUNDS.register()
// 	}

// }