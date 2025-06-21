package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.ListIota
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.iotas.MindReferenceIota
import net.minecraft.server.network.ServerPlayerEntity

import at.petrak.hexcasting.api.HexAPI

class OpGetMinds : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster != null && caster is ServerPlayerEntity) {
			if (HierophanticsAPI.getPlayerState(caster).disabled) {
				// throw MindsDisabledMishap
				return emptyList()
			}
			val minds = HierophanticsAPI.getPlayerState(caster).hieroMinds
			val output = mutableListOf<Iota>()
			minds.forEach { (id, _) -> output.add(MindReferenceIota(id, caster)) }
			return listOf(ListIota(output))
		}
		return listOf(NullIota())
	}
}