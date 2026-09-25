package robotgiggle.hierophantics.fabric

import com.google.common.collect.ImmutableSet

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.registry.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceKeys
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.block.BedBlock
import net.minecraft.block.enums.BedPart
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.HierophanticsVillagers
import robotgiggle.hierophantics.inits.HierophanticsBlocks
import net.fabricmc.fabric.api.`object`.builder.v1.world.poi.PointOfInterestHelper

import java.util.function.Supplier

object FabricHierophanticsVillagers {
    val FLAY_BED_ID = Hierophantics.id("flay_bed")
    val FLAY_BED_POI = PointOfInterestHelper.register(FLAY_BED_ID, 1, 1, HierophanticsBlocks.FLAY_BED.value
        .getStateManager().getStates().stream()
        .filter({blockState -> 
            blockState.get(BedBlock.PART) == BedPart.HEAD
        })
        .collect(ImmutableSet.toImmutableSet())
    )

    val QUILTMIND_ID = Hierophantics.id("quiltmind")
    val QUILTMIND_POI = PointOfInterestHelper.register(QUILTMIND_ID, 1, 1, HierophanticsBlocks.EDIFIED_WORKSTATION.value)
    val QUILTMIND_POI_KEY = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, QUILTMIND_ID)
    val QUILTMIND = VillagerProfession(
        "quiltmind", 
        {e -> e.matchesKey(QUILTMIND_POI_KEY)}, 
        {e -> e.matchesKey(QUILTMIND_POI_KEY)}, 
        ImmutableSet.of(), 
        ImmutableSet.of(), 
        SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE
    )
    
    fun init() {
        Registry.register(Registries.VILLAGER_PROFESSION, QUILTMIND_ID, QUILTMIND)
        HierophanticsVillagers.QUILTMIND = Supplier{ QUILTMIND }
    }
}
