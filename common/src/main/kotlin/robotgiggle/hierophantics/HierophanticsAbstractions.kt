@file:JvmName("HierophanticsAbstractions")

package robotgiggle.hierophantics

import dev.architectury.injectables.annotations.ExpectPlatform
import robotgiggle.hierophantics.registry.HierophanticsRegistrar

fun initRegistries(vararg registries: HierophanticsRegistrar<*>) {
    for (registry in registries) {
        initRegistry(registry)
    }
}

@ExpectPlatform
fun <T : Any> initRegistry(registrar: HierophanticsRegistrar<T>) {
    throw AssertionError()
}
