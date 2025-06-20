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
import java.util.UUID

class MindReferenceIota(mindId: Int, host: PlayerEntity) : Iota(TYPE, MindReference(mindId, host)) {
	@JvmRecord
    data class MindReference(val mindId: Int, val host: PlayerEntity)
    
    override fun isTruthy() = true
	override fun toleratesOther(that: Iota): Boolean {
		return typesMatch(this, that) && this.mindId == (that as MindReferenceIota).mindId && this.host == that.host
	}
	val mindId = (payload as MindReference).mindId
	val host = (payload as MindReference).host

	override fun serialize(): NbtElement {
		val compound = NbtCompound()
		compound.putInt("mindID", mindId)
		compound.putString("hostName", host.getName().getString())
		compound.putString("hostUUID", host.getUuid().toString())
		return compound
	}

	companion object {
		@JvmField
		val TYPE: IotaType<MindReferenceIota> = object : IotaType<MindReferenceIota>() {
			override fun deserialize(nbt: NbtElement, world: ServerWorld): MindReferenceIota? {
				val mindId = (nbt as NbtCompound).getInt("mindID")
				val hostUuid = UUID.fromString(nbt.getString("hostUUID"))
				val host = world.getEntity(hostUuid)
				if (host == null || !(host is PlayerEntity)) return null
				if (!HierophanticsAPI.getPlayerState(host).hasMind(mindId)) return null
				return MindReferenceIota(mindId, host)
			}
			override fun display(nbt: NbtElement): Text {
				// TODO: generate a custom name instead of just using the data
				val id = (nbt as NbtCompound).getInt("mindID")
				val name = nbt.getString("hostName")
				return Text.literal("Embedded Mind ("+name+", #"+id+")").formatted(Formatting.AQUA)
			}
			override fun color() = 0x55ffff
		}
	}
}

fun List<Iota>.getMindReference(idx: Int, argc: Int): MindReferenceIota {
	val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
	if (x is MindReferenceIota)
		return x
	throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "mindReference")
}