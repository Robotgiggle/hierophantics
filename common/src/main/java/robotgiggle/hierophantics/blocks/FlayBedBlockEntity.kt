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
import net.minecraft.village.VillagerData
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

import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.HierophanticsVillagers
import robotgiggle.hierophantics.inits.HierophanticsAdvancements
import robotgiggle.hierophantics.blocks.FlayBedBlock

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems

class FlayBedBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(Hierophantics.FLAY_BED_BLOCK_ENTITY.get(), pos, state) {
    val otherPartPos = pos.offset(BedBlock.getOppositePartDirection(state))
    val headPos = when(state.get(BedBlock.PART)!!) {
        BedPart.HEAD -> pos
        BedPart.FOOT -> otherPartPos
    }
    
    fun activate(world: ServerWorld, state: BlockState, sacrifice: VillagerEntity, pigment: FrozenPigment) {
        if (state.get(BedBlock.OCCUPIED)) {
            val subject = getSleeper(world)
            if (subject is PlayerEntity) {
                // subject is a player: give them a new hieromind and trigger the advancement
                val villagerName = sacrifice.getCustomName()?.getString()
                HieroServerState.getPlayerState(subject).addMind(world.server, villagerName)
                HierophanticsAdvancements.EMBED_MIND.trigger(subject as ServerPlayerEntity)
                
                world.playSound(null, headPos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.BLOCKS, 1.2f, 1f)
                makeParticles(world, pigment, 60)
            } else if (subject is VillagerEntity) {
                // subject is a villager: increase level, merge trade offers, convert to quiltmind if professions don't match
                val data = subject.getVillagerData()
                val trades = subject.getOffers()
                
                val newLevel = Math.min(data.getLevel() + 1, 5)
                when (canSubjectKeepProfession(data.getProfession(), sacrifice.getVillagerData().getProfession())) {
                    0 -> subject.setVillagerData(data.withLevel(newLevel))
                    1 -> subject.setVillagerData(data.withLevel(newLevel).withProfession(sacrifice.getVillagerData().getProfession()))
                    2 -> subject.setVillagerData(data.withLevel(newLevel).withProfession(HierophanticsVillagers.QUILTMIND.get()))
                }
                trades.addAll(sacrifice.getOffers())
                subject.setOffers(trades)
                subject.setExperience(VillagerData.getLowerLevelExperience(newLevel));

                val nearestPlayer = world.getClosestPlayer(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 10.0, false)
                nearestPlayer?.let{HierophanticsAdvancements.FUSE_VILLAGERS.trigger(it as ServerPlayerEntity)}
                
                world.playSound(null, headPos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.BLOCKS, 1.2f, 1f)
                makeParticles(world, pigment, 60)
            } else {
                Hierophantics.LOGGER.warn("Imbuement Bed couldn't find sleeping player or villager")
                makeParticles(world, dyeColor(DyeColor.GRAY), 80)
            }
        } else {
            val nearestPlayer = world.getClosestPlayer(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 10.0, false)
            nearestPlayer?.let{HierophanticsAdvancements.WASTE_MIND.trigger(it as ServerPlayerEntity)}
            makeParticles(world, dyeColor(DyeColor.RED), 80)
        }
    }

    fun getSleeper(world: ServerWorld): Entity? {
        val entities = world.getEntitiesByClass(Entity::class.java, Box(headPos)) { entity -> entity.getHeight() < 0.3 }
        if (entities.isEmpty()) return null
        else return entities.get(0)
    }

    fun canSubjectKeepProfession(prof1: VillagerProfession, prof2: VillagerProfession): Int {
        // blank + blank = quiltmind
        // profA + profA = profA
        // blank + profA = profA
        // profA + profB = quiltmind
        if (prof1 == prof2 && prof1 == VillagerProfession.NONE)
            return 2
        else if (prof1 == prof2 || prof2 == VillagerProfession.NONE)
            return 0
        else if (prof1 == VillagerProfession.NONE)
            return 1
        else
            return 2
    }
    
    fun dyeColor(color: DyeColor): FrozenPigment {
        return FrozenPigment(
            ItemStack(HexItems.DYE_PIGMENTS[color]!!),
            Util.NIL_UUID
        )
    }

    fun makeParticles(world: ServerWorld, color: FrozenPigment, amount: Int) {
        val adjust = Vec3d(0.0, -0.17, 0.0)
        ParticleSpray(pos.toCenterPos().add(adjust), Vec3d(0.0, 0.5, 0.0), 1.3, 0.0, amount).sprayParticles(world, color)
        ParticleSpray(otherPartPos.toCenterPos().add(adjust), Vec3d(0.0, 0.5, 0.0), 1.3, 0.0, amount).sprayParticles(world, color)
    }
}