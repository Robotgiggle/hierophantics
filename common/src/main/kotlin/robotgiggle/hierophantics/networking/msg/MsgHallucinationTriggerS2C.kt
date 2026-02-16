package robotgiggle.hierophantics.networking.msg

import net.minecraft.network.PacketByteBuf

data class MsgHallucinationTriggerS2C(val strength: Double) : HierophanticsMessageS2C {
    companion object : HierophanticsMessageCompanion<MsgHallucinationTriggerS2C> {
        override val type = MsgHallucinationTriggerS2C::class.java

        override fun decode(buf: PacketByteBuf) = MsgHallucinationTriggerS2C(
            strength = buf.readDouble()
        )

        override fun MsgHallucinationTriggerS2C.encode(buf: PacketByteBuf) {
            buf.writeDouble(strength)
        }
    }
}
