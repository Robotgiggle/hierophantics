package robotgiggle.hierophantics

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.PatternShapeMatch
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.mishaps.MindsDisabledMishap
import robotgiggle.hierophantics.networking.msg.MsgHallucinationTriggerS2C
import kotlin.math.ln

class HieroMindCastEnv(caster: ServerPlayerEntity, castingHand: Hand, val muted: Boolean) : PlayerBasedCastEnv(caster, castingHand) {
	var mediaConsumed: Long = 0

	override fun getCastingHand(): Hand = this.castingHand
	override fun getPigment(): FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(this.caster)
	override fun produceParticles(particles: ParticleSpray, pigment: FrozenPigment) {
		if (!muted) super.produceParticles(particles, pigment)
	}

	override fun precheckAction(match: PatternShapeMatch?) {
		if (HieroServerState.getPlayerState(caster).disabled)
			throw MindsDisabledMishap("cast")
		super.precheckAction(match)
	}

	override fun extractMediaEnvironment(costLeft: Long, simulate: Boolean): Long {
		if (caster.isCreative)
			return 0
		if (!simulate) mediaConsumed += costLeft
		return this.extractMediaFromInventory(costLeft, this.canOvercast(), simulate)
	}

	override fun postCast(image: CastingImage?) {
		super.postCast(image)

		val clamped = mediaConsumed.coerceAtMost(MediaConstants.CRYSTAL_UNIT)
		val hallucinationStrength = 1.2 * ln(1.2 + (clamped.toDouble() / MediaConstants.DUST_UNIT))
		MsgHallucinationTriggerS2C(hallucinationStrength).sendToPlayer(caster)
		mediaConsumed = 0
	}
}
