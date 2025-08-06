package robotgiggle.hierophantics.networking.handler

import dev.architectury.networking.NetworkManager.PacketContext
import robotgiggle.hierophantics.config.HierophanticsConfig
import robotgiggle.hierophantics.networking.msg.*

fun HierophanticsMessageS2C.applyOnClient(ctx: PacketContext) = ctx.queue {
    when (this) {
        is MsgSyncConfigS2C -> {
            HierophanticsConfig.onSyncConfig(serverConfig)
        }

        // add more client-side message handlers here
    }
}
