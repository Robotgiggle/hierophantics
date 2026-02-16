package robotgiggle.hierophantics.networking.msg

import net.minecraft.network.PacketByteBuf

data class MsgOwnedMindsS2C(val ownedMinds: Int) : HierophanticsMessageS2C {
    companion object : HierophanticsMessageCompanion<MsgOwnedMindsS2C> {
        override val type = MsgOwnedMindsS2C::class.java

        override fun decode(buf: PacketByteBuf) = MsgOwnedMindsS2C(
            ownedMinds = buf.readInt()
        )

        override fun MsgOwnedMindsS2C.encode(buf: PacketByteBuf) {
            buf.writeInt(ownedMinds)
        }
    }
}
