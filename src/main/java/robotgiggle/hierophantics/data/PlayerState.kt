package robotgiggle.hierophantics.data

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import robotgiggle.hierophantics.HierophanticsAPI
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity

class PlayerState {
	var ownedMinds = 0
	var nextIndex = 0
	val hieroMinds: MutableMap<Int, HieroMind> = mutableMapOf()
	//private var previouslyActiveCassettes: MutableSet<String> = mutableSetOf()

	fun tick(player: ServerPlayerEntity) {
		// if (previouslyActiveCassettes != queuedHexes.keys)
		// 	HierophanticsAPI.sendSyncPacket(player)
		// previouslyActiveCassettes = queuedHexes.keys.toMutableSet()

		// queuedHexes.forEach { (label, hex) ->
		// 	hex.delay -= 1
		// 	if (hex.delay <= 0) {
		// 		val buf = PacketByteBufs.create()
		// 		buf.writeString(label)
		// 		hex.cast(player)
		// 	}
		// }

		// val iterator = queuedHexes.iterator()
		// while (iterator.hasNext())
		// 	if (iterator.next().value.delay <= 0)
		// 		iterator.remove()
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

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("owned", ownedMinds)
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
			compound.getList("minds", NbtElement.COMPOUND_TYPE.toInt()).forEach { mind ->
				state.hieroMinds[mind.asCompound.getInt("id")] = HieroMind.deserialize(mind.asCompound.getCompound("mind"))
			}
			return state
		}
	}
}