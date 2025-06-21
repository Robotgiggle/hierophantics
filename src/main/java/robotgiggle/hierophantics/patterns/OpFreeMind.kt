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
import net.minecraft.server.network.ServerPlayerEntity

class OpFreeMind : ConstMediaAction {
    override val argc = 1
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
			state.freeMind(mindIota.mindId)
        }
		return emptyList()
	}
}