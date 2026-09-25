package robotgiggle.hierophantics.data

import robotgiggle.hierophantics.Hierophantics
import net.minecraft.world.entity.player.Player
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import java.util.*

class HieroServerState : SavedData() {
	private val players: HashMap<UUID, HieroPlayerState> = HashMap()

	override fun save(nbt: CompoundTag): CompoundTag {
		players.forEach { (uuid: UUID, player: HieroPlayerState) -> nbt.put(uuid.toString(), player.serialize()) }
		return nbt
	}

	fun nameUsed(name: String): Boolean {
		var used = false
		players.forEach { (_, state) -> if (state.hieroMinds.containsKey(name)) used = true }
		return used
	}

	companion object {
		private fun createFromNbt(nbt: CompoundTag): HieroServerState {
			val state = HieroServerState()
			nbt.allKeys.forEach { uuid -> state.players[UUID.fromString(uuid)] = HieroPlayerState.deserialize(nbt.getCompound(uuid)) }
			return state
		}

		fun getServerState(server: MinecraftServer): HieroServerState {
			val state = server.getLevel(Level.OVERWORLD)!!.dataStorage.computeIfAbsent(::createFromNbt, ::HieroServerState, Hierophantics.MOD_ID)
			state.setDirty()
			return state
		}

		@JvmStatic
		fun getPlayerState(player: Player): HieroPlayerState {
			return getServerState(player.server!!).players.computeIfAbsent(player.uuid) { HieroPlayerState() }
		}
	}
}