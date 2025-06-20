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
		register("get_minds", "waa", HexDir.EAST, OpGetMinds())
		register("free_mind", "wdd", HexDir.EAST, OpFreeMind())
		register("get_mind_hex", "qaa", HexDir.EAST, OpGetMindHex())
		register("set_mind_hex", "edd", HexDir.EAST, OpSetMindHex())
		// get_mind_trigger
		// set_mind_trigger
		// make_damage_trigger
		// attune_damage_trigger
		// make_health_trigger
		// make_breath_trigger
		// make_hunger_trigger
		// make_velocity_trigger
		// make_fall_trigger
		// make_drop_trigger
		// make_atttack_trigger
		// make_break_trigger
		// make_jump_trigger
		// make_teleport_trigger
	}

	private fun register(name: String, signature: String, startDir: HexDir, action: Action) {
		Registry.register(HexActions.REGISTRY, HexcassettesUtils.id(name), ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action))
	}
}