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
	var disabled = false
	val hieroMinds: MutableMap<Int, HieroMind> = mutableMapOf()
	
	var prevHealth = 0.0f
	var prevBreath = 0.0f
	var prevHunger = 0
	var prevSpeed = 0.0
	var prevPrevSpeed = 0.0
	var prevFallDist = 0.0f

	fun tick(player: ServerPlayerEntity) {
		val currHealth = player.getHealth()
		val currBreath = player.getAir() / 30f
		val currHunger = player.getHungerManager().getFoodLevel()
		val currSpeed = HexAPI.instance().getEntityVelocitySpecial(player).length()
		val currFallDist = player.fallDistance
		if (!disabled) {
			// detect teleportation by looking for single-tick velocity spikes
			// using this rather than a mixin because player teleport code is a horrible mess
			if (prevSpeed > 4*prevPrevSpeed && prevSpeed > 4*currSpeed && prevSpeed > 4) {
				triggerMinds(player, 11)
			}
			// detect threshold-based triggers
			hieroMinds.forEach { (_, mind) -> 
				when (mind.triggerId) {
					2 -> if (currHealth < mind.triggerThreshold && prevHealth >= mind.triggerThreshold) mind.cast(player)
					3 -> if (currBreath < mind.triggerThreshold && prevBreath >= mind.triggerThreshold) mind.cast(player)
					4 -> if (currHunger < mind.triggerThreshold && prevHunger >= mind.triggerThreshold) mind.cast(player)
					5 -> if (currSpeed > mind.triggerThreshold && prevSpeed <= mind.triggerThreshold) mind.cast(player)
					6 -> if (currFallDist > mind.triggerThreshold && prevFallDist <= mind.triggerThreshold) mind.cast(player)
				}
			}
		}
		prevHealth = currHealth
		prevBreath = currBreath
		prevHunger = currHunger
		prevPrevSpeed = prevSpeed
		prevSpeed = currSpeed
		prevFallDist = currFallDist
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
		if (disabled) return
		hieroMinds.forEach { (_, mind) -> if (mind.triggerId == triggerId) mind.cast(player) }
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("owned", ownedMinds)
		compound.putInt("next", nextIndex)
		compound.putBoolean("disabled", disabled)
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
			state.disabled = compound.getBoolean("disabled")
			compound.getList("minds", NbtElement.COMPOUND_TYPE.toInt()).forEach { mind ->
				state.hieroMinds[mind.asCompound.getInt("id")] = HieroMind.deserialize(mind.asCompound.getCompound("mind"))
			}
			return state
		}
	}
}