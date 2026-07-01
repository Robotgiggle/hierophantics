package robotgiggle.hierophantics

import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import robotgiggle.hierophantics.networking.HierophanticsNetworking
import robotgiggle.hierophantics.inits.*
import java.time.LocalDate

object Hierophantics {
    const val MOD_ID = "hierophantics"

    val TRIGGER_TYPES = listOf(
		"damage", "damage_typed", "health", "breath",
		"hunger","velocity", "fall", "drop", "attack",
		"break", "jump", "teleport"
	)

    @JvmField
    val BYPASSES_DAMAGE_TRIGGER: TagKey<DamageType> = TagKey.of(RegistryKeys.DAMAGE_TYPE, id("bypasses_damage_trigger"))

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    @JvmField
    var lastAction: Identifier = Identifier("hexcasting", "get_caster")

    @JvmStatic
	fun id(string: String) = Identifier(MOD_ID, string)

    @JvmStatic
    fun isAprilFools(): Boolean {
        val today = LocalDate.now()
        return (today.monthValue == 4 && today.dayOfMonth == 1)
    }

    fun init() {
        HierophanticsAdvancements.init()
		HierophanticsNetworking.init()
        initRegistries(
            HierophanticsActions,
            HierophanticsBlocks,
            HierophanticsBlockEntities,
            HierophanticsEffects,
            HierophanticsItems,
            HierophanticsIotaTypes,
            HierophanticsSounds,
        )
		HierophanticsConfig.init()
    }
}
