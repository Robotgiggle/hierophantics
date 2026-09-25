package robotgiggle.hierophantics.blocks

import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.animal.allay.Allay
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.npc.VillagerData
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.item.trading.MerchantOffers
import net.minecraft.world.item.trading.MerchantOffer
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.AABB
import net.minecraft.Util
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource

import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.HierophanticsVillagers
import robotgiggle.hierophantics.inits.HierophanticsEffects
import robotgiggle.hierophantics.inits.HierophanticsAdvancements
import robotgiggle.hierophantics.inits.HierophanticsBlockEntities
import robotgiggle.hierophantics.inits.BaseCriterion
import robotgiggle.hierophantics.networking.msg.MsgOwnedMindsS2C
import robotgiggle.hierophantics.networking.msg.MsgHallucinationTriggerS2C

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import kotlin.math.pow

class FlayBedBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HierophanticsBlockEntities.FLAY_BED.value, pos, state) {
    val otherPartPos = pos.relative(BedBlock.getConnectedDirection(state))
    val headPos = when(state.getValue(BedBlock.PART)!!) {
        BedPart.HEAD -> pos
        BedPart.FOOT -> otherPartPos
    }
    var comparatorOutput = 0

    fun tick(world: Level, pos: BlockPos, state: BlockState) {
        if (comparatorOutput == 0 && state.getValue(BedBlock.OCCUPIED)) {
            if (getSleeper(world) is Player) comparatorOutput = 15
            else if (getSleeper(world) is Villager) comparatorOutput = 7
            world.updateNeighbourForOutputSignal(pos, state.getBlock())
        } else if (comparatorOutput > 0 && !state.getValue(BedBlock.OCCUPIED)) {
            comparatorOutput = 0
            world.updateNeighbourForOutputSignal(pos, state.getBlock())
        }
    }
    
    fun activate(world: ServerLevel, state: BlockState, sacrifice: Mob, pigment: FrozenPigment): Boolean {
        if (state.getValue(BedBlock.OCCUPIED)) {
            val subject = getSleeper(world)

            // make sure you aren't flaying something into itself
            if (subject == sacrifice) {
                triggerForNearestPlayer(HierophanticsAdvancements.FUSE_TO_SELF, world)
                world.playSound(null, headPos, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.BLOCKS, 1.2f, 1f)
                makeParticles(world, pigment, 60)
                return false
            }

            if (subject is ServerPlayer && sacrifice is Villager) {
                // villager -> player: give the player a new hieromind and trigger the advancement
                val villagerName = sacrifice.getCustomName()?.getString()
                val newTotal = HieroServerState.getPlayerState(subject).addMind(world.server, villagerName)
                MsgOwnedMindsS2C(newTotal).sendToPlayer(subject)
                MsgHallucinationTriggerS2C(3.0).sendToPlayer(subject)
                
                HierophanticsAdvancements.EMBED_MIND.trigger(subject)
                
                world.playSound(null, headPos, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.BLOCKS, 1.2f, 1f)
                makeParticles(world, pigment, 60)
            } else if (subject is ServerPlayer && sacrifice is Allay) {
                // allay -> player: apply or lengthen media discount effect and trigger the advancement
                if (subject.hasEffect(HierophanticsEffects.MEDIA_DISCOUNT.value)) {
                    val oldTicks = subject.getEffect(HierophanticsEffects.MEDIA_DISCOUNT.value)!!.duration
                    val newTicks = (6000 * Math.E.pow((-oldTicks / 12000).toDouble())).toInt()
                    subject.removeEffect(HierophanticsEffects.MEDIA_DISCOUNT.value)
                    subject.addEffect(MobEffectInstance(HierophanticsEffects.MEDIA_DISCOUNT.value, newTicks + oldTicks))
                } else {
                    subject.addEffect(MobEffectInstance(HierophanticsEffects.MEDIA_DISCOUNT.value, 6000))
                }

                HierophanticsAdvancements.EMBED_MIND.trigger(subject)
                
                world.playSound(null, headPos, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.BLOCKS, 1.2f, 1f)
                makeParticles(world, pigment, 60)
            } else if (subject is Villager && sacrifice is Villager) {
                // villager -> villager: increase level, merge trade offers, convert to quiltmind if professions don't match
                val data = subject.getVillagerData()
                val trades = subject.getOffers()
                
                val newLevel = (data.level + 1).coerceAtMost(5)
                when (canSubjectKeepProfession(data.profession, sacrifice.villagerData.profession)) {
                    0 -> subject.villagerData = data.setLevel(newLevel)
                    1 -> subject.villagerData = data.setLevel(newLevel).setProfession(sacrifice.villagerData.profession)
                    2 -> subject.villagerData = data.setLevel(newLevel).setProfession(HierophanticsVillagers.QUILTMIND.get())
                }
                mergeTradeLists(trades, sacrifice.getOffers())
                subject.setOffers(trades)
                subject.villagerXp = VillagerData.getMinXpPerLevel(newLevel)

                triggerForNearestPlayer(HierophanticsAdvancements.WASTE_MIND, world)
                
                world.playSound(null, headPos, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.BLOCKS, 1.2f, 1f)
                makeParticles(world, pigment, 60)
            } else {
                Hierophantics.LOGGER.warn("Imbuement Bed couldn't find sleeping player or villager")
                makeParticles(world, dyeColor(DyeColor.GRAY), 80)
            }
        } else {
            triggerForNearestPlayer(HierophanticsAdvancements.WASTE_MIND, world)
            makeParticles(world, dyeColor(DyeColor.RED), 80)
        }
        return true
    }

    fun getSleeper(world: Level): Entity? {
        val entities = world.getEntitiesOfClass(Entity::class.java, AABB(headPos)) { entity -> entity.bbHeight < 0.3 }
        if (entities.isEmpty()) return null
        return entities[0]
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

    fun mergeTradeLists(existingList: MerchantOffers, incomingList: MerchantOffers) {
        for (newOffer in incomingList) {
            var merged = false

            // if the incoming offer matches an existing one, merge them into one offer with more max uses
            for ((i, oldOffer) in existingList.withIndex()) {
                if (ItemStack.matches(oldOffer.baseCostA, newOffer.baseCostA)
                 && ItemStack.matches(oldOffer.costB, newOffer.costB)
                 && ItemStack.matches(oldOffer.result, newOffer.result)
                ) {
                    existingList[i] = MerchantOffer(
                        oldOffer.baseCostA, oldOffer.costB, oldOffer.result,
                        oldOffer.maxUses + newOffer.maxUses,
                        oldOffer.xp, oldOffer.priceMultiplier
                    )
                    merged = true
                    break
                }
            }

            // if no match can be found, just append the incoming offer to the list
            if (!merged) {
                existingList.add(newOffer)
            }
        }
    }

    fun triggerForNearestPlayer(adv: BaseCriterion<*>, world: ServerLevel) {
        val nearestPlayer = world.getNearestPlayer(
            worldPosition.x.toDouble(),
            worldPosition.y.toDouble(),
            worldPosition.z.toDouble(),
            10.0, false)
        nearestPlayer?.let{ adv.trigger(it as ServerPlayer) }
    }
    
    fun dyeColor(color: DyeColor): FrozenPigment {
        return FrozenPigment(
            ItemStack(HexItems.DYE_PIGMENTS[color]!!),
            Util.NIL_UUID
        )
    }

    fun makeParticles(world: ServerLevel, color: FrozenPigment, amount: Int) {
        val adjust = Vec3(0.0, -0.17, 0.0)
        ParticleSpray(worldPosition.center.add(adjust), Vec3(0.0, 0.5, 0.0), 1.3, 0.0, amount).sprayParticles(world, color)
        ParticleSpray(otherPartPos.center.add(adjust), Vec3(0.0, 0.5, 0.0), 1.3, 0.0, amount).sprayParticles(world, color)
    }
}