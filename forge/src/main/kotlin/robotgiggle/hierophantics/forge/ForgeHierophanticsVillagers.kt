package robotgiggle.hierophantics.forge

import com.google.common.collect.ImmutableSet

import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.state.properties.BedPart
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.HierophanticsVillagers
import robotgiggle.hierophantics.inits.HierophanticsBlocks

import net.minecraftforge.registries.RegisterEvent

import dev.architectury.registry.registries.DeferredRegister

object ForgeHierophanticsVillagers {
    val POI_TYPES: DeferredRegister<PoiType> = DeferredRegister.create(Hierophantics.MOD_ID, Registries.POINT_OF_INTEREST_TYPE)
    val PROFESSIONS: DeferredRegister<VillagerProfession> = DeferredRegister.create(Hierophantics.MOD_ID, Registries.VILLAGER_PROFESSION)

    val FLAY_BED_POI = POI_TYPES.register("flay_bed", { PoiType(HierophanticsBlocks.FLAY_BED.value
        .getStateDefinition().getPossibleStates().stream()
        .filter({ blockState ->
            blockState.getValue(BedBlock.PART) == BedPart.HEAD
        })
        .collect(ImmutableSet.toImmutableSet()),
    1, 1) })

    val QUILTMIND_POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Hierophantics.id("quiltmind"))
    val QUILTMIND_POI = POI_TYPES.register("quiltmind", { PoiType(HierophanticsBlocks.EDIFIED_WORKSTATION.value.getStateDefinition().getPossibleStates().toSet(), 1, 1) })
    val QUILTMIND = PROFESSIONS.register("quiltmind", { VillagerProfession(
        "quiltmind", 
        {e -> e.`is`(QUILTMIND_POI_KEY)},
        {e -> e.`is`(QUILTMIND_POI_KEY)},
        ImmutableSet.of(), 
        ImmutableSet.of(), 
        SoundEvents.ENCHANTMENT_TABLE_USE
    ) })

    var registered = false
    
    fun init(event: RegisterEvent) {
        if (!registered) {
            POI_TYPES.register()
            PROFESSIONS.register()
            HierophanticsVillagers.QUILTMIND = QUILTMIND
            registered = true
        }
    }
}
