package robotgiggle.hierophantics.networking.msg

import robotgiggle.hierophantics.inits.HierophanticsConfig // gradle build fails without this, idk why
import net.minecraft.network.FriendlyByteBuf

data class MsgOwnedMindsS2C(val ownedMinds: Int) : HierophanticsMessageS2C {
    companion object : HierophanticsMessageCompanion<MsgOwnedMindsS2C> {
        override val type = MsgOwnedMindsS2C::class.java

        override fun decode(buf: FriendlyByteBuf) = MsgOwnedMindsS2C(
            ownedMinds = buf.readInt()
        )

        override fun MsgOwnedMindsS2C.encode(buf: FriendlyByteBuf) {
            buf.writeInt(ownedMinds)
        }
    }
}
