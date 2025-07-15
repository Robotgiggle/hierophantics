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
		val mindRef = args.getMindReference(0, argc)

		val state = HierophanticsAPI.getPlayerState(mindRef.host)
		if (!state.hasMind(mindRef.name)) {
			throw MindFreedMishap()
		}
		if (state.disabled) {
			throw MindsDisabledMishap("read", mindRef.host == env.castingEntity)
		}

        val mind = state.getMind(mindRef.name)
        if (mind.trigger.equals("none")) {
            return listOf(NullIota())
        } else {
            return listOf(TriggerIota(mind.trigger, mind.triggerThreshold, mind.triggerDmgType))
        }
    }
}