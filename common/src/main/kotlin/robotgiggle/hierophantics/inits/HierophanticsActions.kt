package robotgiggle.hierophantics.inits

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexActions
import robotgiggle.hierophantics.patterns.*

object HierophanticsActions : HierophanticsRegistrar<ActionRegistryEntry>(HexRegistries.ACTION, { HexActions.REGISTRY }) {
    val VILLAGER_SLEEP = make("villager_sleep", HexDir.SOUTH_WEST, "qqwqqwqqqqq", OpVillagerSleep)
    
    val GET_MINDS = make("get_minds", HexDir.NORTH_EAST, "qaqqaeawaea", OpGetMinds)
    val GET_MIND_HEX = make("get_mind_hex", HexDir.SOUTH_EAST, "ddeqaa", OpGetMindHex)
    val SET_MIND_HEX = make("set_mind_hex", HexDir.SOUTH_EAST, "ddeedd", OpSetMindHex)
    val GET_MIND_TRIGGER = make("get_mind_trigger", HexDir.SOUTH_EAST, "ddewaa", OpGetMindTrigger)
    val SET_MIND_TRIGGER = make("set_mind_trigger", HexDir.SOUTH_EAST, "ddewdd", OpSetMindTrigger)
    val MUTE_MIND = make("mute_mind", HexDir.SOUTH_EAST, "ddweeee", OpMuteMind)
    val FREE_MIND = make("free_mind", HexDir.SOUTH_EAST, "ddeawawa", OpFreeMind)
    val REENABLE_MINDS = make("reenable_minds", HexDir.NORTH_EAST, "qaqqaeawaeaeqqqeqqwwqqeqqq", OpReenableMinds)
    
    val MAKE_DAMAGE_TRIGGER = make("make_damage_trigger", HexDir.SOUTH_EAST, "qqqqqawwqdwa", OpMakeTrigger("damage"))
    val MAKE_DAMAGE_TYPE_TRIGGER = make("make_damage_type_trigger", HexDir.SOUTH_EAST, "qqqqqawwqdwaqqwawqwa", OpMakeDmgTypeTrigger)
    val MAKE_HEALTH_TRIGGER = make("make_health_trigger", HexDir.SOUTH_EAST, "qqqqqawwewawqada", OpMakeThresholdTrigger("health"))
    val MAKE_BREATH_TRIGGER = make("make_breath_trigger", HexDir.SOUTH_EAST, "qqqqqawweqqqqqaa", OpMakeThresholdTrigger("breath"))
    val MAKE_HUNGER_TRIGGER = make("make_hunger_trigger", HexDir.SOUTH_EAST, "qqqqqawwaedwda", OpMakeThresholdTrigger("hunger"))
    val MAKE_VELOCITY_TRIGGER = make("make_velocity_trigger", HexDir.SOUTH_EAST, "qqqqqawwdaqqqaq", OpMakeThresholdTrigger("velocity"))
    val MAKE_FALL_TRIGGER = make("make_fall_trigger", HexDir.SOUTH_EAST, "qqqqqawweawawa", OpMakeThresholdTrigger("fall"))
    val MAKE_DROP_TRIGGER = make("make_drop_trigger", HexDir.SOUTH_EAST, "qqqqqawweaqaddwd", OpMakeTrigger("drop"))
    val MAKE_ATTACK_TRIGGER = make("make_attack_trigger", HexDir.SOUTH_EAST, "qqqqqawwqwedweq", OpMakeTrigger("attack"))
    val MAKE_BREAK_TRIGGER = make("make_break_trigger", HexDir.SOUTH_EAST, "qqqqqawwwqaqqqqq", OpMakeTrigger("break"))
    val MAKE_JUMP_TRIGGER = make("make_jump_trigger", HexDir.SOUTH_EAST, "qqqqqawwqdwdwd", OpMakeTrigger("jump"))
    val MAKE_TELEPORT_TRIGGER = make("make_teleport_trigger", HexDir.SOUTH_EAST, "qqqqqawweaqaawaaqa", OpMakeTrigger("teleport"))
    
    val INVERT_TRIGGER = make("invert_trigger", HexDir.NORTH_EAST, "aawddeeqqqqqaw", OpInvertTrigger)
    
    private fun make(name: String, startDir: HexDir, signature: String, action: Action) =
        make(name, startDir, signature) { action }

    private fun make(name: String, startDir: HexDir, signature: String, getAction: () -> Action) = register(name) {
        ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), getAction())
    }
}