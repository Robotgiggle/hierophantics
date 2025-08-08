package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.misc.MediaConstants
import robotgiggle.hierophantics.data.HieroPlayerState
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.inits.HierophanticsAdvancements
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
		val mindRef = args.getMindReference(0, argc)

		if (caster == null || caster !is ServerPlayerEntity || mindRef.host != caster) {
			throw NotYourMindMishap()
		}

		val state = HieroServerState.getPlayerState(caster)
		if (!state.hasMind(mindRef.name)) {
			throw MindFreedMishap()
		}
		if (state.disabled) {
			throw MindsDisabledMishap("write")
		}

		// second argument should be either a trigger or null, mishap otherwise
		if (args[1] !is NullIota) {
			args.getTrigger(1, argc)
		}

		return SpellAction.Result(
			Spell(mindRef, args[1], state, caster),
			MediaConstants.SHARD_UNIT,
			listOf()
		)
	}
	private data class Spell(val mindRef: MindReferenceIota, val triggerOrNull: Iota, val state: HieroPlayerState, val caster: ServerPlayerEntity) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val mind = state.getMind(mindRef.name)
			if (triggerOrNull is NullIota) {
                mind.trigger = "none"
                mind.triggerThreshold = -1.0
                mind.triggerDmgType = ""
			} else {
				val trigger = triggerOrNull as TriggerIota
                mind.trigger = trigger.trigger
                mind.triggerThreshold = trigger.threshold
                mind.triggerDmgType = trigger.dmgType
				if (state.allTriggersUsed()) {
					HierophanticsAdvancements.ALL_TRIGGERS.trigger(caster)
				}
			}
		}
	}
}