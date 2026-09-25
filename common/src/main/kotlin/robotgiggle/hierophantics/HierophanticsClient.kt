package robotgiggle.hierophantics

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.TieredItem
import at.petrak.hexcasting.client.ClientTickCounter
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.common.items.ItemStaff
import robotgiggle.hierophantics.inits.HierophanticsEffects
import robotgiggle.hierophantics.inits.HierophanticsConfig
import robotgiggle.hierophantics.inits.HierophanticsConfig.GlobalConfig
import me.shedaniel.autoconfig.AutoConfig
import kotlin.math.abs

object HierophanticsClient {
    @JvmField
    var clientOwnedMinds = 0

    var scalingTimestamp = 0L

    // item categories for hallucinations
    val fish = listOf(
        Items.COD, Items.SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH
    )
    val mediaItems = listOf(
        HexItems.AMETHYST_DUST, Items.AMETHYST_SHARD,
        HexItems.CHARGED_AMETHYST, HexItems.QUENCHED_SHARD
    )
    val mediaBlocks = listOf(
        Items.AMETHYST_BLOCK, Items.BUDDING_AMETHYST,
        HexBlocks.QUENCHED_ALLAY.asItem()
    )
    
    fun init() {
        HierophanticsConfig.initClient()
    }

    fun getConfigScreen(parent: Screen): Screen {
        return AutoConfig.getConfigScreen(GlobalConfig::class.java, parent).get()
    }

    @JvmStatic
    fun setHallucinationScaling(strength: Double) {
        scalingTimestamp = ClientTickCounter.ticksInGame + (2000*strength).toInt()
    }

    @JvmStatic
    fun getHallucinationScaling(): Double {
        // when the scaling timestamp is in the future, hallucination rate is increased
        // as the current time approaches the scaling timestamp, the rate scales back down
        return ((scalingTimestamp - ClientTickCounter.ticksInGame) / 2000.0).coerceAtLeast(0.3)
    }

    @JvmStatic
    fun hallucinateItem(original: ItemStack): ItemStack {
        val config = HierophanticsConfig.client.itemHallucinations

        // hallucination rng is based on the item's hashcode and the current game time
        val hash = original.hashCode()
        val timeScramble = (ClientTickCounter.ticksInGame/(180 + (hash % 40))).toInt()
        val rng = abs((hash + timeScramble * (150 + (hash % 300))) % 10000)

        // hallucinate emeralds due to embedded minds
        val emeraldChance = (config.baseEmeraldRate * clientOwnedMinds * getHallucinationScaling()).coerceAtMost(config.maxEmeraldRate)
        if (rng < emeraldChance * 10000) {
            if (Hierophantics.isAprilFools())
                return ItemStack(fish[rng % 4], original.count)
            else if (original.item is BlockItem)
                return ItemStack(Items.EMERALD_BLOCK, original.count)
            else
                return ItemStack(Items.EMERALD, original.count)
        }

        // hallucinate media items due to Manifold Mind
        if (Minecraft.getInstance().player!!.hasEffect((HierophanticsEffects.MEDIA_DISCOUNT.value)) ) {
            if (rng > (1 - config.mediaRate) * 10000) {
                if (Hierophantics.isAprilFools())
                    return ItemStack(fish[rng % 4], original.count)
                else if (original.item is BlockItem)
                    return ItemStack(mediaBlocks[rng % 3], original.count)
                else if (original.item is TieredItem || original.item is ItemStaff)
                    return ItemStack(HexItems.STAFF_QUENCHED, original.count)
                else
                    return ItemStack(mediaItems[rng % 4], original.count)
            }
        }

        return original
    }
}
