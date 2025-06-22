package robotgiggle.hierophantics.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.BedBlock
import net.minecraft.block.ShapeContext;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.BlockPos
import net.minecraft.util.DyeColor
import net.minecraft.world.World
import net.minecraft.world.BlockView;

class FlayBedBlock : BedBlock(DyeColor.BLACK, Settings.copy(Blocks.DEEPSLATE_TILES).strength(4f, 4f)) {
    override fun createBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return FlayBedBlockEntity(blockPos, blockState);
    }

    override fun getOutlineShape(blockState: BlockState, blockView: BlockView, blockPos: BlockPos, shapeContext: ShapeContext): VoxelShape {
        return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
    }

    override fun getRenderType(blockState: BlockState): BlockRenderType {
        return BlockRenderType.MODEL;
    }
}