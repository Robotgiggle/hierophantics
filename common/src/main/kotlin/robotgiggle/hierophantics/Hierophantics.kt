package robotgiggle.hierophantics

import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import robotgiggle.hierophantics.config.HierophanticsConfig
import robotgiggle.hierophantics.networking.HierophanticsNetworking
import robotgiggle.hierophantics.registry.HierophanticsActions

object Hierophantics {
    const val MODID = "hierophantics"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)

    @JvmStatic
    fun id(path: String) = Identifier(MODID, path)

    fun init() {
        HierophanticsConfig.init()
        initRegistries(
            HierophanticsActions,
        )
        HierophanticsNetworking.init()
    }
}
