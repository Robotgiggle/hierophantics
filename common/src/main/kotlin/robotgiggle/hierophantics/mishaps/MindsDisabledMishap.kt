package robotgiggle.hierophantics.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

class MindsDisabledMishap(val action: String, val yourMind: Boolean = true) : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PURPLE)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Component {
		val translatedAction = Component.translatable("hierophantics.mishap.minds_disabled.action.$action")
		return error("hierophantics:minds_disabled." + if (yourMind) "yours" else "other", translatedAction)
	} 
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}