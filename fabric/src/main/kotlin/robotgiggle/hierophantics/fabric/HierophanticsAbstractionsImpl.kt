@file:JvmName("HierophanticsAbstractionsImpl")

package robotgiggle.hierophantics.fabric

import robotgiggle.hierophantics.inits.HierophanticsRegistrar
import net.minecraft.core.Registry

fun <T : Any> initRegistry(registrar: HierophanticsRegistrar<T>) {
    val registry = registrar.registry
    registrar.init { id, value -> Registry.register(registry, id, value) }
}