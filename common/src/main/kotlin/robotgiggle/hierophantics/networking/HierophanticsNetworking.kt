package robotgiggle.hierophantics.networking

import dev.architectury.networking.NetworkChannel
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.networking.msg.HierophanticsMessageCompanion

object HierophanticsNetworking {
    val CHANNEL: NetworkChannel = NetworkChannel.create(Hierophantics.id("networking_channel"))

    fun init() {
        for (subclass in HierophanticsMessageCompanion::class.sealedSubclasses) {
            subclass.objectInstance?.register(CHANNEL)
        }
    }
}
