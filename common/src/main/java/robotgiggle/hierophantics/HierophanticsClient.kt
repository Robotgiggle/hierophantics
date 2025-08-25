package robotgiggle.hierophantics

import robotgiggle.hierophantics.inits.HierophanticsConfig
import robotgiggle.hierophantics.inits.HierophanticsConfig.GlobalConfig
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.gui.screen.Screen

object HierophanticsClient {
    @JvmStatic
    var clientOwnedMinds = 0;
    
    fun init() {
        HierophanticsConfig.initClient()
    }

    fun getConfigScreen(parent: Screen): Screen {
        return AutoConfig.getConfigScreen(GlobalConfig::class.java, parent).get()
    }
}
