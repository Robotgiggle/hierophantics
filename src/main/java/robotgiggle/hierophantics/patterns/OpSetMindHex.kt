package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.misc.MediaConstants
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.data.PlayerState
import robotgiggle.hierophantics.iotas.MindReferenceIota
import robotgiggle.hierophantics.iotas.getMindReference
import robotgiggle.hierophantics.mishaps.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.nbt.NbtCompound

class OpSetMindHex : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val caster = env.castingEntity
		val mind = args.getMindReference(0, argc)

		if (caster == null || caster !is ServerPlayerEntity || mind.host != caster) {
			throw NotYourMindMishap()
		}

		val state = HierophanticsAPI.getPlayerState(caster)
		if (!state.hasMind(mind.name)) {
			throw MindFreedMishap()
		}
		if (state.disabled) {
			throw MindsDisabledMishap("modify")
		}

		// second argument should be either a list or null, mishap otherwise
		if (args[1] !is NullIota) {
			args.getList(1, argc)
		}

		return SpellAction.Result(
			Spell(mind, args[1], state),
			MediaConstants.CRYSTAL_UNIT,
			listOf()
		)
	}
	private data class Spell(val mind: MindReferenceIota, val payload: Iota, val state: PlayerState) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (payload is NullIota) {
				state.hieroMinds[mind.name]!!.hex = NbtCompound()
			} else {
				state.hieroMinds[mind.name]!!.hex = IotaType.serialize(payload as ListIota)
			}
		}
	}
}