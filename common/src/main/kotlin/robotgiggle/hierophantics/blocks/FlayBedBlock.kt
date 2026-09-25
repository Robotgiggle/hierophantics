package robotgiggle.hierophantics.blocks

import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.item.DyeColor
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.entity.player.Player
import net.minecraft.network.chat.Component
import net.minecraft.world.level.Level
import net.minecraft.world.level.BlockGetter

class FlayBedBlock : BedBlock(DyeColor.BLACK, Properties.copy(Blocks.DEEPSLATE_TILES).strength(4f, 4f)) {
    init {
        registerDefaultState(stateDefinition.any()
            .setValue(PART, BedPart.FOOT)
            .setValue(OCCUPIED, false)
        )
	}
    
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return FlayBedBlockEntity(blockPos, blockState)
    }

    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collCtx: CollisionContext): VoxelShape {
        return Shapes.box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun fallOn(world: Level, blockState: BlockState, blockPos: BlockPos, entity: Entity, f: Float) {
        entity.causeFallDamage(f, 1.0F, entity.damageSources().fall())
    }

    override fun updateEntityAfterFallOn(blockView: BlockGetter, entity: Entity) {
        entity.deltaMovement = entity.deltaMovement.multiply(1.0, 0.0, 1.0)
    }

    override fun hasAnalogOutputSignal(state: BlockState) = true

    override fun getAnalogOutputSignal(state: BlockState, world: Level, pos: BlockPos): Int {
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
    override fun use(blockState: BlockState, world: Level, blockPos: BlockPos, Player: Player, hand: InteractionHand, blockHitResult: BlockHitResult): InteractionResult {
        if (world.isClientSide) return InteractionResult.CONSUME
        var sleepPos = blockPos
        if (blockState.getValue(PART) == BedPart.FOOT) {
            sleepPos = sleepPos.relative(blockState.getValue(FACING))
            if (!world.getBlockState(sleepPos).`is`(this)) {
               return InteractionResult.CONSUME
            }
        }
        if (blockState.getValue(OCCUPIED)) {
            if (!wakeVillager(world, blockPos)) {
               Player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true)
            }
            return InteractionResult.SUCCESS
        }
        Player.startSleepInBed(sleepPos).ifLeft({problem ->
            if (problem.message != null) {
                Player.displayClientMessage(problem.message!!, true)
            }
        })
        return InteractionResult.SUCCESS
    }

    // this is private in BedBlock so i have to reimplement it
    fun wakeVillager(world: Level, blockPos: BlockPos): Boolean {
        val list = world.getEntitiesOfClass(Villager::class.java, AABB(blockPos), LivingEntity::isSleeping)
        if (list.isEmpty()) {
            return false
        } else {
            list[0].stopSleeping()
            return true
        }
    }
}