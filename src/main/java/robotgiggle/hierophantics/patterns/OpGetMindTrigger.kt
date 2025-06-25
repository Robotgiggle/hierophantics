package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.iotas.TriggerIota
import robotgiggle.hierophantics.iotas.MindReferenceIota
import robotgiggle.hierophantics.iotas.getMindReference
import robotgiggle.hierophantics.mishaps.*
import net.minecraft.server.network.ServerPlayerEntity

import at.petrak.hexcasting.api.HexAPI

class OpGetMindTrigger : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity
		val mindIota = args.getMindReference(0, argc)

		if (caster == null || caster !is ServerPlayerEntity || mindIota.host != caster) {
			throw NotYourMindMishap()
		}

		val state = HierophanticsAPI.getPlayerState(caster)
		if (!state.hasMind(mindIota.name)) {
			throw MindFreedMishap()
		}
		if (state.disabled) {
			throw MindsDisabledMishap()
		}

        val mind = state.hieroMinds[mindIota.name]!!
        if (mind.triggerId == -1) {
            return listOf(NullIota())
        } else {
            return listOf(TriggerIota(mind.triggerId, mind.triggerThreshold, mind.triggerDmgType))
        }
    }
}