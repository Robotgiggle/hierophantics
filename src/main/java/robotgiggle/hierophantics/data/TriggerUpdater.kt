package robotgiggle.hierophantics.data

import net.minecraft.nbt.NbtCompound

import at.petrak.hexcasting.api.HexAPI

val triggerNames = listOf(
    "damage",
    "damage_typed",
    "health",
    "breath",
    "hunger",
    "velocity",
    "fall",
    "drop",
    "attack",
    "break",
    "jump",
    "teleport"
)

fun updateTriggerNbt(nbt: NbtCompound) {
    val triggerId = nbt.getInt("triggerId")
    val triggerString = nbt.getString("trigger")
    if (triggerString.equals("")) {
        if (triggerId == -1) nbt.putString("trigger", "none")
        else nbt.putString("trigger", triggerNames[triggerId])
    }
}