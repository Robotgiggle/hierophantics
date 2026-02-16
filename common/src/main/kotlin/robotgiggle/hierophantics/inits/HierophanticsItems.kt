package robotgiggle.hierophantics.inits

import net.minecraft.item.Item
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

object HierophanticsItems : HierophanticsRegistrar<Item>(RegistryKeys.ITEM, { Registries.ITEM }) {
    // no items here, this just exists so that BlockItems can use its props and register() method

    val props: Item.Settings get() = Item.Settings().`arch$tab`(RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier("hexcasting:hexcasting")))
}