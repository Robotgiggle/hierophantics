package robotgiggle.hierophantics.data

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.common.lib.HexSounds
import robotgiggle.hierophantics.HieroMindCastEnv
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.inits.HierophanticsSounds
import robotgiggle.hierophantics.iotas.MishapThrowerIota
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.sound.SoundCategory

class HieroMind(var hex: NbtCompound, var trigger: String, var triggerThreshold: Double, var triggerDmgType: String) {
	constructor() : this(NbtCompound(), "none", -1.0, "") {}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putCompound("hex", hex)
		compound.putString("trigger", trigger)
		compound.putDouble("triggerThreshold", triggerThreshold)
		compound.putString("triggerDmgType", triggerDmgType)
		return compound
	}

	fun cast(player: ServerPlayerEntity, initialStack: List<Iota> = listOf()) {
		//if (player.isDead()) return
		val hand = if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty && player.getStackInHand(Hand.OFF_HAND).isEmpty) Hand.OFF_HAND else Hand.MAIN_HAND
		val harness = CastingVM(CastingImage().copy(stack = initialStack), HieroMindCastEnv(player, hand))
		val hexIota = IotaType.deserialize(hex, player.serverWorld)
		if (hexIota is ListIota) {
			var patternList = hexIota.list.toList()
			if (HierophanticsAPI.getPlayerState(player).disabled) {
				patternList = listOf(MishapThrowerIota())
			}
			val ecv = harness.queueExecuteAndWrapIotas(patternList, player.serverWorld)
			val pos = player.getPos()
			val sound = if (ecv.resolutionType.success) HierophanticsSounds.HIEROMIND_CAST else HexSounds.CAST_FAILURE
			player.getWorld().playSound(null, pos.x, pos.y, pos.z, sound, SoundCategory.PLAYERS, 1f, 1f)
		}	
	}

	companion object {
		fun deserialize(compound: NbtCompound) = HieroMind(
			compound.getCompound("hex"), 
			compound.getString("trigger"), 
			compound.getDouble("triggerThreshold"), 
			compound.getString("triggerDmgType")
		)
	}
}