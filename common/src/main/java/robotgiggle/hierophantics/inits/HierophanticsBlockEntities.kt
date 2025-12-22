package robotgiggle.hierophantics.inits

import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.util.math.BlockPos
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.Registries
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import robotgiggle.hierophantics.blocks.FlayBedBlockEntity

object HierophanticsBlockEntities : HierophanticsRegistrar<BlockEntityType<*>>(RegistryKeys.BLOCK_ENTITY_TYPE, { Registries.BLOCK_ENTITY_TYPE }) {
    @JvmField
    val FLAY_BED = register("flay_bed", ::FlayBedBlockEntity) {
        arrayOf(HierophanticsBlocks.FLAY_BED.value)
    }

    private fun <T : BlockEntity> register(
        name: String,
        func: (BlockPos, BlockState) -> T,
        blocks: () -> Array<Block>,
    ) = register(name) { IXplatAbstractions.INSTANCE.createBlockEntityType(func, *blocks()) }
}