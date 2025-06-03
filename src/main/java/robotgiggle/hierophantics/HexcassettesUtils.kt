package robotgiggle.hierophantics

import net.minecraft.util.Identifier

object HexcassettesUtils {
	fun id(string: String) = Identifier(HierophanticsMain.MOD_ID, string)
	fun shortenLabel(label: String) = label.substring(0, HierophanticsMain.MAX_LABEL_LENGTH.coerceAtMost(label.length))
}