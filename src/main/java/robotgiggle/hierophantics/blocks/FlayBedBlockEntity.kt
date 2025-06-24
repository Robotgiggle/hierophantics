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
import net.minecraft.util.math.Vec3d
import net.minecraft.util.Util
import net.minecraft.util.DyeColor
import net.minecraft.world.World
import net.minecraft.sound.SoundEvents
import net.minecraft.sound.SoundCategory

import robotgiggle.hierophantics.HierophanticsMain
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.blocks.FlayBedBlock

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import at.petrak.hexcasting.common.lib.HexItems

class FlayBedBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HierophanticsMain.FLAY_BED_BLOCK_ENTITY, pos, state) {
    val otherPartPos = pos.offset(BedBlock.getOppositePartDirection(state))
    
    fun tick(world: World, pos: BlockPos) {
        if (world.isClient()) return
        var state = world.getBlockState(pos)
        if (state.get(FlayBedBlock.INFUSED)) {
            if (state.get(BedBlock.OCCUPIED)) {
                val targetPos = when(state.get(BedBlock.PART)!!) {
                    BedPart.HEAD -> pos
                    BedPart.FOOT -> otherPartPos
                }
                val players = world.getEntitiesByClass(PlayerEntity::class.java, Box(targetPos)) { player -> player.getHeight() < 0.3 }
                if (players.isEmpty()) {
                    HexAPI.LOGGER.warn("FlayBed can't find sleeping player")
                    makeParticles(world as ServerWorld, dyeColor(DyeColor.RED))
                } else {
                    HierophanticsAPI.getPlayerState(players.get(0)).addMind()
                    world.playSound(null, targetPos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.BLOCKS, 1.2f, 1f)
                    makeParticles(world as ServerWorld, IXplatAbstractions.INSTANCE.getPigment(players.get(0)))
                }
            } else {
                makeParticles(world as ServerWorld, dyeColor(DyeColor.RED))
            }
            world.setBlockState(pos, state.with(FlayBedBlock.INFUSED, false))
        }
    }
    
    fun dyeColor(color: DyeColor): FrozenPigment {
        return FrozenPigment(
            ItemStack(HexItems.DYE_PIGMENTS[color]!!),
            Util.NIL_UUID
        )
    }

    fun makeParticles(world: ServerWorld, color: FrozenPigment) {
        val adjust = Vec3d(0.0, -0.17, 0.0)
        ParticleSpray(pos.toCenterPos().add(adjust), Vec3d(0.0, 0.5, 0.0), 1.3, 0.0, 60).sprayParticles(world, color)
        ParticleSpray(otherPartPos.toCenterPos().add(adjust), Vec3d(0.0, 0.5, 0.0), 1.3, 0.0, 60).sprayParticles(world, color)
    }
}