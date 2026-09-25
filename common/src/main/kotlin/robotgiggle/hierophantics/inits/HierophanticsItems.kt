package robotgiggle.hierophantics.inits

import net.minecraft.world.item.Item
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

object HierophanticsItems : HierophanticsRegistrar<Item>(Registries.ITEM, { BuiltInRegistries.ITEM }) {
    // no items here, this just exists so that BlockItems can use its props and register() method

    val props: Item.Properties get() = Item.Properties().`arch$tab`(
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation("hexcasting:hexcasting"))
    )
}