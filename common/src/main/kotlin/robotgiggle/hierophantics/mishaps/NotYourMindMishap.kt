package robotgiggle.hierophantics.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

class NotYourMindMishap : Mishap() {
    override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.BLACK)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Component = error("hierophantics:not_your_mind")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
		env.mishapEnvironment.blind(100)
	}
}