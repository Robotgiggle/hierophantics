package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.npc.Villager
import net.minecraft.entity.ai.brain.Activity
import net.minecraft.world.entity.schedule.Schedule
import net.minecraft.world.entity.schedule.ScheduleBuilder
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.text.Text
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.inits.HierophanticsConfig
import robotgiggle.hierophantics.inits.HierophanticsEffects
import robotgiggle.hierophantics.minterface.VillagerMinterface
import robotgiggle.hierophantics.mixin.accessor.PlayerAccessor

object OpVillagerSleep : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        env.assertEntityInRange(target)

        if (!(target is Villager || target is Player && playerIsSleepable(target))) {
            throw MishapBadEntity(target, Text.translatable("hexcasting.mishap.invalid_value.class.entity.villager"))
        }
        
        return SpellAction.Result(
			Spell(target),
			MediaConstants.CRYSTAL_UNIT,
			listOf()
		)
    }
    fun playerIsSleepable(player: Player): Boolean {
        return HierophanticsConfig.server.playerSleepSpell 
            && HieroServerState.getPlayerState(player).ownedMinds > 0
    }
    private data class Spell(val target: LivingEntity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            if (target is Villager) {
                val now = (env.getWorld().getTimeOfDay() % 24000).toInt()
                val forcedSleepSched = ScheduleBuilder(Schedule())
                    .withActivity(now, Activity.REST)
                    .withActivity(now + 600, Activity.IDLE)
                    .build()
                target.brain.setSchedule(forcedSleepSched)
                (target as VillagerMinterface).`hierophantics$setForcedSleepStatus`(if (target.isBaby()) 2 else 1)
            } else if (target is Player) {
                if (env.getWorld().isNight) {
                    target.sleep(target.blockPosition())
                    (target as PlayerAccessor).setSleepCounter(0)
                    target.addStatusEffect(StatusEffectInstance(HierophanticsEffects.SLEEP_ANYWHERE.value, 300, 0, false, false))
                    env.getWorld().updateSleepingPlayers()
                }
            }
        }
    }
}