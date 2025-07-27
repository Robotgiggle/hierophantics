package robotgiggle.hierophantics.blocks

import net.minecraft.block.BlockState
import net.minecraft.block.BedBlock
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.server.world.ServerWorld
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.village.VillagerProfession
import net.minecraft.text.Text
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
import robotgiggle.hierophantics.inits.HierophanticsVillagers
import robotgiggle.hierophantics.inits.HierophanticsAdvancements
import robotgiggle.hierophantics.blocks.FlayBedBlock

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems

class FlayBedBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HierophanticsMain.FLAY_BED_BLOCK_ENTITY, pos, state) {
    val otherPartPos = pos.offset(BedBlock.getOppositePartDirection(state))
    
    fun activate(world: ServerWorld, state: BlockState, sacrifice: MobEntity, pigment: FrozenPigment) {
        if (state.get(BedBlock.OCCUPIED)) {
            val headPos = when(state.get(BedBlock.PART)!!) {
                BedPart.HEAD -> pos
                BedPart.FOOT -> otherPartPos
            }
            val players = world.getEntitiesByClass(PlayerEntity::class.java, Box(headPos)) { entity -> entity.getHeight() < 0.3 }
            val villagers = world.getEntitiesByClass(VillagerEntity::class.java, Box(headPos)) { entity -> entity.getHeight() < 0.3 }
            if (players.size > 0) {
                // sleeping entity is a player: give them a new hieromind and trigger the advancement
                val villagerName: Text? = sacrifice.getCustomName()
                if (villagerName != null)
                    HierophanticsAPI.getPlayerState(players.get(0)).addMindNamed(world.server, villagerName.getString())
                else
                    HierophanticsAPI.getPlayerState(players.get(0)).addMind(world.server)
                HierophanticsAdvancements.EMBED_MIND.trigger(players.get(0) as ServerPlayerEntity)
                
                world.playSound(null, headPos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.BLOCKS, 1.2f, 1f)
                makeParticles(world, pigment)
            } else if (villagers.size > 0) {
                // sleeping entity is a villager: increase level, merge trade offers, convert into quiltmind
                val subject = villagers.get(0)
                val data = subject.getVillagerData()
                val trades = subject.getOffers()
                
                val oldLevel = when(data.getProfession()) {
                    VillagerProfession.NONE -> 0
                    VillagerProfession.NITWIT -> 0
                    else -> data.getLevel()
                }
                subject.setVillagerData(data
                    .withLevel(oldLevel + 1)
                    .withProfession(HierophanticsVillagers.QUILTMIND)
                )
                trades.addAll((sacrifice as VillagerEntity).getOffers())
                subject.setOffers(trades)
                if (subject.getExperience() == 0) subject.setExperience(1);

                world.playSound(null, headPos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.BLOCKS, 1.2f, 1f)
                makeParticles(world, pigment)
            } else {
                HexAPI.LOGGER.warn("Imbuement Bed couldn't find sleeping entity")
                makeParticles(world, dyeColor(DyeColor.GRAY))
            }
        } else {
            val nearestPlayer = world.getClosestPlayer(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 5.0, false)
            HierophanticsAdvancements.WASTE_MIND.trigger(nearestPlayer as ServerPlayerEntity)
            makeParticles(world, dyeColor(DyeColor.RED))
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