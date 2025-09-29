package robotgiggle.hierophantics.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import robotgiggle.hierophantics.data.HieroServerState
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.damage.DamageType

class TriggerIota(trigger: Trigger) : Iota(TYPE, trigger) {
	@JvmRecord
    data class Trigger(val type: String, val threshold: Double, val dmgType: String, val inverted: Boolean) {
		fun serialize(): NbtCompound {
			val compound = NbtCompound()
			compound.putString("trigger", type)
			compound.putDouble("threshold", threshold)
			compound.putString("dmgType", dmgType)
			compound.putBoolean("inverted", inverted)
			return compound
		}
		fun passedThreshold(currVal: Double, prevVal: Double): Boolean {
			val upward = if (type == "velocity" || type == "fall") !inverted else inverted
			if (upward) return currVal > threshold && prevVal <= threshold
			else return currVal < threshold && prevVal >= threshold
		}
		companion object {
			fun deserialize(nbt: NbtElement): Trigger {
				val type = (nbt as NbtCompound).getString("trigger")
				val threshold = nbt.getDouble("threshold")
				val dmgType = nbt.getString("dmgType")
				val inverted = nbt.getBoolean("inverted")
				return Trigger(type, threshold, dmgType, inverted)
			}
			fun none() = Trigger("none", -1.0, "", false)
		}
	}

	constructor(trigger: String, threshold: Double = -1.0, dmgType: String = "", inverted: Boolean = false) : this(Trigger(trigger, threshold, dmgType, inverted))
    
    override fun isTruthy() = true
	override fun toleratesOther(that: Iota): Boolean {
		return typesMatch(this, that) && this.trigger == (that as TriggerIota).trigger
	}

	val trigger = payload as Trigger
	val type = trigger.type
	val threshold = trigger.threshold
	val dmgType = trigger.dmgType
	val inverted = trigger.inverted

	override fun serialize(): NbtElement {
		return trigger.serialize()
	}

	companion object {
		@JvmField
		val TYPE: IotaType<TriggerIota> = object : IotaType<TriggerIota>() {
			override fun deserialize(nbt: NbtElement, world: ServerWorld): TriggerIota? {
				return TriggerIota(Trigger.deserialize(nbt))
			}
			override fun display(nbt: NbtElement): Text {
				var typeDisplay: Text
				val type = (nbt as NbtCompound).getString("trigger")
				val dmgType = nbt.getString("dmgType")
				val threshold = nbt.getDouble("threshold")
				val inverted = nbt.getBoolean("inverted")
				if (!dmgType.equals("")) {
					typeDisplay = Text.translatable("hierophantics.tooltip.trigger_type.damage_typed", dmgType)
				} else if (threshold != -1.0) {
					val dir = if (inverted.xor(type == "velocity" || type == "fall")) 
						Text.translatable("hierophantics.tooltip.threshold_upward") 
					else 
						Text.translatable("hierophantics.tooltip.threshold_downward")
					typeDisplay = Text.translatable("hierophantics.tooltip.trigger_type." + type, dir, threshold)
				} else {
					typeDisplay = Text.translatable("hierophantics.tooltip.trigger_type." + type)
				}
				return Text.translatable("hierophantics.tooltip.trigger", typeDisplay).formatted(Formatting.YELLOW)
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