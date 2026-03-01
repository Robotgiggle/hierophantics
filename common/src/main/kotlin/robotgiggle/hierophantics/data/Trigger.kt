package robotgiggle.hierophantics.data

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement

@JvmRecord
data class Trigger(val type: String, val threshold: Double, val dmgType: String, val inverted: Boolean) {
    fun serialize(): NbtCompound {
        val compound = NbtCompound()
        compound.putString("trigger", type)
        compound.putDouble("threshold", threshold)
        compound.putString("dmgType", dmgType)
        compound.putBoolean("inverted", inverted)
        return compound
    }
    fun passedThreshold(currVal: Double, prevVal: Double): Boolean {
        val upward = if (type == "velocity" || type == "fall") !inverted else inverted
        if (upward) return currVal > threshold && prevVal <= threshold && prevVal != -1.0
        else return currVal < threshold && prevVal >= threshold
    }
    companion object {
        fun deserialize(nbt: NbtElement): Trigger {
            val type = (nbt as NbtCompound).getString("trigger")
            val threshold = nbt.getDouble("threshold")
            val dmgType = nbt.getString("dmgType")
            val inverted = nbt.getBoolean("inverted")
            return Trigger(type, threshold, dmgType, inverted)
        }
        fun none() = Trigger("none", -1.0, "", false)
    }
}