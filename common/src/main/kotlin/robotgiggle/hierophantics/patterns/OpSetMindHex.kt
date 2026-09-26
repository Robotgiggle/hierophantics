package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.misc.MediaConstants
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.data.HieroPlayerState
import robotgiggle.hierophantics.iotas.getMindReference
import robotgiggle.hierophantics.mishaps.*
import net.minecraft.server.level.ServerPlayer
import net.minecraft.nbt.CompoundTag
import robotgiggle.hierophantics.data.HieroMind

object OpSetMindHex : SpellAction {
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

		// second argument should be either a list or null, mishap otherwise
		if (args[1] !is NullIota) {
			args.getList(1, argc)
		}

		val trueName = MishapOthersName.getTrueNameFromDatum(args[1], env.castingEntity as? ServerPlayer)
        if (trueName != null) {
			throw MishapOthersName(trueName)
		}

		return SpellAction.Result(
			Spell(mind, args[1]),
			MediaConstants.CRYSTAL_UNIT,
			listOf()
		)
	}
	private data class Spell(val mind: HieroMind, val payload: Iota) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (payload is NullIota) {
				mind.hex = CompoundTag()
			} else {
				mind.hex = IotaType.serialize(payload as ListIota)
			}
		}
	}
}