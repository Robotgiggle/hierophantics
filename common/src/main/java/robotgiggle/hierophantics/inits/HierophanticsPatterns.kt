package robotgiggle.hierophantics.inits

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions
import at.petrak.hexcasting.common.lib.HexRegistries
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.patterns.*
import net.minecraft.registry.Registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier

object HierophanticsPatterns {
	val ACTIONS: DeferredRegister<ActionRegistryEntry> = DeferredRegister.create(Hierophantics.MOD_ID, HexRegistries.ACTION)

	private fun register(name: String, signature: String, startDir: HexDir, action: Action) {
		ACTIONS.register(name, {-> ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action)})
	}

	@JvmStatic
	fun init() {
		register("villager_sleep", "qqwqqwqqqqq", HexDir.SOUTH_WEST, OpVillagerSleep())
		register("get_minds", "qaqqaeawaea", HexDir.NORTH_EAST, OpGetMinds())
		register("get_mind_hex", "ddeqaa", HexDir.SOUTH_EAST, OpGetMindHex())
		register("set_mind_hex", "ddeedd", HexDir.SOUTH_EAST, OpSetMindHex())
		register("get_mind_trigger", "ddewaa", HexDir.SOUTH_EAST, OpGetMindTrigger())
		register("set_mind_trigger", "ddewdd", HexDir.SOUTH_EAST, OpSetMindTrigger())
		register("mute_mind", "ddweeee", HexDir.SOUTH_EAST, OpMuteMind())
		register("free_mind", "ddeawawa", HexDir.SOUTH_EAST, OpFreeMind())
		register("reenable_minds", "qaqqaeawaeaeqqqeqqwwqqeqqq", HexDir.NORTH_EAST, OpReenableMinds())
		register("make_damage_trigger", "qqqqqawwqdwa", HexDir.SOUTH_EAST, OpMakeTrigger("damage"))
		register("make_damage_type_trigger", "qqqqqawwqdwaqqwawqwa", HexDir.SOUTH_EAST, OpMakeDmgTypeTrigger())
		register("make_health_trigger", "qqqqqawwewawqada", HexDir.SOUTH_EAST, OpMakeThresholdTrigger("health"))
		register("make_breath_trigger", "qqqqqawweqqqqqaa", HexDir.SOUTH_EAST, OpMakeThresholdTrigger("breath"))
		register("make_hunger_trigger", "qqqqqawwaedwda", HexDir.SOUTH_EAST, OpMakeThresholdTrigger("hunger"))
		register("make_velocity_trigger", "qqqqqawwdaqqqaq", HexDir.SOUTH_EAST, OpMakeThresholdTrigger("velocity"))
		register("make_fall_trigger", "qqqqqawweawawa", HexDir.SOUTH_EAST, OpMakeThresholdTrigger("fall"))
		register("make_drop_trigger", "qqqqqawweaqaddwd", HexDir.SOUTH_EAST, OpMakeTrigger("drop"))
		register("make_attack_trigger", "qqqqqawwqwedweq", HexDir.SOUTH_EAST, OpMakeTrigger("attack"))
		register("make_break_trigger", "qqqqqawwwqaqqqqq", HexDir.SOUTH_EAST, OpMakeTrigger("break"))
		register("make_jump_trigger", "qqqqqawwqdwdwd", HexDir.SOUTH_EAST, OpMakeTrigger("jump"))
		register("make_teleport_trigger", "qqqqqawweaqaawaaqa", HexDir.SOUTH_EAST, OpMakeTrigger("teleport"))

		ACTIONS.register()
	}
}