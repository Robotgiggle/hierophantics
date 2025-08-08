package robotgiggle.hierophantics.data

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.api.utils.vecFromNBT
import at.petrak.hexcasting.api.casting.iota.Iota
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.data.generateMindName
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.util.math.Vec3d
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.MinecraftServer

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.iota.Vec3Iota

class HieroPlayerState() {
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
	var prev2Vel = Vec3d.ZERO
	var prev3Vel = Vec3d.ZERO
	var prevFallDist = 0.0f
	var skipTeleTrigger = false

	fun tick(player: ServerPlayerEntity) {
		if (player.isDead() || ownedMinds == 0) return

		val currHealth = player.getHealth()
		val currBreath = player.getAir() / 30f
		val currHunger = player.getHungerManager().getFoodLevel()
		val currVel = HexAPI.instance().getEntityVelocitySpecial(player)
		val currFallDist = player.fallDistance

		val currSpeed = currVel.length()
		val prevSpeed = prevVel.length()
		val prev2Speed = prev2Vel.length()
		val prev3Speed = prev3Vel.length()

		var teleported = false
		
		// detect teleportation by looking for single-tick velocity spikes
		// using this rather than a mixin because player teleport code is a horrible mess
		if (prevSpeed > 4*prev2Speed && prevSpeed > 4*prev3Speed && prevSpeed > 4*currSpeed) {
			teleported = true
			if (skipTeleTrigger) skipTeleTrigger = false
			else triggerMinds(player, "teleport", Vec3Iota(prevVel))	
		}

		// detect threshold-based triggers
		hieroMinds.forEach { (_, mind) -> 
			when (mind.trigger) {
				"health" -> if (currHealth < mind.triggerThreshold && prevHealth >= mind.triggerThreshold) mind.cast(player)
				"breath" -> if (currBreath < mind.triggerThreshold && prevBreath >= mind.triggerThreshold) mind.cast(player)
				"hunger" -> if (currHunger < mind.triggerThreshold && prevHunger >= mind.triggerThreshold) mind.cast(player)
				"velocity" -> if (!teleported && prevSpeed > mind.triggerThreshold && prev2Speed <= mind.triggerThreshold) mind.cast(player)
				"fall" -> if (currFallDist > mind.triggerThreshold && prevFallDist <= mind.triggerThreshold) mind.cast(player)
			}
		}

		prevHealth = currHealth
		prevBreath = currBreath
		prevHunger = currHunger
		prev3Vel = prev2Vel
		prev2Vel = prevVel
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
		Hierophantics.TRIGGER_NAMES.forEach { triggerName -> 
			var triggerUsed = false
			hieroMinds.forEach { (_, mind) -> 
				if (mind.trigger == triggerName) triggerUsed = true
			}
			if (!triggerUsed) allTriggers = false
		}
		return allTriggers
	}

	fun addMind(server: MinecraftServer) {
		val sState = HieroServerState.getServerState(server)
		var name = generateMindName()
		while (sState.nameUsed(name)) {
			name = generateMindName()
		}
		hieroMinds[name] = HieroMind()
		ownedMinds++
	}

	fun addMindNamed(server: MinecraftServer, baseName: String) {
		val sState = HieroServerState.getServerState(server)
		var name = baseName
		var suffix = 2
		while (sState.nameUsed(name)) {
			name = baseName + " " + Hierophantics.numToRoman(suffix)
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
		fun deserialize(compound: NbtCompound): HieroPlayerState {
			val state = HieroPlayerState()
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