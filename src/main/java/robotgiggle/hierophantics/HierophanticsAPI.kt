package robotgiggle.hierophantics

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import robotgiggle.hierophantics.data.PlayerState
import robotgiggle.hierophantics.data.HieroMind
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.PersistentState
import net.minecraft.world.World
import java.util.*

class HierophanticsAPI : PersistentState() {
	private val players: HashMap<UUID, PlayerState> = HashMap()

	override fun writeNbt(nbt: NbtCompound): NbtCompound {
		players.forEach { (uuid: UUID, player: PlayerState) -> nbt.put(uuid.toString(), player.serialize()) }
		return nbt
	}

	companion object {
		private fun createFromNbt(nbt: NbtCompound): HierophanticsAPI {
			val state = HierophanticsAPI()
			nbt.keys.forEach { uuid -> state.players[UUID.fromString(uuid)] = PlayerState.deserialize(nbt.getCompound(uuid)) }
			return state
		}

		private fun getServerState(server: MinecraftServer): HierophanticsAPI {
			val state = server.getWorld(World.OVERWORLD)!!.persistentStateManager.getOrCreate(::createFromNbt, ::HierophanticsAPI, HierophanticsMain.MOD_ID)
			state.markDirty()
			return state
		}

		@JvmStatic
		fun getPlayerState(player: PlayerEntity): PlayerState {
			return getServerState(player.server!!).players.computeIfAbsent(player.uuid) { PlayerState() }
		}

		// fun queue(player: ServerPlayerEntity, hex: ListIota, delay: Int, label: String) {
		// 	getPlayerState(player).queuedHexes[label] = HieroMind(IotaType.serialize(hex), delay)
		// }

		// fun dequeueAll(player: ServerPlayerEntity) {
		// 	getPlayerState(player).queuedHexes.clear()
		// }

		// fun dequeueByName(player: ServerPlayerEntity, label: String) {
		// 	getPlayerState(player).queuedHexes.remove(label)
		// }
	}
}