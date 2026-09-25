package robotgiggle.hierophantics.inits

import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import robotgiggle.hierophantics.blocks.FlayBedBlockEntity

object HierophanticsBlockEntities : HierophanticsRegistrar<BlockEntityType<*>>(Registries.BLOCK_ENTITY_TYPE, { BuiltInRegistries.BLOCK_ENTITY_TYPE }) {
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