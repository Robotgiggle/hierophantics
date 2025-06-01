package robotgiggle.hierophantics

import robotgiggle.hierophantics.inits.HexcassettesNetworking
import net.fabricmc.api.ClientModInitializer

class HexcassettesClient : ClientModInitializer {
	override fun onInitializeClient() {
		HexcassettesNetworking.clientInit()
	}
}