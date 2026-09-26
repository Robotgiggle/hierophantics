package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.getBool
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.data.HieroPlayerState
import robotgiggle.hierophantics.iotas.getMindReference
import robotgiggle.hierophantics.mishaps.*
import net.minecraft.server.level.ServerPlayer
import robotgiggle.hierophantics.data.HieroMind

object OpMuteMind : SpellAction {
    override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val caster = env.castingEntity
		val mindRef = args.getMindReference(0, argc)

		if (caster == null || caster !is ServerPlayer || mindRef.host != caster) {
			throw NotYourMindMishap()
		}

		val state = HieroServerState.getPlayerState(caster)
		if (state.disabled) throw MindsDisabledMishap("write")
		val mind = state.getMind(mindRef.name)

        val newState = args.getBool(1, argc)
		
		return SpellAction.Result(
			Spell(mind, newState),
			0,
			listOf()
		)
	}
	private data class Spell(val mind: HieroMind, val newState: Boolean) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			mind.muted = newState
		}
	}
}