package robotgiggle.hierophantics.blocks

import net.minecraft.block.BlockState
import net.minecraft.block.BedBlock
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World

import robotgiggle.hierophantics.HierophanticsMain
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.blocks.FlayBedBlock

import at.petrak.hexcasting.api.HexAPI

class FlayBedBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HierophanticsMain.FLAY_BED_BLOCK_ENTITY, pos, state) {
    fun tick(world: World, pos: BlockPos) {
        if (world.isClient()) return
        var state = world.getBlockState(pos)
        if (state.get(FlayBedBlock.INFUSED) && state.get(BedBlock.OCCUPIED)) {
            var targetPos = pos
            if (state.get(BedBlock.PART) == BedPart.FOOT) {
                targetPos = targetPos.offset(state.get(BedBlock.FACING))
            }
            val players = world.getEntitiesByClass(PlayerEntity::class.java, Box(targetPos)) { player -> player.getHeight() < 0.3 }
            if (players.isEmpty()) {
                world.setBlockState(pos, state.with(FlayBedBlock.INFUSED, false))
                HexAPI.LOGGER.warn("FlayBed can't find sleeping player")
                return
            }
            HierophanticsAPI.getPlayerState(players.get(0)).addMind()
            world.setBlockState(pos, state.with(FlayBedBlock.INFUSED, false))
        }
    }
}