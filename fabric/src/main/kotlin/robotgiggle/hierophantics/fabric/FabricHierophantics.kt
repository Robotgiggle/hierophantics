package robotgiggle.hierophantics.fabric

import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.inits.HierophanticsCommands
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback

object FabricHierophantics : ModInitializer {
    override fun onInitialize() {
        Hierophantics.init()
        CommandRegistrationCallback.EVENT.register{dp, _, _ -> HierophanticsCommands.register(dp)}
        FabricHierophanticsVillagers.init()
    }
}
