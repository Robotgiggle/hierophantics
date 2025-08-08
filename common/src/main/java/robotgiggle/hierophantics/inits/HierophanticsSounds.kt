package robotgiggle.hierophantics.inits

import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.Hierophantics.id
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.SoundEvent

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier

object HierophanticsSounds {
	val SOUNDS: DeferredRegister<SoundEvent> = DeferredRegister.create(Hierophantics.MOD_ID, RegistryKeys.SOUND_EVENT)

	val HIEROMIND_CAST: RegistrySupplier<SoundEvent> = register("hieromind_cast")

	fun init() {
		SOUNDS.register()
	}

	private fun register(name: String): RegistrySupplier<SoundEvent> {
		val event = SoundEvent.of(id(name))
		return SOUNDS.register(name, {-> event})
	}
}