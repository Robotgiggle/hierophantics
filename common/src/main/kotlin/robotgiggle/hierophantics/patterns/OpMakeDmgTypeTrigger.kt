package robotgiggle.hierophantics.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.iotas.TriggerIota
import net.minecraft.server.network.ServerPlayerEntity

object OpMakeDmgTypeTrigger : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
        if (caster != null && caster is ServerPlayerEntity) {
            val lastDmgType = HieroServerState.getPlayerState(caster).lastDmgType
            if (lastDmgType != "") 
                return listOf(TriggerIota("damage_typed", dmgType=lastDmgType))
        }
        return listOf(TriggerIota("damage"))
	}
}