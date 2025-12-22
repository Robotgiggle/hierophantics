package robotgiggle.hierophantics

import net.minecraft.util.Identifier
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.item.Item
import net.minecraft.item.BlockItem
import net.minecraft.block.Block
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import robotgiggle.hierophantics.networking.HierophanticsNetworking
import robotgiggle.hierophantics.inits.*
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.HexAttributes
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.Iota

object Hierophantics {
    const val MOD_ID = "hierophantics"

    val TRIGGER_TYPES = listOf(
		"damage", "damage_typed", "health", "breath",
		"hunger","velocity", "fall", "drop", "attack",
		"break", "jump", "teleport"
	)

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    @JvmStatic
	fun id(string: String) = Identifier(MOD_ID, string)

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
