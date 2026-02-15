package robotgiggle.hierophantics.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import robotgiggle.hierophantics.HierophanticsClient

object FabricHierophanticsModMenu : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory(HierophanticsClient::getConfigScreen)
}
