package robotgiggle.hierophantics.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag

@JvmRecord
data class Trigger(val type: String, val threshold: Double, val dmgType: String, val inverted: Boolean) {
    fun serialize(): CompoundTag {
        val compound = CompoundTag()
        compound.putString("trigger", type)
        compound.putDouble("threshold", threshold)
        compound.putString("dmgType", dmgType)
        compound.putBoolean("inverted", inverted)
        return compound
    }
    fun passedThreshold(currVal: Double, prevVal: Double): Boolean {
        val upward = if (type == "velocity" || type == "fall") !inverted else inverted
        if (upward) return currVal > threshold && prevVal <= threshold && prevVal != -1.0
        return currVal < threshold && prevVal >= threshold
    }
    companion object {
        fun deserialize(nbt: Tag): Trigger {
            val type = (nbt as CompoundTag).getString("trigger")
            val threshold = nbt.getDouble("threshold")
            val dmgType = nbt.getString("dmgType")
            val inverted = nbt.getBoolean("inverted")
            return Trigger(type, threshold, dmgType, inverted)
        }
        fun none() = Trigger("none", -1.0, "", false)
    }
}