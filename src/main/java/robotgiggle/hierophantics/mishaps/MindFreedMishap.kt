package robotgiggle.hierophantics.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import robotgiggle.hierophantics.HierophanticsMain
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MindFreedMishap : Mishap() {
    override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PURPLE)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Text = error("hierophantics:mind_freed")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}