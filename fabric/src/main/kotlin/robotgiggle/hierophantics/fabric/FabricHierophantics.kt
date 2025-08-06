package robotgiggle.hierophantics.fabric

import robotgiggle.hierophantics.Hierophantics
import net.fabricmc.api.ModInitializer

object FabricHierophantics : ModInitializer {
    override fun onInitialize() {
        Hierophantics.init()
    }
}
