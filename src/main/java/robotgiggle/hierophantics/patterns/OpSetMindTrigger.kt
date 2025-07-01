package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.misc.MediaConstants
import robotgiggle.hierophantics.data.PlayerState
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.iotas.TriggerIota
import robotgiggle.hierophantics.iotas.getTrigger
import robotgiggle.hierophantics.iotas.MindReferenceIota
import robotgiggle.hierophantics.iotas.getMindReference
import robotgiggle.hierophantics.mishaps.*
import net.minecraft.server.network.ServerPlayerEntity

import at.petrak.hexcasting.api.HexAPI

class OpSetMindTrigger : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val caster = env.castingEntity
		val mind = args.getMindReference(0, argc)

		if (caster == null || caster !is ServerPlayerEntity || mind.host != caster) {
			throw NotYourMindMishap()
		}

		val state = HierophanticsAPI.getPlayerState(caster)
		if (!state.hasMind(mind.name)) {
			throw MindFreedMishap()
		}
		if (state.disabled) {
			throw MindsDisabledMishap()
		}

		// second argument should be either a trigger or null, mishap otherwise
		if (args[1] !is NullIota) {
			args.getTrigger(1, argc)
		}

		return SpellAction.Result(
			Spell(mind, args[1], state),
			MediaConstants.SHARD_UNIT,
			listOf()
		)
	}
	private data class Spell(val mind: MindReferenceIota, val triggerOrNull: Iota, val state: PlayerState) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (triggerOrNull is NullIota) {
                state.hieroMinds[mind.name]!!.triggerId = -1
                state.hieroMinds[mind.name]!!.triggerThreshold = -1.0
                state.hieroMinds[mind.name]!!.triggerDmgType = ""
			} else {
				val trigger = triggerOrNull as TriggerIota
                state.hieroMinds[mind.name]!!.triggerId = trigger.triggerId
                state.hieroMinds[mind.name]!!.triggerThreshold = trigger.threshold
                state.hieroMinds[mind.name]!!.triggerDmgType = trigger.dmgType
			}
		}
	}
}