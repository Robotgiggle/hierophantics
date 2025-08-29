package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.ai.brain.Activity
import net.minecraft.entity.ai.brain.Schedule
import net.minecraft.entity.ai.brain.ScheduleBuilder
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.text.Text
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.inits.HierophanticsConfig
import robotgiggle.hierophantics.minterface.VillagerEntityMinterface
import robotgiggle.hierophantics.mixin.accessor.PlayerEntityAccessor

class OpVillagerSleep : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        env.assertEntityInRange(target)

        if (!(target is VillagerEntity || target is PlayerEntity && playerIsSleepable(target))) {
            throw MishapBadEntity(target, Text.translatable("hexcasting.mishap.invalid_value.class.entity.villager"))
        }
        
        return SpellAction.Result(
			Spell(target),
			MediaConstants.CRYSTAL_UNIT,
			listOf()
		)
    }
    fun playerIsSleepable(player: PlayerEntity): Boolean {
        return HierophanticsConfig.server.playerSleepSpell 
            && HieroServerState.getPlayerState(player).ownedMinds > 0
    }
    private data class Spell(val target: LivingEntity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            if (target is VillagerEntity) {
                val now = (env.getWorld().getTimeOfDay() % 24000).toInt()
                val forcedSleepSched = ScheduleBuilder(Schedule())
                    .withActivity(now, Activity.REST)
                    .withActivity(now + 300, Activity.IDLE)
                    .build()
                target.brain.setSchedule(forcedSleepSched)
                (target as VillagerEntityMinterface).setForcedSleepStatus(if (target.isBaby()) 2 else 1)
            } else if (target is PlayerEntity) {
                if (env.getWorld().isNight) {
                    target.sleep(target.getBlockPos())
                    (target as PlayerEntityAccessor).setSleepTimer(0)
                    target.addStatusEffect(StatusEffectInstance(Hierophantics.SLEEP_ANYWHERE_EFFECT.get(), 300, 0, false, false))
                    env.getWorld().updateSleepingPlayers()
                }
            }
        }
    }
}