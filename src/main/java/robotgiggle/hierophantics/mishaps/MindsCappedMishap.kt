package robotgiggle.hierophantics.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import robotgiggle.hierophantics.HierophanticsMain
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.entity.player.PlayerEntity

class MindsCappedMishap(val subject: PlayerEntity) : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PURPLE)
	override fun particleSpray(ctx: CastingEnvironment): ParticleSpray {
        return ParticleSpray.burst(subject.eyePos, 1.0)
    }
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Text = error("hierophantics:minds_capped", subject.getName())
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}