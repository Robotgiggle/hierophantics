package robotgiggle.hierophantics.forge

import dev.architectury.platform.forge.EventBuses
import robotgiggle.hierophantics.Hierophantics
import net.minecraft.data.DataProvider
import net.minecraft.data.DataProvider.Factory
import net.minecraft.data.DataOutput
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Hierophantics.MODID)
class HierophanticsForge {
    init {
        MOD_BUS.apply {
            EventBuses.registerModEventBus(Hierophantics.MODID, this)
            addListener(ForgeHierophanticsClient::init)
            addListener(::gatherData)
        }
        Hierophantics.init()
    }

    private fun gatherData(event: GatherDataEvent) {
        event.apply {
            // TODO: add datagen providers here
        }
    }
}

fun <T : DataProvider> GatherDataEvent.addProvider(run: Boolean, factory: (DataOutput) -> T) =
    generator.addProvider(run, Factory { factory(it) })
