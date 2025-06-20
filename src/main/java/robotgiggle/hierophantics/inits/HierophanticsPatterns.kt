package robotgiggle.hierophantics.inits

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions
import robotgiggle.hierophantics.HexcassettesUtils
import robotgiggle.hierophantics.patterns.*
import net.minecraft.registry.Registry

object HierophanticsPatterns {
	@JvmStatic
	fun init() {
		register("debug_add_mind", "wwaa", HexDir.EAST, OpDebugAddMind())
		register("get_minds", "qaqaeawaea", HexDir.NORTH_EAST, OpGetMinds())
		register("free_mind", "ddeawawa", HexDir.SOUTH_EAST, OpFreeMind())
		register("get_mind_hex", "ddeqaa", HexDir.SOUTH_EAST, OpGetMindHex())
		register("set_mind_hex", "ddeedd", HexDir.SOUTH_EAST, OpSetMindHex())
		register("get_mind_trigger", "ddewaa", HexDir.SOUTH_EAST, OpGetMindTrigger())
		register("set_mind_trigger", "ddewdd", HexDir.SOUTH_EAST, OpSetMindTrigger())
		register("make_damage_trigger", "qqqqqawwqdwa", HexDir.SOUTH_EAST, OpMakeTrigger(0))
		// attune_damage_trigger (NE dwaqqwawqwa)
		register("make_health_trigger", "qqqqqawwewawqada", HexDir.SOUTH_EAST, OpMakeThresholdTrigger(2))
		register("make_breath_trigger", "qqqqqawweqqqqqaa", HexDir.SOUTH_EAST, OpMakeThresholdTrigger(3))
		register("make_hunger_trigger", "qqqqqawwaedwda", HexDir.SOUTH_EAST, OpMakeThresholdTrigger(4))
		register("make_velocity_trigger", "qqqqqawwdaqqqaq", HexDir.SOUTH_EAST, OpMakeThresholdTrigger(5))
		register("make_fall_trigger", "qqqqqawweawawa", HexDir.SOUTH_EAST, OpMakeThresholdTrigger(6))
		register("make_drop_trigger", "qqqqqawweaqaddwd", HexDir.SOUTH_EAST, OpMakeTrigger(7))
		register("make_attack_trigger", "qqqqqawwqwedweq", HexDir.SOUTH_EAST, OpMakeTrigger(8))
		register("make_break_trigger", "qqqqqawwwqaqqqqq", HexDir.SOUTH_EAST, OpMakeTrigger(9))
		register("make_jump_trigger", "qqqqqawwqdwdwd", HexDir.SOUTH_EAST, OpMakeTrigger(10))
		register("make_teleport_trigger", "qqqqqawweaqaawaaqa", HexDir.SOUTH_EAST, OpMakeTrigger(11))
	}

	private fun register(name: String, signature: String, startDir: HexDir, action: Action) {
		Registry.register(HexActions.REGISTRY, HexcassettesUtils.id(name), ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action))
	}
}