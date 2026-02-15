package robotgiggle.hierophantics.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexDamageTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.entity.mob.MobEntity
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class NitwitImbuementMishap(val mob: MobEntity) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.GREEN)
    override fun particleSpray(ctx: CastingEnvironment): ParticleSpray {
        return ParticleSpray.burst(mob.eyePos, 1.0)
    }
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) = error("hierophantics:nitwit_imbuement", mob.displayName);
    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        trulyHurt(mob, mob.getDamageSources().create(HexDamageTypes.OVERCAST, ctx.castingEntity), 1f)
    }
}