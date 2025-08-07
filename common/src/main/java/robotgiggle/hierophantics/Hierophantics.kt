package robotgiggle.hierophantics

import net.minecraft.util.Identifier
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.item.Item
import net.minecraft.item.BlockItem
import net.minecraft.block.Block
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.entity.BlockEntityType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import robotgiggle.hierophantics.networking.HierophanticsNetworking
import robotgiggle.hierophantics.inits.*
import robotgiggle.hierophantics.iotas.*
import robotgiggle.hierophantics.blocks.*
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes

object Hierophantics {
    const val MOD_ID = "hierophantics"

    val TRIGGER_NAMES = listOf(
		"damage", "damage_typed", "health", "breath",
		"hunger","velocity", "fall", "drop", "attack",
		"break", "jump", "teleport"
	)

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    @JvmField
    val FLAY_BED_BLOCK: FlayBedBlock = FlayBedBlock()
    val FLAY_BED_ITEM: BlockItem = BlockItem(FLAY_BED_BLOCK, Item.Settings())
    val FLAY_BED_BLOCK_ENTITY: BlockEntityType<FlayBedBlockEntity> = BlockEntityType.Builder.create(::FlayBedBlockEntity, FLAY_BED_BLOCK).build(null)

    @JvmField
    val EDIFIED_WORKSTATION_BLOCK: Block = Block(Settings.copy(HexBlocks.EDIFIED_PLANKS))
    val EDIFIED_WORKSTATION_ITEM: BlockItem = BlockItem(EDIFIED_WORKSTATION_BLOCK, Item.Settings())

    @JvmStatic
	fun id(string: String) = Identifier(MOD_ID, string)

    fun init() {
        Registry.register(Registries.BLOCK, id("flay_bed"), FLAY_BED_BLOCK)
		Registry.register(Registries.ITEM, id("flay_bed"), FLAY_BED_ITEM)
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("flay_bed"), FLAY_BED_BLOCK_ENTITY)

		Registry.register(Registries.BLOCK, id("edified_workstation"), EDIFIED_WORKSTATION_BLOCK)
		Registry.register(Registries.ITEM, id("edified_workstation"), EDIFIED_WORKSTATION_ITEM)

		Registry.register(HexIotaTypes.REGISTRY, id("mind_reference"), MindReferenceIota.TYPE)
		Registry.register(HexIotaTypes.REGISTRY, id("trigger"), TriggerIota.TYPE)
		Registry.register(HexIotaTypes.REGISTRY, id("mishap_thrower"), MishapThrowerIota.TYPE)

		HierophanticsAdvancements.init()
		HierophanticsNetworking.init()
		HierophanticsVillagers.init()
		HierophanticsPatterns.init()
		HierophanticsConfig.init()
		HierophanticsSounds.init()
    }

    // Credit to André Kramer Orten on stackoverflow for this
	fun numToRoman(number: Int): String {
		return "I".repeat(number)
            .replace("IIIII", "V")
            .replace("IIII", "IV")
            .replace("VV", "X")
            .replace("VIV", "IX")
            .replace("XXXXX", "L")
            .replace("XXXX", "XL")
            .replace("LL", "C")
            .replace("LXL", "XC")
            .replace("CCCCC", "D")
            .replace("CCCC", "CD")
            .replace("DD", "M")
            .replace("DCD", "CM");
	}
}
