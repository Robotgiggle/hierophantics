package robotgiggle.hierophantics.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import robotgiggle.hierophantics.HierophanticsMain
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MindsDisabledMishap : Mishap() {
    override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PURPLE)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Text = error(HierophanticsMain.MOD_ID + ":minds_disabled")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}