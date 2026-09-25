package robotgiggle.hierophantics.inits

import net.minecraft.item.Item
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceKeys
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.util.Identifier

object HierophanticsItems : HierophanticsRegistrar<Item>(RegistryKeys.ITEM, { Registries.ITEM }) {
    // no items here, this just exists so that BlockItems can use its props and register() method

    val props: Item.Settings get() = Item.Settings().`arch$tab`(RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier("hexcasting:hexcasting")))
}