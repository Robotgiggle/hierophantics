package robotgiggle.hierophantics.data

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.api.utils.vecFromNBT
import robotgiggle.hierophantics.HierophanticsAPI
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.util.math.Vec3d
import net.minecraft.server.network.ServerPlayerEntity

import at.petrak.hexcasting.api.HexAPI

class PlayerState() {
	var ownedMinds = 0
	var nextIndex = 0
	val hieroMinds: MutableMap<Int, HieroMind> = mutableMapOf()
	
	var prevHealth: Double = 0.0
	var prevBreath: Double = 0.0
	var prevHunger: Double = 0.0
	var prevSpeed: Double = 0.0
	var prevPrevSpeed: Double = 0.0
	var prevFallTime: Double = 0.0
	var died = false

	fun tick(player: ServerPlayerEntity) {
		val currSpeed = HexAPI.instance().getEntityVelocitySpecial(player).length()
		// detect teleportation by looking for single-tick velocity spikes
		// using this rather than a mixin because player teleport code is a horrible mess
		if (prevSpeed > 4*prevPrevSpeed && prevSpeed > 4*currSpeed && prevSpeed > 4) {
			if (died) died = false
			else triggerMinds(player, 11)
		}
		prevPrevSpeed = prevSpeed
		prevSpeed = currSpeed
	}

	fun addMind() {
		hieroMinds[nextIndex] = HieroMind()
		nextIndex++
		ownedMinds++
	}

	fun freeMind(id: Int) {
		hieroMinds.remove(id)
		ownedMinds--
	}

	fun hasMind(id: Int): Boolean {
		return hieroMinds.containsKey(id)
	}

	fun triggerMinds(player: ServerPlayerEntity, triggerId: Int) {
		hieroMinds.forEach { (_, mind) -> if (mind.triggerId == triggerId) mind.cast(player) }
	}

	fun markDied() { died = true }

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("owned", ownedMinds)
		compound.putInt("next", nextIndex)
		compound.putBoolean("died", died)
		val minds = NbtList()
		hieroMinds.forEach { (id, mind) ->
			val mindNbt = NbtCompound()
			mindNbt.putInt("id", id)
			mindNbt.putCompound("mind", mind.serialize())
			minds.add(mindNbt)
		}
		compound.putList("minds", minds)
		return compound
	}

	companion object {
		fun deserialize(compound: NbtCompound): PlayerState {
			val state = PlayerState()
			state.ownedMinds = compound.getInt("owned")
			state.nextIndex = compound.getInt("next")
			state.died = compound.getBoolean("died")
			compound.getList("minds", NbtElement.COMPOUND_TYPE.toInt()).forEach { mind ->
				state.hieroMinds[mind.asCompound.getInt("id")] = HieroMind.deserialize(mind.asCompound.getCompound("mind"))
			}
			return state
		}
	}
}