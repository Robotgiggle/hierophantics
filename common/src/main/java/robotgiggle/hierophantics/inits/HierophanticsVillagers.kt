package robotgiggle.hierophantics.inits;

import com.google.common.collect.ImmutableSet;

import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import robotgiggle.hierophantics.Hierophantics;

object HierophanticsVillagers {
    val QUILTMIND_ID = Hierophantics.id("quiltmind");
    val QUILTMIND_POI_KEY = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, QUILTMIND_ID);
    val QUILTMIND_POI = PointOfInterestType(Hierophantics.EDIFIED_WORKSTATION_BLOCK.getStateManager().getStates().toSet(), 1, 1)
    val QUILTMIND = VillagerProfession(
        "quiltmind", 
        {e -> e.matchesKey(QUILTMIND_POI_KEY)}, 
        {e -> e.matchesKey(QUILTMIND_POI_KEY)}, 
        ImmutableSet.of(), 
        ImmutableSet.of(), 
        SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE
    )
    
    fun init() {
        Registry.register(Registries.VILLAGER_PROFESSION, QUILTMIND_ID, QUILTMIND);
    }
}
