package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.ListIota
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.iotas.MindReferenceIota
import robotgiggle.hierophantics.iotas.getMindReference
import robotgiggle.hierophantics.mishaps.*
import net.minecraft.server.network.ServerPlayerEntity

class OpGetMindHex : ConstMediaAction {
    override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val mind = args.getMindReference(0, argc)

		val state = HierophanticsAPI.getPlayerState(mind.host)
		if (!state.hasMind(mind.name)) {
			throw MindFreedMishap()
		}
		if (state.disabled) {
			throw MindsDisabledMishap()
		}

		val storedHex = IotaType.deserialize(state.hieroMinds[mind.name]!!.hex, env.world)
		if (storedHex is ListIota) {
			return listOf(storedHex)
		} else {
			return listOf(NullIota())
		}
	}
}