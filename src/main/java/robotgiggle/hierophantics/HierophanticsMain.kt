package robotgiggle.hierophantics

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import robotgiggle.hierophantics.HierophanticsUtils.id
import robotgiggle.hierophantics.inits.HierophanticsPatterns
import robotgiggle.hierophantics.inits.HierophanticsSounds
import robotgiggle.hierophantics.iotas.*
import robotgiggle.hierophantics.blocks.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.entity.LivingEntity
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.BlockItem
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Rarity
import net.minecraft.world.World

class HierophanticsMain : ModInitializer {
	override fun onInitialize() {
		// val cassette = CassetteItem()
		// Registry.register(Registries.ITEM, id("cassette"), cassette)
		// ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(Registries.ITEM_GROUP.key, HexAPI.modLoc("hexcasting"))).register { group -> group.add(cassette) }

		Registry.register(Registries.BLOCK, id("flay_bed"), FLAY_BED_BLOCK)
		Registry.register(Registries.ITEM, id("flay_bed"), BlockItem(FLAY_BED_BLOCK, Item.Settings()))
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("flay_bed"), FLAY_BED_BLOCK_ENTITY)

		Registry.register(HexIotaTypes.REGISTRY, id("mind_reference"), MindReferenceIota.TYPE)
		Registry.register(HexIotaTypes.REGISTRY, id("trigger"), TriggerIota.TYPE)

		HierophanticsPatterns.init()
		HierophanticsSounds.init()
	}

	companion object {
		const val MOD_ID: String = "hierophantics"
		const val MAX_CASSETTES: Int = 6
		const val MAX_LABEL_LENGTH: Int = 32

		val FLAY_BED_BLOCK: FlayBedBlock = FlayBedBlock()
		val FLAY_BED_BLOCK_ENTITY: BlockEntityType<FlayBedBlockEntity> = BlockEntityType.Builder.create(::FlayBedBlockEntity, FLAY_BED_BLOCK).build(null)
	}
}