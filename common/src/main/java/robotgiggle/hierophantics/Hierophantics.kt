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
import robotgiggle.hierophantics.iotas.*
import robotgiggle.hierophantics.blocks.*
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.HexAttributes
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.Iota

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier

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
    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(MOD_ID, RegistryKeys.BLOCK)
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(MOD_ID, RegistryKeys.ITEM)
    val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<out BlockEntity>> = DeferredRegister.create(MOD_ID, RegistryKeys.BLOCK_ENTITY_TYPE)
    val EFFECTS: DeferredRegister<StatusEffect> = DeferredRegister.create(MOD_ID, RegistryKeys.STATUS_EFFECT)

    @JvmField
    val FLAY_BED_BLOCK: RegistrySupplier<FlayBedBlock> = BLOCKS.register("flay_bed", {-> FlayBedBlock()})
    val FLAY_BED_ITEM: RegistrySupplier<Item> = ITEMS.register("flay_bed", {-> BlockItem(FLAY_BED_BLOCK.get(), Item.Settings())})
    val FLAY_BED_BLOCK_ENTITY: RegistrySupplier<BlockEntityType<FlayBedBlockEntity>> = BLOCK_ENTITIES.register("flay_bed", {-> BlockEntityType.Builder.create(::FlayBedBlockEntity, FLAY_BED_BLOCK.get()).build(null)})

    @JvmField
    val EDIFIED_WORKSTATION_BLOCK: RegistrySupplier<Block> = BLOCKS.register("edified_workstation", {-> Block(Settings.copy(HexBlocks.EDIFIED_PLANKS))})
    val EDIFIED_WORKSTATION_ITEM: RegistrySupplier<Item> = ITEMS.register("edified_workstation", {-> BlockItem(EDIFIED_WORKSTATION_BLOCK.get(), Item.Settings())})

    @JvmField // the actual effects of this are handled in CastingEnvironmentMixin
    val MEDIA_DISCOUNT_EFFECT: RegistrySupplier<StatusEffect> = EFFECTS.register("media_discount", {-> (object : StatusEffect(StatusEffectCategory.BENEFICIAL, 0x64fbff) {})})
    @JvmField // the actual effects of this are handled in LivingEntityMixin
    val SLEEP_ANYWHERE_EFFECT: RegistrySupplier<StatusEffect> = EFFECTS.register("sleep_anywhere", {-> (object : StatusEffect(StatusEffectCategory.BENEFICIAL, 0) {})})

    @JvmStatic
	fun id(string: String) = Identifier(MOD_ID, string)

    fun init() {
        val IOTAS: DeferredRegister<IotaType<out Iota>> = DeferredRegister.create(MOD_ID, HexRegistries.IOTA_TYPE)
        IOTAS.register("mind_reference", {-> MindReferenceIota.TYPE})
		IOTAS.register("trigger", {-> TriggerIota.TYPE})
		IOTAS.register("mishap_thrower", {-> MishapThrowerIota.TYPE})

        BLOCKS.register()
        ITEMS.register()
        BLOCK_ENTITIES.register()
        EFFECTS.register()

		HierophanticsAdvancements.init()
		HierophanticsNetworking.init()
		HierophanticsPatterns.init()
		HierophanticsConfig.init()
		HierophanticsSounds.init()

        IOTAS.register()
    }
}
