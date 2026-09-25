package robotgiggle.hierophantics.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexDamageTypes
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.DyeColor

class NitwitImbuementMishap(val mob: Mob) : Mishap() {
    override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.GREEN)
    override fun particleSpray(env: CastingEnvironment): ParticleSpray {
        return ParticleSpray.burst(mob.eyePosition, 1.0)
    }
    override fun errorMessage(env: CastingEnvironment, errorCtx: Context) = error("hierophantics:nitwit_imbuement", mob.displayName)
    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        trulyHurt(mob, mob.damageSources().source(HexDamageTypes.OVERCAST, env.castingEntity), 1f)
    }
}