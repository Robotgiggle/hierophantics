package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.iotas.TriggerIota
import robotgiggle.hierophantics.iotas.getTrigger

class OpInvertTrigger() : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val oldTrigger = args.getTrigger(0, argc)

        if (oldTrigger.threshold == -1.0) {
            throw MishapInvalidIota.of(oldTrigger, 0, "threshold_trigger")
        }

        return listOf(TriggerIota(
            oldTrigger.type, oldTrigger.threshold, oldTrigger.dmgType, !oldTrigger.inverted
        ))
	}
}