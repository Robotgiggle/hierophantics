@file:JvmName("HierophanticsAbstractionsImpl")

package robotgiggle.hierophantics.forge

import robotgiggle.hierophantics.inits.HierophanticsRegistrar
import net.minecraftforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

fun <T : Any> initRegistry(registrar: HierophanticsRegistrar<T>) {
    MOD_BUS.addListener { event: RegisterEvent ->
        event.register(registrar.registryKey) { helper ->
            registrar.init(helper::register)
        }
    }
}