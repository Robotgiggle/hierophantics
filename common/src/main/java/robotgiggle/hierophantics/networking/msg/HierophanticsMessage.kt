package robotgiggle.hierophantics.networking.msg

import dev.architectury.networking.NetworkChannel
import dev.architectury.networking.NetworkManager.PacketContext
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.networking.HierophanticsNetworking
import robotgiggle.hierophantics.networking.handler.applyOnClient
import robotgiggle.hierophantics.networking.handler.applyOnServer
import net.fabricmc.api.EnvType
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import java.util.function.Supplier

sealed interface HierophanticsMessage

sealed interface HierophanticsMessageC2S : HierophanticsMessage {
    fun sendToServer() {
        HierophanticsNetworking.CHANNEL.sendToServer(this)
    }
}

sealed interface HierophanticsMessageS2C : HierophanticsMessage {
    fun sendToPlayer(player: ServerPlayerEntity) {
        HierophanticsNetworking.CHANNEL.sendToPlayer(player, this)
    }

    fun sendToPlayers(players: Iterable<ServerPlayerEntity>) {
        HierophanticsNetworking.CHANNEL.sendToPlayers(players, this)
    }
}

sealed interface HierophanticsMessageCompanion<T : HierophanticsMessage> {
    val type: Class<T>

    fun decode(buf: PacketByteBuf): T

    fun T.encode(buf: PacketByteBuf)

    fun apply(msg: T, supplier: Supplier<PacketContext>) {
        val ctx = supplier.get()
        when (ctx.env) {
            EnvType.SERVER, null -> {
                Hierophantics.LOGGER.debug("Server received packet from {}: {}", ctx.player.name.string, this)
                when (msg) {
                    is HierophanticsMessageC2S -> msg.applyOnServer(ctx)
                    else -> Hierophantics.LOGGER.warn("Message not handled on server: {}", msg::class)
                }
            }
            EnvType.CLIENT -> {
                Hierophantics.LOGGER.debug("Client received packet: {}", this)
                when (msg) {
                    is HierophanticsMessageS2C -> msg.applyOnClient(ctx)
                    else -> Hierophantics.LOGGER.warn("Message not handled on client: {}", msg::class)
                }
            }
        }
    }

    fun register(channel: NetworkChannel) {
        channel.register(type, { msg, buf -> msg.encode(buf) }, ::decode, ::apply)
    }
}
