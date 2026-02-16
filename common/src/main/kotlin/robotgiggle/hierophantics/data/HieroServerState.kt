package robotgiggle.hierophantics.data

import robotgiggle.hierophantics.Hierophantics
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState
import net.minecraft.world.World
import java.util.*

class HieroServerState : PersistentState() {
	private val players: HashMap<UUID, HieroPlayerState> = HashMap()

	override fun writeNbt(nbt: NbtCompound): NbtCompound {
		players.forEach { (uuid: UUID, player: HieroPlayerState) -> nbt.put(uuid.toString(), player.serialize()) }
		return nbt
	}

	fun nameUsed(name: String): Boolean {
		var used = false
		players.forEach { (_, state) -> if (state.hieroMinds.containsKey(name)) used = true }
		return used
	}

	companion object {
		private fun createFromNbt(nbt: NbtCompound): HieroServerState {
			val state = HieroServerState()
			nbt.keys.forEach { uuid -> state.players[UUID.fromString(uuid)] = HieroPlayerState.deserialize(nbt.getCompound(uuid)) }
			return state
		}

		fun getServerState(server: MinecraftServer): HieroServerState {
			val state = server.getWorld(World.OVERWORLD)!!.persistentStateManager.getOrCreate(::createFromNbt, ::HieroServerState, Hierophantics.MOD_ID)
			state.markDirty()
			return state
		}

		@JvmStatic
		fun getPlayerState(player: PlayerEntity): HieroPlayerState {
			return getServerState(player.server!!).players.computeIfAbsent(player.uuid) { HieroPlayerState() }
		}
	}
}