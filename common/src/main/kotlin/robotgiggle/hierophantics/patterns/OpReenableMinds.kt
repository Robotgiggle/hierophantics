package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.data.HieroPlayerState
import robotgiggle.hierophantics.mishaps.NotYourMindMishap
import net.minecraft.server.network.ServerPlayerEntity

object OpReenableMinds : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val caster = env.castingEntity
		if (caster == null || caster !is ServerPlayerEntity ) {
			throw NotYourMindMishap()
		}

		val state = HieroServerState.getPlayerState(caster)

		return SpellAction.Result(
			Spell(state),
			if (state.disabled) MediaConstants.DUST_UNIT * state.ownedMinds else 0,
			listOf()
		)
	}
	private data class Spell(val state: HieroPlayerState) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			state.disabled = false
		}
	}
}