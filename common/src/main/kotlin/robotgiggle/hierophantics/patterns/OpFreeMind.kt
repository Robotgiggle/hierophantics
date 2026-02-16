package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.data.HieroPlayerState
import robotgiggle.hierophantics.iotas.getMindReference
import robotgiggle.hierophantics.networking.msg.MsgOwnedMindsS2C
import robotgiggle.hierophantics.mishaps.*
import net.minecraft.server.network.ServerPlayerEntity

object OpFreeMind : SpellAction {
    override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val caster = env.castingEntity
		val mindRef = args.getMindReference(0, argc)

		if (caster == null || caster !is ServerPlayerEntity || mindRef.host != caster) {
			throw NotYourMindMishap()
		}

		val state = HieroServerState.getPlayerState(caster)
		if (!state.hasMind(mindRef.name)) {
			throw MindFreedMishap()
		}
		if (state.disabled) {
			throw MindsDisabledMishap("free")
		}
		
		return SpellAction.Result(
			Spell(state, mindRef.name),
			0,
			listOf()
		)
	}
	private data class Spell(val state: HieroPlayerState, val mindName: String) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val newTotal = state.freeMind(mindName)
			MsgOwnedMindsS2C(newTotal).sendToPlayer(env.castingEntity as ServerPlayerEntity)
		}
	}
}