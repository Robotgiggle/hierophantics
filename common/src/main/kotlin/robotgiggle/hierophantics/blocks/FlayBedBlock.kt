package robotgiggle.hierophantics.blocks

import net.minecraft.world.level.block.state.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.BedBlock
import net.minecraft.block.ShapeContext
import net.minecraft.block.BlockRenderType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.block.enums.BedPart
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.npc.Villager
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.core.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.DyeColor
import net.minecraft.util.Hand
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.entity.player.Player
import net.minecraft.text.Text
import net.minecraft.world.level.Level
import net.minecraft.world.BlockView

class FlayBedBlock : BedBlock(DyeColor.BLACK, Settings.copy(Blocks.DEEPSLATE_TILES).strength(4f, 4f)) {
    init {
		defaultState = stateManager.defaultState
            .with(PART, BedPart.FOOT)
            .with(OCCUPIED, false)
	}
    
    override fun createBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return FlayBedBlockEntity(blockPos, blockState)
    }

    override fun getOutlineShape(blockState: BlockState, blockView: BlockView, blockPos: BlockPos, shapeContext: ShapeContext): VoxelShape {
        return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
    }

    override fun getRenderType(blockState: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun onLandedUpon(world: Level, blockState: BlockState, blockPos: BlockPos, entity: Entity, f: Float) {
        entity.handleFallDamage(f, 1.0F, entity.getDamageSources().fall())
    }

    override fun onEntityLand(blockView: BlockView, entity: Entity) {
        entity.setVelocity(entity.getVelocity().multiply(1.0, 0.0, 1.0))
    }

    override fun hasComparatorOutput(state: BlockState) = true

    override fun getComparatorOutput(state: BlockState, world: Level, pos: BlockPos): Int {
        val be = world.getBlockEntity(pos)
        if (be is FlayBedBlockEntity) {
            return be.comparatorOutput
        }
        return 0
    }

    override fun <T : BlockEntity> getTicker(world: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T> {
        return BlockEntityTicker { _world, _pos, _state, blockEntity -> (blockEntity as FlayBedBlockEntity).tick(_world, _pos, _state) }
    }

    // this is identical to BedBlock except it doesn't try to explode in other dims
    override fun onUse(blockState: BlockState, world: Level, blockPos: BlockPos, Player: Player, hand: Hand, blockHitResult: BlockHitResult): ActionResult {
        if (world.isClientSide) return ActionResult.CONSUME
        var sleepPos = blockPos
        if (blockState.get(PART) == BedPart.FOOT) {
            sleepPos = sleepPos.offset(blockState.get(FACING))
            if (!world.getBlockState(sleepPos).isOf(this)) {
               return ActionResult.CONSUME
            }
        }
        if (blockState.get(OCCUPIED)) {
            if (!wakeVillager(world, blockPos)) {
               Player.sendMessage(Text.translatable("block.minecraft.bed.occupied"), true)
            }
            return ActionResult.SUCCESS
        }
        Player.trySleep(sleepPos).ifLeft({sleepFailureReason -> 
            if (sleepFailureReason.getMessage() != null) {
                Player.sendMessage(sleepFailureReason.getMessage(), true)
            }
        })
        return ActionResult.SUCCESS
    }

    // this is private in BedBlock so i have to reimplement it
    fun wakeVillager(world: Level, blockPos: BlockPos): Boolean {
        val list = world.getEntitiesByClass(Villager::class.java, Box(blockPos), LivingEntity::isSleeping)
        if (list.isEmpty()) {
            return false
        } else {
            (list.get(0)).wakeUp()
            return true
        }
    }
}