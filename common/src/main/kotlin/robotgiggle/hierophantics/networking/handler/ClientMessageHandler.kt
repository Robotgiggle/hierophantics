package robotgiggle.hierophantics.networking.handler

import dev.architectury.networking.NetworkManager.PacketContext
import robotgiggle.hierophantics.HierophanticsClient
import robotgiggle.hierophantics.inits.HierophanticsConfig
import robotgiggle.hierophantics.networking.msg.*

fun HierophanticsMessageS2C.applyOnClient(ctx: PacketContext) = ctx.queue {
    when (this) {
        is MsgSyncConfigS2C -> {
            HierophanticsConfig.onSyncConfig(serverConfig)
        }

        is MsgOwnedMindsS2C -> {
            HierophanticsClient.clientOwnedMinds = ownedMinds
        }

        is MsgHallucinationTriggerS2C -> {
            HierophanticsClient.setHallucinationScaling(strength)
        }
    }
}
