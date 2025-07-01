package robotgiggle.hierophantics.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import robotgiggle.hierophantics.HierophanticsAPI
import robotgiggle.hierophantics.data.updateTriggerNbt
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.damage.DamageType

class TriggerIota(trigger: String, threshold: Double = -1.0, dmgType: String = "") : Iota(TYPE, Trigger(trigger, threshold, dmgType)) {
	@JvmRecord
    data class Trigger(val trigger: String, val threshold: Double, val dmgType: String)
    
    override fun isTruthy() = true
	override fun toleratesOther(that: Iota): Boolean {
		return typesMatch(this, that) && this.trigger == (that as TriggerIota).trigger && this.threshold == that.threshold && this.dmgType == that.dmgType
	}
	val trigger = (payload as Trigger).trigger
	val threshold = (payload as Trigger).threshold
	val dmgType = (payload as Trigger).dmgType

	override fun serialize(): NbtElement {
		val compound = NbtCompound()
		compound.putString("trigger", trigger)
		compound.putDouble("threshold", threshold)
		compound.putString("dmgType", dmgType)
		return compound
	}

	companion object {
		@JvmField
		val TYPE: IotaType<TriggerIota> = object : IotaType<TriggerIota>() {
			override fun deserialize(nbt: NbtElement, world: ServerWorld): TriggerIota? {
				updateTriggerNbt(nbt as NbtCompound)
				val trigger = nbt.getString("trigger")
				val threshold = nbt.getDouble("threshold")
				val dmgType = nbt.getString("dmgType")
				return TriggerIota(trigger, threshold, dmgType)
			}
			override fun display(nbt: NbtElement): Text {
				var type: Text
				val dmgType = (nbt as NbtCompound).getString("dmgType")
				val threshold = nbt.getDouble("threshold")
				if (!dmgType.equals("")) {
					type = Text.translatable("hierophantics.tooltip.trigger_type.damage_typed", dmgType)
				} else if (threshold != -1.0) {
					type = Text.translatable("hierophantics.tooltip.trigger_type." + nbt.getString("trigger"), nbt.getDouble("threshold"))
				} else {
					type = Text.translatable("hierophantics.tooltip.trigger_type." + nbt.getString("trigger"))
				}
				return Text.translatable("hierophantics.tooltip.trigger", type).formatted(Formatting.YELLOW)
			}
			override fun color() = 0xffff55
		}
	}
}

fun List<Iota>.getTrigger(idx: Int, argc: Int): TriggerIota {
	val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
	if (x is TriggerIota)
		return x
	throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "trigger")
}