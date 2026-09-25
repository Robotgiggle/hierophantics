package robotgiggle.hierophantics.inits

import at.petrak.hexcasting.common.lib.HexBlocks
import net.minecraft.world.item.Item
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.core.registries.Registries
import net.minecraft.core.registries.BuiltInRegistries
import robotgiggle.hierophantics.blocks.*

object HierophanticsBlocks : HierophanticsRegistrar<Block>(Registries.BLOCK, { BuiltInRegistries.BLOCK }) {
    @JvmField
    val FLAY_BED = blockItem("flay_bed", HierophanticsItems.props, { FlayBedBlock() } )

    @JvmField 
    val EDIFIED_WORKSTATION = blockItem("edified_workstation", HierophanticsItems.props, { Block(Properties.copy(HexBlocks.EDIFIED_PLANKS)) })

    private fun <T : Block> blockItem(name: String, props: Item.Properties, builder: () -> T) =
        blockItem(name, builder) { BlockItem(it, props) }

    private fun <B : Block, I : Item> blockItem(
        name: String,
        blockBuilder: () -> B,
        itemBuilder: (B) -> I,
    ): BlockItemEntry<B, I> {
        val blockEntry = register(name, blockBuilder)
        val itemEntry = HierophanticsItems.register(name) { itemBuilder(blockEntry.value) }
        return BlockItemEntry(blockEntry, itemEntry)
    }

    class BlockItemEntry<B : Block, I : Item>(
        blockEntry: Entry<B>,
        val itemEntry: HierophanticsRegistrar<Item>.Entry<I>,
    ) : Entry<B>(blockEntry), ItemLike {
        val block by ::value
        val item by itemEntry::value
        val itemKey by itemEntry::key

        override fun asItem() = item
    }
}