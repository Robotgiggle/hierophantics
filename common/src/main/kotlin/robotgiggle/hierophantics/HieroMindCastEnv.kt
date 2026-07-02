package robotgiggle.hierophantics

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.PatternShapeMatch
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.mishaps.MindsDisabledMishap

class HieroMindCastEnv(caster: ServerPlayerEntity, castingHand: Hand, val muted: Boolean) : PlayerBasedCastEnv(caster, castingHand) {
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

	public override fun extractMediaEnvironment(costLeft: Long, simulate: Boolean): Long {
		if (caster.isCreative)
			return 0
		return this.extractMediaFromInventory(costLeft, this.canOvercast(), simulate)
	}
}
