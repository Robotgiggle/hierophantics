package robotgiggle.hierophantics.registry

import dev.architectury.platform.Platform
import robotgiggle.hierophantics.Hierophantics
import net.fabricmc.api.EnvType
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier

typealias RegistrarEntry<T> = HierophanticsRegistrar<T>.Entry<out T>

abstract class HierophanticsRegistrar<T : Any>(
    val registryKey: RegistryKey<Registry<T>>,
    getRegistry: () -> Registry<T>,
) {
    /** Do not access until the mod has been initialized! */
    val registry by lazy(getRegistry)

    private var isInitialized = false

    private val mutableEntries = mutableSetOf<Entry<out T>>()
    val entries: Set<Entry<out T>> = mutableEntries

    open fun init(registerer: (Identifier, T) -> Unit) {
        if (isInitialized) throw IllegalStateException("$this has already been initialized!")
        isInitialized = true
        for (entry in entries) {
            registerer(entry.id, entry.value)
        }
        if (Platform.getEnv() == EnvType.CLIENT) {
            initClient()
        }
    }

    open fun initClient() {}

    fun <V : T> register(name: String, builder: () -> V): Entry<V> = register(Hierophantics.id(name), builder)

    fun <V : T> register(id: Identifier, builder: () -> V): Entry<V> = register(id, lazy {
        if (!isInitialized) throw IllegalStateException("$this has not been initialized!")
        builder()
    })

    fun <V : T> register(id: Identifier, lazyValue: Lazy<V>): Entry<V> = Entry(id, lazyValue).also {
        if (!mutableEntries.add(it)) {
            throw IllegalArgumentException("Duplicate id: $id")
        }
    }

    open inner class Entry<V : T>(
        val id: Identifier,
        private val lazyValue: Lazy<V>,
    ) {
        constructor(entry: Entry<V>) : this(entry.id, entry.lazyValue)

        val key: RegistryKey<T> = RegistryKey.of(registryKey, id)

        /** Do not access until the mod has been initialized! */
        val value by lazyValue

        override fun equals(other: Any?) = when (other) {
            is HierophanticsRegistrar<*>.Entry<*> -> key.getRegistry().equals(other.key.getRegistry()) && id.equals(other.id)
            else -> false
        }

        override fun hashCode() = 31 * key.getRegistry().hashCode() + id.hashCode()
    }
}
