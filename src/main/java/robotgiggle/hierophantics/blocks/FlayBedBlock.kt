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
import net.minecraft.block.enums.BedPart;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.BlockPos
import net.minecraft.util.DyeColor
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World
import net.minecraft.world.BlockView;
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty;
import robotgiggle.hierophantics.HierophanticsMain

class FlayBedBlock : BedBlock(DyeColor.BLACK, Settings.copy(Blocks.DEEPSLATE_TILES).strength(4f, 4f)) {
    init {
		defaultState = stateManager.defaultState
            .with(INFUSED, false)
            .with(PART, BedPart.FOOT)
            .with(OCCUPIED, false)
	}
    
    override fun createBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return FlayBedBlockEntity(blockPos, blockState);
    }

    override fun getOutlineShape(blockState: BlockState, blockView: BlockView, blockPos: BlockPos, shapeContext: ShapeContext): VoxelShape {
        return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
    }

    override fun getRenderType(blockState: BlockState): BlockRenderType {
        return BlockRenderType.MODEL;
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
		super.appendProperties(builder)
		builder.add(INFUSED)
	}

    override fun <T : BlockEntity> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T> {
        return BlockEntityTicker { world1, pos, _, blockEntity -> (blockEntity as FlayBedBlockEntity).tick(world1, pos) }
    }

    override fun onUse(blockState: BlockState, world: World, blockPos: BlockPos, playerEntity: PlayerEntity, hand: Hand, blockHitResult: BlockHitResult): ActionResult {
        if (world.isClient) return ActionResult.CONSUME;
        var sleepPos = blockPos
        if (blockState.get(PART) == BedPart.FOOT) {
            sleepPos = sleepPos.offset(blockState.get(FACING))
            if (!world.getBlockState(sleepPos).isOf(this)) {
               return ActionResult.CONSUME;
            }
        }
        if (blockState.get(OCCUPIED)) {
            playerEntity.sendMessage(Text.translatable("block.minecraft.bed.occupied"), true);
            return ActionResult.SUCCESS;
        }
        playerEntity.trySleep(sleepPos).ifLeft({sleepFailureReason -> 
            if (sleepFailureReason.getMessage() != null) {
                playerEntity.sendMessage(sleepFailureReason.getMessage(), true);
            }
        });
        return ActionResult.SUCCESS;
    }

    companion object {
        @JvmField
        val INFUSED: BooleanProperty = BooleanProperty.of("infused")
    }
}