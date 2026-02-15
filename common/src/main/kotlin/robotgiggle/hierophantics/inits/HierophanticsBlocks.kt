package robotgiggle.hierophantics.inits

import at.petrak.hexcasting.common.lib.HexBlocks
import net.minecraft.item.Item
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemConvertible
import net.minecraft.block.Block
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.Registries
import robotgiggle.hierophantics.blocks.*

object HierophanticsBlocks : HierophanticsRegistrar<Block>(RegistryKeys.BLOCK, { Registries.BLOCK }) {
    @JvmField
    val FLAY_BED = blockItem("flay_bed", HierophanticsItems.props, { FlayBedBlock() } )

    @JvmField 
    val EDIFIED_WORKSTATION = blockItem("edified_workstation", HierophanticsItems.props, { Block(Settings.copy(HexBlocks.EDIFIED_PLANKS)) })

    private fun <T : Block> blockItem(name: String, props: Item.Settings, builder: () -> T) =
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
    ) : Entry<B>(blockEntry), ItemConvertible {
        val block by ::value
        val item by itemEntry::value
        val itemKey by itemEntry::key

        override fun asItem() = item
    }
}