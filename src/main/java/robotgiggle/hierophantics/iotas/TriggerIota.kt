package robotgiggle.hierophantics.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import robotgiggle.hierophantics.HierophanticsAPI
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.damage.DamageType

class TriggerIota(triggerId: Int, threshold: Double = -1.0, dmgType: String = "") : Iota(TYPE, Trigger(triggerId, threshold, dmgType)) {
	@JvmRecord
    data class Trigger(val triggerId: Int, val threshold: Double, val dmgType: String)
    
    override fun isTruthy() = true
	override fun toleratesOther(that: Iota): Boolean {
		return typesMatch(this, that) && this.triggerId == (that as TriggerIota).triggerId && this.threshold == that.threshold && this.dmgType == that.dmgType
	}
	val triggerId = (payload as Trigger).triggerId
	val threshold = (payload as Trigger).threshold
	val dmgType = (payload as Trigger).dmgType

	override fun serialize(): NbtElement {
		val compound = NbtCompound()
		compound.putInt("triggerId", triggerId)
		compound.putDouble("threshold", threshold)
		compound.putString("dmgType", dmgType)
		return compound
	}

	companion object {
		@JvmField
		val TYPE: IotaType<TriggerIota> = object : IotaType<TriggerIota>() {
			override fun deserialize(nbt: NbtElement, world: ServerWorld): TriggerIota? {
				val triggerId = (nbt as NbtCompound).getInt("triggerId")
				val threshold = nbt.getDouble("threshold")
				val dmgType = nbt.getString("dmgType")
				return TriggerIota(triggerId, threshold, dmgType)
			}
			override fun display(nbt: NbtElement): Text {
				val label = when((nbt as NbtCompound).getInt("triggerId")) {
					0 -> "When Damaged"
					1 -> "When Damaged By " + nbt.getString("dmgType")
					2 -> "When Health Drops Below " + nbt.getDouble("threshold")
					3 -> "When Breath Drops Below " + nbt.getDouble("threshold")
					4 -> "When Hunger Drops Below " + nbt.getDouble("threshold")
					5 -> "When Velocity Greater Than " + nbt.getDouble("threshold")
					6 -> "When Falling For " + nbt.getDouble("threshold") + " Seconds"
					7 -> "When Item Dropped"
					8 -> "When Entity Struck"
					9 -> "When Block Broken"
					10 -> "When Jumping"
					11 -> "When Teleporting"
					else -> "UNKNOWN TRIGGER"
				}
				return Text.literal(label).formatted(Formatting.GREEN)
			}
			override fun color() = 0x55ff55
		}
	}
}

fun List<Iota>.getTrigger(idx: Int, argc: Int): TriggerIota {
	val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
	if (x is TriggerIota)
		return x
	throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "Trigger")
}