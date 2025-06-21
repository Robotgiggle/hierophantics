package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.getList
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.iotas.MindReferenceIota
import robotgiggle.hierophantics.iotas.getMindReference
import robotgiggle.hierophantics.mishaps.*
import net.minecraft.server.network.ServerPlayerEntity

class OpSetMindHex : ConstMediaAction {
    override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity
		if (caster != null && caster is ServerPlayerEntity) {
            val mindIota = args.getMindReference(0, argc)
			if (mindIota.host != caster) throw NotYourMindMishap()
			val state = HierophanticsAPI.getPlayerState(caster)
			if (!state.hasMind(mindIota.mindId)) throw MindFreedMishap()
			if (state.disabled) throw MindsDisabledMishap()
            args.getList(1, argc) // this makes sure the second argument is a list
			state.hieroMinds[mindIota.mindId]!!.hex = IotaType.serialize(args[1] as ListIota)
        }
		return emptyList()
	}
}