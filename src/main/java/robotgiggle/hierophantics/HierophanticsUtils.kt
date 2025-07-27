package robotgiggle.hierophantics

import net.minecraft.util.Identifier

object HierophanticsUtils {
	val TRIGGER_NAMES = listOf(
		"damage", "damage_typed", "health", "breath",
		"hunger","velocity", "fall", "drop", "attack",
		"break", "jump", "teleport"
	)
	
      @JvmStatic
	fun id(string: String) = Identifier(HierophanticsMain.MOD_ID, string)

	// Credit to André Kramer Orten on stackoverflow for this
	fun numToRoman(number: Int): String {
		return "I".repeat(number)
            .replace("IIIII", "V")
            .replace("IIII", "IV")
            .replace("VV", "X")
            .replace("VIV", "IX")
            .replace("XXXXX", "L")
            .replace("XXXX", "XL")
            .replace("LL", "C")
            .replace("LXL", "XC")
            .replace("CCCCC", "D")
            .replace("CCCC", "CD")
            .replace("DD", "M")
            .replace("DCD", "CM");
	}
}