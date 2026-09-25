package robotgiggle.hierophantics

import net.minecraft.world.damagesource.DamageType
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.resources.ResourceLocation
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
    val BYPASSES_DAMAGE_TRIGGER: TagKey<DamageType> = TagKey.create(Registries.DAMAGE_TYPE, id("bypasses_damage_trigger"))

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    @JvmStatic
	fun id(string: String) = ResourceLocation(MOD_ID, string)

    @JvmStatic
    fun isAprilFools(): Boolean {
        val today = LocalDate.now()
        return (today.monthValue == 4 && today.dayOfMonth == 1)
    }

    fun init() {
        HierophanticsAdvancements.init()
		HierophanticsNetworking.init()
        HierophanticsConfig.init()
        initRegistries(
            HierophanticsActions,
            HierophanticsBlocks,
            HierophanticsBlockEntities,
            HierophanticsEffects,
            HierophanticsItems,
            HierophanticsIotaTypes,
            HierophanticsSounds,
        )
    }
}
