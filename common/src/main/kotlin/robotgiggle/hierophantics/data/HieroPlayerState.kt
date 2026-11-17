package robotgiggle.hierophantics.data

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import at.petrak.hexcasting.api.casting.iota.Iota
import robotgiggle.hierophantics.Hierophantics
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag
import net.minecraft.world.phys.Vec3
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.MinecraftServer

import at.petrak.hexcasting.api.HexAPI
import robotgiggle.hierophantics.mishaps.MindFreedMishap

import java.util.function.Predicate

class HieroPlayerState {
	// this stuff gets serde'd
	var ownedMinds = 0
	var disabled = false
	var lastDmgType = ""
	val hieroMinds: MutableMap<String, HieroMind> = mutableMapOf()
	
	// this stuff doesn't
	var prevHealth = -1.0
	var prevBreath = -1.0
	var prevHunger = -1.0
	var prevPos = Vec3.ZERO
	var prevSpeed = -1.0
	var prevFallDist = -1.0
	var velSkipTimer = 0

	fun tick(player: ServerPlayer) {
		if (player.isDeadOrDying || ownedMinds == 0) return

		val currHealth = player.health.toDouble()
		val currBreath = player.airSupply / 30.0
		val currHunger = player.getFoodData().foodLevel.toDouble()
		val currSpeed = HexAPI.instance().getEntityVelocitySpecial(player).length()
		val currFallDist = player.fallDistance.toDouble()

		// detect threshold-based triggers (but not when respawning)
		if (prevHealth > 0) {
			hieroMinds.forEach { (_, mind) ->
				val trigger = mind.trigger
				when (trigger.type) {
					"health" -> if (trigger.passedThreshold(currHealth, prevHealth)) mind.cast(player)
					"breath" -> if (trigger.passedThreshold(currBreath, prevBreath)) mind.cast(player)
					"hunger" -> if (trigger.passedThreshold(currHunger, prevHunger)) mind.cast(player)
					"velocity" -> if (trigger.passedThreshold(currSpeed, prevSpeed) && velSkipTimer == 0) mind.cast(player)
					"fall" -> if (trigger.passedThreshold(currFallDist, prevFallDist) ) mind.cast(player)
				}
			}
		}

		if (velSkipTimer > 0) velSkipTimer--
		prevHealth = currHealth
		prevBreath = currBreath
		prevHunger = currHunger
		prevSpeed = currSpeed
		prevPos = player.position()
		prevFallDist = currFallDist
	}

	// this sets the timer to 2 since the velocity measurement is delayed by one tick
	// so the timer needs to stay above 0 until the spike we're trying to skip actually appears
	fun skipVelocityTriggers() { velSkipTimer = 2 }

	fun checkTypedDamage(player: ServerPlayer, type: String, initialIota: Iota) {
		lastDmgType = type
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
		return hieroMinds.getOrElse(name) { throw MindFreedMishap() }
	}

	fun hasMind(name: String): Boolean {
		return hieroMinds.containsKey(name)
	}

	@JvmOverloads
	fun triggerMinds(player: ServerPlayer, triggerPred: Predicate<Trigger>, initialIota: Iota? = null) {
		val initialStack = if (initialIota != null) listOf(initialIota) else listOf()
		hieroMinds.forEach { (_, mind) -> if (triggerPred.test(mind.trigger)) mind.cast(player, initialStack) }
	}

	@JvmOverloads
	fun triggerMinds(player: ServerPlayer, triggerType: String, initialIota: Iota? = null) {
		triggerMinds(player, {t -> t.type == triggerType}, initialIota)
	}

	fun serialize(): CompoundTag {
		val compound = CompoundTag()
		compound.putInt("owned", ownedMinds)
		compound.putBoolean("disabled", disabled)
		compound.putString("lastDmgType", lastDmgType)
		val minds = ListTag()
		hieroMinds.forEach { (name, mind) ->
			val mindNbt = CompoundTag()
			mindNbt.putString("name", name)
			mindNbt.putCompound("mind", mind.serialize())
			minds.add(mindNbt)
		}
		compound.putList("minds", minds)
		return compound
	}

	companion object {
		fun deserialize(compound: CompoundTag): HieroPlayerState {
			val state = HieroPlayerState()
			state.ownedMinds = compound.getInt("owned")
			state.disabled = compound.getBoolean("disabled")
			state.lastDmgType = compound.getString("lastDmgType")
			compound.getList("minds", Tag.TAG_COMPOUND.toInt()).forEach { mind ->
				state.hieroMinds[mind.asCompound.getString("name")] = HieroMind.deserialize(mind.asCompound.getCompound("mind"))
			}
			return state
		}
	}
}