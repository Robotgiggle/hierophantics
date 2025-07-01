package robotgiggle.hierophantics.data

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.common.lib.HexSounds
import robotgiggle.hierophantics.HieroMindCastEnv
import robotgiggle.hierophantics.inits.HierophanticsSounds
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.sound.SoundCategory

class HieroMind(var hex: NbtCompound, var triggerId: Int, var triggerThreshold: Double, var triggerDmgType: String) {
	constructor() : this(NbtCompound(), -1, -1.0, "") {}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putCompound("hex", hex)
		compound.putInt("triggerId", triggerId)
		compound.putDouble("triggerThreshold", triggerThreshold)
		compound.putString("triggerDmgType", triggerDmgType)
		return compound
	}

	fun cast(player: ServerPlayerEntity) {
		val hand = if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty && player.getStackInHand(Hand.OFF_HAND).isEmpty) Hand.OFF_HAND else Hand.MAIN_HAND
		val harness = CastingVM.empty(HieroMindCastEnv(player, hand))
		val hexIota = IotaType.deserialize(hex, player.serverWorld)
		if (hexIota is ListIota) {
			// TODO: add custom iota to start of list to throw MindsDisabledMishap if minds are disabled
			val ecv = harness.queueExecuteAndWrapIotas(hexIota.list.toList(), player.serverWorld)
			val pos = player.getPos()
			val sound = if (ecv.resolutionType.success) HierophanticsSounds.HIEROMIND_CAST else HexSounds.CAST_FAILURE
			player.getWorld().playSound(null, pos.x, pos.y, pos.z, sound, SoundCategory.PLAYERS, 1f, 1f)
		}	
	}

	companion object {
		fun deserialize(compound: NbtCompound) = HieroMind(
			compound.getCompound("hex"), 
			compound.getInt("triggerId"), 
			compound.getDouble("triggerThreshold"), 
			compound.getString("triggerDmgType")
		)
	}
}