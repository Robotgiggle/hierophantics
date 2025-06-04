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
			HexAPI.LOGGER.warn("caster check passed")
			val minds = HierophanticsAPI.getPlayerState(caster).hieroMinds
			HexAPI.LOGGER.warn("got internal mind list of size " + minds.size)
			val output = mutableListOf<Iota>()
			minds.forEach { (id, _) ->
				val mindiota = MindReferenceIota(id, caster)
				if (mindiota is MindReferenceIota) {
					HexAPI.LOGGER.warn("correct type")
				} else {
					HexAPI.LOGGER.warn("incorrect type")
				}
				output.add(mindiota)
			}
			HexAPI.LOGGER.warn("output is of size " + output.size)
			if (output[0] is MindReferenceIota) {
				HexAPI.LOGGER.warn("first element is correct type")
			} else {
				HexAPI.LOGGER.warn("first element is NOT correct type")
			}
			val list = ListIota(output)
			
			val first = list.getList().getAt(0)
			if (first is MindReferenceIota) {
				HexAPI.LOGGER.warn("first converted element is correct type")
			} else {
				HexAPI.LOGGER.warn("first converted element is NOT correct type")
			}

			HexAPI.LOGGER.warn("returning list")
			return listOf(list)
		}
		HexAPI.LOGGER.warn("caster check failed, returning null")
		return listOf(NullIota())
	}
}