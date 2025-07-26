package robotgiggle.hierophantics.data

import net.minecraft.nbt.NbtCompound
import robotgiggle.hierophantics.HierophanticsUtils

fun updateTriggerNbt(nbt: NbtCompound) {
    val triggerId = nbt.getInt("triggerId")
    val triggerString = nbt.getString("trigger")
    if (triggerString.equals("")) {
        if (triggerId == -1) nbt.putString("trigger", "none")
        else nbt.putString("trigger", HierophanticsUtils.TRIGGER_NAMES[triggerId])
    }
}