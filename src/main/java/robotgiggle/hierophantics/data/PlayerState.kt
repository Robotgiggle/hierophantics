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
import java.util.*

class PlayerState {
	var ownedMinds = 0
	val hieroMinds: MutableMap<UUID, HieroMind> = mutableMapOf()
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

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("owned", ownedMinds)
		val minds = NbtList()
		hieroMinds.forEach { (uuid, mind) ->
			val mindNbt = NbtCompound()
			mindNbt.putString("id", uuid.toString())
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
				state.hieroMinds[UUID.fromString(mind.asCompound.getString("id"))] = HieroMind.deserialize(mind.asCompound.getCompound("mind"))
			}
			return state
		}
	}
}