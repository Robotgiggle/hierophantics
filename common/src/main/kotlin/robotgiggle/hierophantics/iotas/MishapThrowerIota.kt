package robotgiggle.hierophantics.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.mishaps.MindsDisabledMishap
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class MishapThrowerIota() : Iota(TYPE, 0) {
    override fun isTruthy() = false
    override fun toleratesOther(that: Iota) = false
    override fun serialize(): NbtElement = NbtCompound()

    override fun execute(vm: CastingVM, world: ServerWorld, continuation: SpellContinuation): CastResult {
		return CastResult(
			this,
			continuation,
			null,
			listOf(OperatorSideEffect.DoMishap(MindsDisabledMishap("cast"), Mishap.Context(null, null))),
			ResolvedPatternType.ERRORED,
			HexEvalSounds.MISHAP
		)
    }

    companion object {
        @JvmField
		val TYPE: IotaType<MishapThrowerIota> = object : IotaType<MishapThrowerIota>() {
			override fun deserialize(nbt: NbtElement, world: ServerWorld): MishapThrowerIota? {
				return MishapThrowerIota()
			}
			override fun display(nbt: NbtElement): Text {
				return Text.translatable("hierophantics.tooltip.mishap_thrower").formatted(Formatting.DARK_RED)
			}
			override fun color() = 0xaa0000
		}
    }
}