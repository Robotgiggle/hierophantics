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
import java.util.UUID

class MindReferenceIota(name: String, host: PlayerEntity) : Iota(TYPE, MindReference(name, host)) {
	@JvmRecord
    data class MindReference(val name: String, val host: PlayerEntity)
    
    override fun isTruthy() = true
	override fun toleratesOther(that: Iota): Boolean {
		return typesMatch(this, that) && this.name == (that as MindReferenceIota).name && this.host == that.host
	}
	val name = (payload as MindReference).name
	val host = (payload as MindReference).host

	override fun serialize(): NbtElement {
		val compound = NbtCompound()
		compound.putString("name", name)
		compound.putString("hostUUID", host.getUuid().toString())
		return compound
	}

	companion object {
		@JvmField
		val TYPE: IotaType<MindReferenceIota> = object : IotaType<MindReferenceIota>() {
			override fun deserialize(nbt: NbtElement, world: ServerWorld): MindReferenceIota? {
				val name = (nbt as NbtCompound).getString("name")
				val hostUuid = UUID.fromString(nbt.getString("hostUUID"))
				val host = world.getEntity(hostUuid)
				if (host == null || !(host is PlayerEntity)) return null
				if (!HieroServerState.getPlayerState(host).hasMind(name)) return null
				return MindReferenceIota(name, host)
			}
			override fun display(nbt: NbtElement): Text {
				val name = (nbt as NbtCompound).getString("name")
				return Text.translatable("hierophantics.tooltip.mind_reference", name).formatted(Formatting.AQUA)
			}
			override fun color() = 0x55ffff
		}
	}
}

fun List<Iota>.getMindReference(idx: Int, argc: Int): MindReferenceIota {
	val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
	if (x is MindReferenceIota)
		return x
	throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "mind_reference")
}