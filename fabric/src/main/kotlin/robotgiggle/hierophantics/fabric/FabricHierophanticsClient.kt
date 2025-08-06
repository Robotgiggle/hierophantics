package robotgiggle.hierophantics.fabric

import robotgiggle.hierophantics.HierophanticsClient
import net.fabricmc.api.ClientModInitializer

object FabricHierophanticsClient : ClientModInitializer {
    override fun onInitializeClient() {
        HierophanticsClient.init()
    }
}
