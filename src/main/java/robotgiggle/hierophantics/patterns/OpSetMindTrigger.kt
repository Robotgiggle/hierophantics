package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.iotas.TriggerIota
import robotgiggle.hierophantics.iotas.getTrigger
import robotgiggle.hierophantics.iotas.MindReferenceIota
import robotgiggle.hierophantics.iotas.getMindReference
import net.minecraft.server.network.ServerPlayerEntity

import at.petrak.hexcasting.api.HexAPI

class OpSetMindTrigger : ConstMediaAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity
        if (caster != null && caster is ServerPlayerEntity) {
            val mindIota = args.getMindReference(0, argc)
            if (mindIota.host != caster) {
                // TODO: throw NotYourMindMishap
                return emptyList()
            }
            val state = HierophanticsAPI.getPlayerState(caster)
            if (!state.hasMind(mindIota.mindId)) {
                // TODO: throw MindFreedMishap
                return emptyList()
            }
            if (state.disabled) {
				// TODO: throw MindsDisabledMishap
				return emptyList()
			}
            if (args[1] is NullIota) {
                state.hieroMinds[mindIota.mindId]!!.triggerId = -1
                state.hieroMinds[mindIota.mindId]!!.triggerThreshold = -1.0
                state.hieroMinds[mindIota.mindId]!!.triggerDmgType = ""
            } else {
                val triggerIota = args.getTrigger(1, argc)
                state.hieroMinds[mindIota.mindId]!!.triggerId = triggerIota.triggerId
                state.hieroMinds[mindIota.mindId]!!.triggerThreshold = triggerIota.threshold
                state.hieroMinds[mindIota.mindId]!!.triggerDmgType = triggerIota.dmgType
            }
        }
        return emptyList()
    }
}