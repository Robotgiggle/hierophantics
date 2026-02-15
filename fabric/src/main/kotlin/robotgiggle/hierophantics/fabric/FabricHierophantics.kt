package robotgiggle.hierophantics.fabric

import at.petrak.hexcasting.api.HexAPI
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.inits.HierophanticsCommands
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents

object FabricHierophantics : ModInitializer {
    override fun onInitialize() {
        Hierophantics.init()
        CommandRegistrationCallback.EVENT.register{dp, _, _ -> HierophanticsCommands.register(dp)}
        FabricHierophanticsVillagers.init()
    }
}
