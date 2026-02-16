package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.getPositiveDouble
import robotgiggle.hierophantics.iotas.TriggerIota

class OpMakeThresholdTrigger(val trigger: String) : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val threshold = args.getPositiveDouble(0, argc)
		return listOf(TriggerIota(trigger, threshold))
	}
}