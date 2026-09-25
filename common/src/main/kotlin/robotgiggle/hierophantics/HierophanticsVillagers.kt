package robotgiggle.hierophantics

import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import java.util.function.Supplier

object HierophanticsVillagers {
    // For some reason, villager POIs only work on fabric if I use the fabric-specific PointOfInterestHelper to
    // register them. This means that villager setup has to be handled separately on each platform rather than
    // via the standard cross-platform registrar system.

    // This gets replaced with the actual profession by the platform-specific initializers
    // if the imbuement bed is making nitwits, something is very wrong
    @JvmField
    var QUILTMIND: Supplier<VillagerProfession> = Supplier{ VillagerProfession.NITWIT }

    @JvmField
    val FLAY_BED_POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Hierophantics.id("flay_bed"))
}