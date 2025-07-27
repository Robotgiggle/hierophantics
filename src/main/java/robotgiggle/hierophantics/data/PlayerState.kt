package robotgiggle.hierophantics.data

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.api.utils.vecFromNBT
import at.petrak.hexcasting.api.casting.iota.Iota
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.HierophanticsUtils
import robotgiggle.hierophantics.data.generateMindName
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.util.math.Vec3d
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.stat.Stats;

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.iota.Vec3Iota

class PlayerState() {
	// this stuff gets serde'd
	var ownedMinds = 0
	var disabled = false
	var lastDmgType = ""
	val hieroMinds: MutableMap<String, HieroMind> = mutableMapOf()
	
	// this stuff doesn't
	var prevHealth = 0.0f
	var prevBreath = 0.0f
	var prevHunger = 0
	var prevVel = Vec3d.ZERO
	var prevPrevVel = Vec3d.ZERO
	var prevFallDist = 0.0f

	fun tick(player: ServerPlayerEntity) {
		if (player.isDead()) return

		val currHealth = player.getHealth()
		val currBreath = player.getAir() / 30f
		val currHunger = player.getHungerManager().getFoodLevel()
		val currVel = HexAPI.instance().getEntityVelocitySpecial(player)
		val currFallDist = player.fallDistance

		val currSpeed = currVel.length()
		val prevSpeed = prevVel.length()
		val prevPrevSpeed = prevPrevVel.length()
			
		// detect teleportation by looking for single-tick velocity spikes
		// using this rather than a mixin because player teleport code is a horrible mess
		if (prevSpeed > 4*prevPrevSpeed && prevSpeed > 4*currSpeed && prevSpeed >= 4) {
			if (player.statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH)) > 20) {
				triggerMinds(player, "teleport", Vec3Iota(prevVel))
			}	
		}

		// detect threshold-based triggers
		hieroMinds.forEach { (_, mind) -> 
			when (mind.trigger) {
				"health" -> if (currHealth < mind.triggerThreshold && prevHealth >= mind.triggerThreshold) mind.cast(player)
				"breath" -> if (currBreath < mind.triggerThreshold && prevBreath >= mind.triggerThreshold) mind.cast(player)
				"hunger" -> if (currHunger < mind.triggerThreshold && prevHunger >= mind.triggerThreshold) mind.cast(player)
				"velocity" -> if (currSpeed > mind.triggerThreshold && prevSpeed <= mind.triggerThreshold) mind.cast(player)
				"fall" -> if (currFallDist > mind.triggerThreshold && prevFallDist <= mind.triggerThreshold) mind.cast(player)
			}
		}

		prevHealth = currHealth
		prevBreath = currBreath
		prevHunger = currHunger
		prevPrevVel = prevVel
		prevVel = currVel
		prevFallDist = currFallDist
	}

	fun checkTypedDamage(player: ServerPlayerEntity, type: String, initialIota: Iota) {
		lastDmgType = type;
		hieroMinds.forEach { (_, mind) -> 
			if (mind.trigger == "damage_typed" && mind.triggerDmgType.equals(type)) 
				mind.cast(player, listOf(initialIota))
		}
	}

	fun allTriggersUsed(): Boolean {
		var allTriggers = true
		HierophanticsUtils.TRIGGER_NAMES.forEach { triggerName -> 
			var triggerUsed = false
			hieroMinds.forEach { (_, mind) -> 
				if (mind.trigger == triggerName) triggerUsed = true
			}
			if (!triggerUsed) allTriggers = false
		}
		return allTriggers
	}

	fun addMind(server: MinecraftServer) {
		val sState = HierophanticsAPI.getServerState(server)
		var name = generateMindName()
		while (sState.nameUsed(name)) {
			name = generateMindName()
		}
		hieroMinds[name] = HieroMind()
		ownedMinds++
	}

	fun addMindNamed(server: MinecraftServer, baseName: String) {
		val sState = HierophanticsAPI.getServerState(server)
		var name = baseName
		var suffix = 2
		while (sState.nameUsed(name)) {
			name = baseName + " " + HierophanticsUtils.numToRoman(suffix)
			suffix++
		}
		hieroMinds[name] = HieroMind()
		ownedMinds++
	}

	fun getMind(name: String): HieroMind {
		return hieroMinds[name]!!;
	}

	fun freeMind(name: String) {
		hieroMinds.remove(name)
		ownedMinds--
	}

	fun hasMind(name: String): Boolean {
		return hieroMinds.containsKey(name)
	}

	@JvmOverloads
	fun triggerMinds(player: ServerPlayerEntity, trigger: String, initialStack: List<Iota> = listOf()) {
		hieroMinds.forEach { (_, mind) -> if (mind.trigger == trigger) mind.cast(player, initialStack) }
	}

	fun triggerMinds(player: ServerPlayerEntity, trigger: String, initialIota: Iota) {
		triggerMinds(player, trigger, listOf(initialIota))
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("owned", ownedMinds)
		compound.putBoolean("disabled", disabled)
		compound.putString("lastDmgType", lastDmgType)
		val minds = NbtList()
		hieroMinds.forEach { (name, mind) ->
			val mindNbt = NbtCompound()
			mindNbt.putString("name", name)
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
			state.disabled = compound.getBoolean("disabled")
			state.lastDmgType = compound.getString("lastDmgType")
			compound.getList("minds", NbtElement.COMPOUND_TYPE.toInt()).forEach { mind ->
				state.hieroMinds[mind.asCompound.getString("name")] = HieroMind.deserialize(mind.asCompound.getCompound("mind"))
			}
			return state
		}
	}
}