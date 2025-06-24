package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.misc.MediaConstants
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.data.PlayerState
import robotgiggle.hierophantics.iotas.MindReferenceIota
import robotgiggle.hierophantics.mishaps.NotYourMindMishap
import net.minecraft.server.network.ServerPlayerEntity

import at.petrak.hexcasting.api.HexAPI

class OpReenableMinds : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val caster = env.castingEntity
		if (caster == null || caster !is ServerPlayerEntity ) {
			throw NotYourMindMishap()
		}

		val state = HierophanticsAPI.getPlayerState(caster)

		return SpellAction.Result(
			Spell(state),
			if (state.disabled) MediaConstants.DUST_UNIT * state.ownedMinds else 0,
			listOf()
		)
	}
	private data class Spell(val state: PlayerState) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			state.disabled = false
		}
	}
}

// class OpReenableMinds : ConstMediaAction {
// 	override val argc = 0
// 	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
// 		val caster = env.castingEntity
// 		if (caster != null && caster is ServerPlayerEntity) {
// 			HierophanticsAPI.getPlayerState(caster).disabled = false
// 		}
// 		return emptyList()
// 	}
// }