package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.ListIota
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.iotas.MindReferenceIota
import robotgiggle.hierophantics.iotas.getMindReference
import robotgiggle.hierophantics.mishaps.*
import net.minecraft.server.network.ServerPlayerEntity

object OpGetMindHex : ConstMediaAction {
    override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val mindRef = args.getMindReference(0, argc)

		val state = HieroServerState.getPlayerState(mindRef.host)
		if (!state.hasMind(mindRef.name)) {
			throw MindFreedMishap()
		}
		if (state.disabled) {
			throw MindsDisabledMishap("read", mindRef.host == env.castingEntity)
		}

		val storedHex = IotaType.deserialize(state.getMind(mindRef.name).hex, env.world)
		if (storedHex is ListIota) {
			return listOf(storedHex)
		} else {
			return listOf(NullIota())
		}
	}
}