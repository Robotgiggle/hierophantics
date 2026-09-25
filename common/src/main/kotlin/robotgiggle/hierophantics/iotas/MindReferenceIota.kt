package robotgiggle.hierophantics.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import robotgiggle.hierophantics.data.HieroServerState
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.world.entity.player.Player
import java.util.UUID

class MindReferenceIota(name: String, host: Player) : Iota(TYPE, MindReference(name, host)) {
	@JvmRecord
    data class MindReference(val name: String, val host: Player)
    
    override fun isTruthy() = true
	override fun toleratesOther(that: Iota): Boolean {
		return typesMatch(this, that) && this.name == (that as MindReferenceIota).name && this.host == that.host
	}
	val name = (payload as MindReference).name
	val host = (payload as MindReference).host

	override fun serialize(): Tag {
		val compound = CompoundTag()
		compound.putString("name", name)
		compound.putString("hostUUID", host.getUUID().toString())
		return compound
	}

	companion object {
		@JvmField
		val TYPE: IotaType<MindReferenceIota> = object : IotaType<MindReferenceIota>() {
			override fun deserialize(nbt: Tag, world: ServerLevel): MindReferenceIota? {
				val name = (nbt as CompoundTag).getString("name")
				val hostUuid = UUID.fromString(nbt.getString("hostUUID"))
				val host = world.getEntity(hostUuid)
				if (host == null || host !is Player) return null
				if (!HieroServerState.getPlayerState(host).hasMind(name)) return null
				return MindReferenceIota(name, host)
			}
			override fun display(nbt: Tag): Component {
				val name = (nbt as CompoundTag).getString("name")
				return Component.translatable("hierophantics.tooltip.mind_reference", name).withStyle(ChatFormatting.AQUA)
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