package robotgiggle.hierophantics.forge

import dev.architectury.platform.forge.EventBuses
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.inits.HierophanticsCommands
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Hierophantics.MOD_ID)
class HierophanticsForge {
    init {
        MOD_BUS.apply {
            EventBuses.registerModEventBus(Hierophantics.MOD_ID, this)
            addListener(ForgeHierophanticsClient::init)
            addListener(ForgeHierophanticsVillagers::init)
        }
        Hierophantics.init()
        FORGE_BUS.addListener{evt: RegisterCommandsEvent -> HierophanticsCommands.register(evt.getDispatcher())}
    }
}
