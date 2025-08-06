package robotgiggle.hierophantics.networking.msg

import robotgiggle.hierophantics.config.HierophanticsConfig
import net.minecraft.network.PacketByteBuf

data class MsgSyncConfigS2C(val serverConfig: HierophanticsConfig.ServerConfig) : HierophanticsMessageS2C {
    companion object : HierophanticsMessageCompanion<MsgSyncConfigS2C> {
        override val type = MsgSyncConfigS2C::class.java

        override fun decode(buf: PacketByteBuf) = MsgSyncConfigS2C(
            serverConfig = HierophanticsConfig.ServerConfig.decode(buf),
        )

        override fun MsgSyncConfigS2C.encode(buf: PacketByteBuf) {
            serverConfig.encode(buf)
        }
    }
}
