package robotgiggle.hierophantics

import robotgiggle.hierophantics.config.HierophanticsConfig
import robotgiggle.hierophantics.config.HierophanticsConfig.GlobalConfig
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.gui.screen.Screen

object HierophanticsClient {
    fun init() {
        HierophanticsConfig.initClient()
    }

    fun getConfigScreen(parent: Screen): Screen {
        return AutoConfig.getConfigScreen(GlobalConfig::class.java, parent).get()
    }
}
