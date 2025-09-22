package robotgiggle.hierophantics.inits

import dev.architectury.event.events.client.ClientPlayerEvent
import dev.architectury.event.events.common.PlayerEvent
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.ConfigHolder
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject
import me.shedaniel.autoconfig.serializer.PartitioningSerializer
import me.shedaniel.autoconfig.serializer.PartitioningSerializer.GlobalData
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer
import net.minecraft.network.PacketByteBuf
import net.minecraft.item.Item
import vazkii.patchouli.api.PatchouliAPI
import robotgiggle.hierophantics.Hierophantics
import robotgiggle.hierophantics.networking.msg.MsgSyncConfigS2C

object HierophanticsConfig {
    @JvmStatic
    lateinit var holder: ConfigHolder<GlobalConfig>

    @JvmStatic
    val client get() = holder.config.client

    @JvmStatic
    val server get() = syncedServerConfig ?: holder.config.server

    // only used on the client, probably
    private var syncedServerConfig: ServerConfig? = null

    fun init() {
        holder = AutoConfig.register(
            GlobalConfig::class.java,
            PartitioningSerializer.wrap(::Toml4jConfigSerializer),
        )

        PlayerEvent.PLAYER_JOIN.register { player ->
            MsgSyncConfigS2C(holder.config.server).sendToPlayer(player)
            PatchouliAPI.get().setConfigFlag("hierophantics:player_sleep_spell", server.playerSleepSpell)
        }
    }

    fun initClient() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register { _ ->
            syncedServerConfig = null
        }
    }

    fun onSyncConfig(serverConfig: ServerConfig) {
        syncedServerConfig = serverConfig
    }

    @Config(name = Hierophantics.MOD_ID)
    class GlobalConfig : GlobalData() {
        @Category("client")
        @TransitiveObject
        val client = ClientConfig()

        @Category("server")
        @TransitiveObject
        val server = ServerConfig()
    }

    @Config(name = "client")
    class ClientConfig : ConfigData {
        @Tooltip
        @CollapsibleObject
        val itemHallucinations: ItemHallucinations = ItemHallucinations()
        @Tooltip
        @CollapsibleObject
        val audioHallucinations: AudioHallucinations = AudioHallucinations()

        class ItemHallucinations {
            @Tooltip
            val baseEmeraldRate: Double = 0.0015
            @Tooltip
            val maxEmeraldRate: Double = 0.1
            @Tooltip
            val mediaRate: Double = 0.1
        }

        class AudioHallucinations {
            @Tooltip
            val baseVillagerRate: Double = 0.00006
            @Tooltip
            val maxVillagerRate: Double = 0.004
            @Tooltip
            val allayRate: Double = 0.004
            @Tooltip
            val cooldown: Int = 70
        }
    }

    @Config(name = "server")
    class ServerConfig : ConfigData {
        var maxMinds: Int = 64
            private set
        var mediaDiscount: Double = 0.75
            private set
        @Tooltip
        var playerSleepSpell: Boolean = true
            private set

        fun encode(buf: PacketByteBuf) {
            buf.writeInt(maxMinds)
            buf.writeDouble(mediaDiscount)
            buf.writeBoolean(playerSleepSpell)
        }

        companion object {
            fun decode(buf: PacketByteBuf) = ServerConfig().apply {
                maxMinds = buf.readInt()
                mediaDiscount = buf.readDouble()
                playerSleepSpell = buf.readBoolean()
            }
        }
    }
}
