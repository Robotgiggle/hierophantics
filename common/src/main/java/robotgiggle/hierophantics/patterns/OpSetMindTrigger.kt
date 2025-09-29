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
			Spell(state, mindRef.name, args[1], caster),
			MediaConstants.SHARD_UNIT,
			listOf()
		)
	}
	private data class Spell(val state: HieroPlayerState, val mindName: String, val triggerOrNull: Iota, val caster: ServerPlayerEntity) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val mind = state.getMind(mindName)
			if (triggerOrNull is NullIota) {
                mind.trigger = TriggerIota.Trigger.none()
			} else {
				val triggerIota = triggerOrNull as TriggerIota
                mind.trigger = triggerIota.trigger
				if (state.allTriggersUsed()) {
					HierophanticsAdvancements.ALL_TRIGGERS.trigger(caster)
				}
			}
		}
	}
}