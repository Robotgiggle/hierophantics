package robotgiggle.hierophantics

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import robotgiggle.hierophantics.HierophanticsUtils.id
import robotgiggle.hierophantics.inits.*
import robotgiggle.hierophantics.iotas.*
import robotgiggle.hierophantics.blocks.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.entity.LivingEntity
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.BlockItem
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Rarity
import net.minecraft.world.World

import net.minecraft.block.Blocks;

class HierophanticsMain : ModInitializer {
	override fun onInitialize() {
		Registry.register(Registries.BLOCK, id("flay_bed"), FLAY_BED_BLOCK)
		Registry.register(Registries.ITEM, id("flay_bed"), FLAY_BED_ITEM)
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("flay_bed"), FLAY_BED_BLOCK_ENTITY)

		Registry.register(Registries.BLOCK, id("edified_workstation"), EDIFIED_WORKSTATION_BLOCK)
		Registry.register(Registries.ITEM, id("edified_workstation"), EDIFIED_WORKSTATION_ITEM)

		ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(Registries.ITEM_GROUP.key, HexAPI.modLoc("hexcasting"))).register { group -> group.add(FLAY_BED_ITEM) }
		ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(Registries.ITEM_GROUP.key, HexAPI.modLoc("hexcasting"))).register { group -> group.add(EDIFIED_WORKSTATION_ITEM) }

		Registry.register(HexIotaTypes.REGISTRY, id("mind_reference"), MindReferenceIota.TYPE)
		Registry.register(HexIotaTypes.REGISTRY, id("trigger"), TriggerIota.TYPE)
		Registry.register(HexIotaTypes.REGISTRY, id("mishap_thrower"), MishapThrowerIota.TYPE)

		HierophanticsAdvancements.init()
		HierophanticsVillagers.init()
		HierophanticsPatterns.init()
		HierophanticsSounds.init()
	}

	companion object {
		const val MOD_ID: String = "hierophantics"

		@JvmField
		val FLAY_BED_BLOCK: FlayBedBlock = FlayBedBlock()
		val FLAY_BED_ITEM: BlockItem = BlockItem(FLAY_BED_BLOCK, Item.Settings())
		val FLAY_BED_BLOCK_ENTITY: BlockEntityType<FlayBedBlockEntity> = BlockEntityType.Builder.create(::FlayBedBlockEntity, FLAY_BED_BLOCK).build(null)

		@JvmField
		val EDIFIED_WORKSTATION_BLOCK: Block = Block(Settings.copy(HexBlocks.EDIFIED_PLANKS))
		val EDIFIED_WORKSTATION_ITEM: BlockItem = BlockItem(EDIFIED_WORKSTATION_BLOCK, Item.Settings())
	}
}