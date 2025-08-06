package robotgiggle.hierophantics.forge

import robotgiggle.hierophantics.HierophanticsClient
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT

object ForgeHierophanticsClient {
    fun init(event: FMLClientSetupEvent) {
        HierophanticsClient.init()
        LOADING_CONTEXT.registerExtensionPoint(ConfigScreenFactory::class.java) {
            ConfigScreenFactory { _, parent -> HierophanticsClient.getConfigScreen(parent) }
        }
    }
}
