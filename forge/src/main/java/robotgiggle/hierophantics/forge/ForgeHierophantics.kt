package robotgiggle.hierophantics.forge

import dev.architectury.platform.forge.EventBuses
import at.petrak.hexcasting.api.HexAPI
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.inits.HierophanticsCommands
import net.minecraft.data.DataProvider
import net.minecraft.data.DataProvider.Factory
import net.minecraft.data.DataOutput
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Hierophantics.MOD_ID)
class HierophanticsForge {
    init {
        MOD_BUS.apply {
            EventBuses.registerModEventBus(Hierophantics.MOD_ID, this)
            addListener(ForgeHierophanticsClient::init)
            // addListener{evt: BuildCreativeModeTabContentsEvent -> 
            //     if (evt.getTabKey() == RegistryKey.of(Registries.ITEM_GROUP.key, HexAPI.modLoc("hexcasting"))) {
            //         evt.accept{ -> Hierophantics.FLAY_BED_ITEM.get()}
            //         evt.accept{ -> Hierophantics.EDIFIED_WORKSTATION_ITEM.get()}
            //     }
            // }
            addListener(ForgeHierophanticsVillagers::init)
        }
        Hierophantics.init()
        FORGE_BUS.addListener{evt: RegisterCommandsEvent -> HierophanticsCommands.register(evt.getDispatcher())};
    }
}
