package robotgiggle.hierophantics.data

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.common.lib.HexSounds
import robotgiggle.hierophantics.HieroMindCastEnv
import robotgiggle.hierophantics.inits.HierophanticsSounds
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Hand
import net.minecraft.sounds.SoundSource

class HieroMind(var hex: NbtCompound, var trigger: Trigger, var muted: Boolean) {
	constructor() : this(NbtCompound(), Trigger("none", -1.0, "", false), false)

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putCompound("hex", hex)
		compound.putCompound("trigger", trigger.serialize())
		compound.putBoolean("muted", muted)
		return compound
	}

	fun cast(player: ServerPlayer, initialStack: List<Iota> = listOf()) {
		val hand = if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty && player.getStackInHand(Hand.OFF_HAND).isEmpty) Hand.OFF_HAND else Hand.MAIN_HAND
		val harness = CastingVM(CastingImage().copy(stack = initialStack), HieroMindCastEnv(player, hand, muted))
		val hexIota = IotaType.deserialize(hex, player.serverLevel)
		if (hexIota is ListIota) {
			var patternList = hexIota.list.toList()
			val ecv = harness.queueExecuteAndWrapIotas(patternList, player.serverLevel)
			if (!muted) {
				val pos = player.position()
				val sound = if (ecv.resolutionType.success) HierophanticsSounds.HIEROMIND_CAST.value else HexSounds.CAST_FAILURE
				player.level().playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, 1f, 1f)
			}
		}	
	}

	companion object {
		fun deserialize(compound: NbtCompound) = HieroMind(
			compound.getCompound("hex"), 
			Trigger.deserialize(compound.getCompound("trigger")),
			compound.getBoolean("muted")
		)
	}
}