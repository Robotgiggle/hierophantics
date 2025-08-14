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
import robotgiggle.hierophantics.iotas.MindReferenceIota
import robotgiggle.hierophantics.iotas.getMindReference
import robotgiggle.hierophantics.mishaps.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.nbt.NbtCompound

class OpSetMindHex : SpellAction {
	override val argc = 2
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
			throw MindsDisabledMishap("write")
		}

		// second argument should be either a list or null, mishap otherwise
		if (args[1] !is NullIota) {
			args.getList(1, argc)
		}

		val trueName = MishapOthersName.getTrueNameFromDatum(args[1], env.castingEntity as? ServerPlayerEntity)
        if (trueName != null) {
			throw MishapOthersName(trueName)
		}

		return SpellAction.Result(
			Spell(mindRef, args[1], state),
			MediaConstants.CRYSTAL_UNIT,
			listOf()
		)
	}
	private data class Spell(val mindRef: MindReferenceIota, val payload: Iota, val state: HieroPlayerState) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (payload is NullIota) {
				state.getMind(mindRef.name).hex = NbtCompound()
			} else {
				state.getMind(mindRef.name).hex = IotaType.serialize(payload as ListIota)
			}
		}
	}
}