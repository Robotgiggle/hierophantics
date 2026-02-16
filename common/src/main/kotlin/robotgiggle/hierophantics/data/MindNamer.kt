package robotgiggle.hierophantics.data

import net.minecraft.server.MinecraftServer
import kotlin.random.Random

// Thanks to miyucomics for this name generator!
// https://github.com/miyucomics/hexcellular/blob/d39eaa163e74d8a2c2187d2640ca390fd70836f2/src/main/java/miyucomics/hexcellular/PropertyNamer.kt

object MindNamer {
	private val consonants = charArrayOf('b', 'k', 'l', 'm', 'n', 'p', 'r', 't', 'd', 'f')
	private val vowels = charArrayOf('a', 'e', 'i', 'o', 'u')

	private val weights = listOf(0, 5, 3)
	private val scanned = weights.runningFold(0) { sum, weight -> sum + weight }.drop(1)
	private val peak = scanned.last()

	fun generateRandomName(server: MinecraftServer): String {
		val sState = HieroServerState.getServerState(server)
		var name: String
		do {
			val word = StringBuilder()
			val numberOfSyllables = generateNumberOfSyllables()
			repeat(numberOfSyllables) {
				val syllable = generateSyllable(word.endsWith('n'))
				word.append(syllable)
			}
			word.replace(0, 1, word.substring(0, 1).uppercase())
			name = word.toString()
		} while (sState.nameUsed(name))
		return name
	}

	private fun generateNumberOfSyllables(): Int {
		val index = Random.nextInt(1, peak + 1)
		return scanned.indexOfFirst { index <= it } + 1
	}

	private fun generateSyllable(wasNasal: Boolean): String {
		var consonant  = consonants.random()
		var vowel  = vowels.random()
		var syllable = "$consonant$vowel"
		while (wasNasal && (consonant == 'm' || consonant == 'n')) {
			consonant  = consonants.random()
			vowel  = vowels.random()
			syllable = "$consonant$vowel"
		}
		if (Random.nextBoolean() && !syllable.startsWith('n'))
			syllable += 'n'
		return syllable
	}

	fun processCustomName(server: MinecraftServer, baseName: String): String {
		val sState = HieroServerState.getServerState(server)
		var name = baseName
		var suffix = 2
		while (sState.nameUsed(name)) {
			name = baseName + " " + numToRoman(suffix)
			suffix++
		}
		return name
	}

	// Credit to André Kramer Orten on stackoverflow for this
	private fun numToRoman(number: Int): String {
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
            .replace("DCD", "CM")
	}
}