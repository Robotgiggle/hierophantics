package robotgiggle.hierophantics.client

import net.minecraft.text.Text

object ClientStorage {
	@JvmField
	var ownedCassettes: Int = 0
	@JvmField
	val labels: LinkedHashMap<String, Text> = LinkedHashMap()
}