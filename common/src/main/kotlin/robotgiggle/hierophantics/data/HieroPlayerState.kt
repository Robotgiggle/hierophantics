package robotgiggle.hierophantics.data

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.api.utils.vecFromNBT
import at.petrak.hexcasting.api.casting.iota.Iota
import robotgiggle.hierophantics.Hierophantics
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
	var prevHealth = -1.0
	var prevBreath = -1.0
	var prevHunger = -1.0
	var prevVel = Vec3d.ZERO
	var prev2Vel = Vec3d.ZERO
	var prev3Vel = Vec3d.ZERO
	var prevFallDist = -1.0
	var skipTeleTrigger = 0

	fun tick(player: ServerPlayerEntity) {
		if (player.isDead() || ownedMinds == 0) return

		val currHealth = player.getHealth().toDouble()
		val currBreath = player.getAir() / 30.0
		val currHunger = player.getHungerManager().getFoodLevel().toDouble()
		val currVel = HexAPI.instance().getEntityVelocitySpecial(player)
		val currFallDist = player.fallDistance.toDouble()

		val currSpeed = currVel.length()
		val prevSpeed = prevVel.length()
		val prev2Speed = prev2Vel.length()
		val prev3Speed = prev3Vel.length()
		
		// detect teleportation by looking for single-tick velocity spikes
		// using this rather than a mixin because player teleport code is a horrible mess
		var teleported = false
		if (prevSpeed > 4*prev2Speed && prevSpeed > 4*prev3Speed && prevSpeed > 4*currSpeed && prevSpeed >= 1.5) {
			teleported = true
			if (skipTeleTrigger > 0) skipTeleTrigger = 0
			else triggerMinds(player, "teleport", Vec3Iota(prevVel))	
		}

		// detect threshold-based triggers
		hieroMinds.forEach { (_, mind) -> 
			val trigger = mind.trigger
			when (trigger.type) {
				"health" -> if (trigger.passedThreshold(currHealth, prevHealth)) mind.cast(player)
				"breath" -> if (trigger.passedThreshold(currBreath, prevBreath)) mind.cast(player)
				"hunger" -> if (trigger.passedThreshold(currHunger, prevHunger)) mind.cast(player)
				"velocity" -> if (!teleported && trigger.passedThreshold(prevSpeed, prev2Speed)) {
					if (skipTeleTrigger > 0) skipTeleTrigger = 0
					else mind.cast(player)
				}
				"fall" -> if (trigger.passedThreshold(currFallDist, prevFallDist)) mind.cast(player)
			}
		}

		if (skipTeleTrigger > 0) skipTeleTrigger--
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
			if (mind.trigger.type == "damage_typed" && mind.trigger.dmgType.equals(type)) 
				mind.cast(player, listOf(initialIota))
		}
	}

	fun allTriggersUsed(): Boolean {
		var allTriggers = true
		Hierophantics.TRIGGER_TYPES.forEach { triggerType -> 
			var triggerUsed = false
			hieroMinds.forEach { (_, mind) -> 
				if (mind.trigger.type == triggerType) triggerUsed = true
			}
			if (!triggerUsed) allTriggers = false
		}
		return allTriggers
	}

	fun addMind(server: MinecraftServer, villagerName: String?): Int {
		val name = when(villagerName) {
			null -> MindNamer.generateRandomName(server)
			else -> MindNamer.processCustomName(server, villagerName)
		}
		hieroMinds[name] = HieroMind()
		return ++ownedMinds
	}

	fun freeMind(name: String): Int {
		hieroMinds.remove(name)
		return --ownedMinds
	}

	fun getMind(name: String): HieroMind {
		return hieroMinds[name]!!;
	}

	fun hasMind(name: String): Boolean {
		return hieroMinds.containsKey(name)
	}

	@JvmOverloads
	fun triggerMinds(player: ServerPlayerEntity, trigger: String, initialStack: List<Iota> = listOf()) {
		hieroMinds.forEach { (_, mind) -> if (mind.trigger.type == trigger) mind.cast(player, initialStack) }
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