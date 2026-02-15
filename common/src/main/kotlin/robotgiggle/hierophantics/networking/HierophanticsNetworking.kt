package robotgiggle.hierophantics.networking

import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.networking.NetworkChannel
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.networking.msg.HierophanticsMessageCompanion
import robotgiggle.hierophantics.networking.msg.MsgOwnedMindsS2C

object HierophanticsNetworking {
    val CHANNEL: NetworkChannel = NetworkChannel.create(Hierophantics.id("networking_channel"))

    fun init() {
        for (subclass in HierophanticsMessageCompanion::class.sealedSubclasses) {
            subclass.objectInstance?.register(CHANNEL)
        }

        PlayerEvent.PLAYER_JOIN.register { player ->
            MsgOwnedMindsS2C(HieroServerState.getPlayerState(player).ownedMinds).sendToPlayer(player)
        }
    }
}
