package robotgiggle.hierophantics

import net.minecraft.village.VillagerProfession
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryKey
import java.util.function.Supplier

object HierophanticsVillagers {
    // this gets replaced with the actual profession by the platform-specific initializers
    // if the imbuement bed is making nitwits, something is very wrong
    @JvmField
    var QUILTMIND: Supplier<VillagerProfession> = Supplier{ VillagerProfession.NITWIT }

    @JvmField
    val FLAY_BED_POI_KEY = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Hierophantics.id("flay_bed"))
}