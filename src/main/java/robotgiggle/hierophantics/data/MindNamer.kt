package robotgiggle.hierophantics.data

import kotlin.random.Random

// Thanks to miyucomics for this name generator!
// https://github.com/miyucomics/hexcellular/blob/d39eaa163e74d8a2c2187d2640ca390fd70836f2/src/main/java/miyucomics/hexcellular/PropertyNamer.kt

private val consonants = charArrayOf('b', 'k', 'l', 'm', 'n', 'p', 'r', 't', 'd', 'f')
private val vowels = charArrayOf('a', 'e', 'i', 'o', 'u')

private val weights = listOf(0, 5, 3)
private val scanned = weights.runningFold(0) { sum, weight -> sum + weight }.drop(1)
private val peak = scanned.last()

fun generateMindName(): String {
	val word = StringBuilder()

	val numberOfSyllables = generateNumberOfSyllables()
	repeat(numberOfSyllables) {
		val syllable = generateSyllable(word.endsWith('n'))
		word.append(syllable)
	}

	word.replace(0, 1, word.substring(0, 1).uppercase())

	return word.toString()
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