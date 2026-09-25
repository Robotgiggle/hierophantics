package robotgiggle.hierophantics.networking.msg

import robotgiggle.hierophantics.inits.HierophanticsConfig // gradle build fails without this, idk why
import net.minecraft.network.FriendlyByteBuf

data class MsgHallucinationTriggerS2C(val strength: Double) : HierophanticsMessageS2C {
    companion object : HierophanticsMessageCompanion<MsgHallucinationTriggerS2C> {
        override val type = MsgHallucinationTriggerS2C::class.java

        override fun decode(buf: FriendlyByteBuf) = MsgHallucinationTriggerS2C(
            strength = buf.readDouble()
        )

        override fun MsgHallucinationTriggerS2C.encode(buf: FriendlyByteBuf) {
            buf.writeDouble(strength)
        }
    }
}
