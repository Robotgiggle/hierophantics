package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.iotas.MindReferenceIota
import net.minecraft.server.network.ServerPlayerEntity

import at.petrak.hexcasting.api.HexAPI
// DEBUG ONLY, REMOVE BEFORE RELEASING MOD

class OpDebugAddMind : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster != null && caster is ServerPlayerEntity) {
			HexAPI.LOGGER.warn("caster check passed")
			HierophanticsAPI.getPlayerState(caster).addMind()
		}
		HexAPI.LOGGER.warn("returning null")
		return listOf(NullIota())
	}
}