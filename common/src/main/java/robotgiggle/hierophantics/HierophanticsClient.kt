package robotgiggle.hierophantics

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack
import net.minecraft.item.Items;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ToolItem;
import at.petrak.hexcasting.client.ClientTickCounter;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.items.ItemStaff;
import robotgiggle.hierophantics.inits.HierophanticsEffects
import robotgiggle.hierophantics.inits.HierophanticsConfig
import robotgiggle.hierophantics.inits.HierophanticsConfig.GlobalConfig
import me.shedaniel.autoconfig.AutoConfig

object HierophanticsClient {
    @JvmStatic
    var clientOwnedMinds = 0;
    
    fun init() {
        HierophanticsConfig.initClient()
    }

    fun getConfigScreen(parent: Screen): Screen {
        return AutoConfig.getConfigScreen(GlobalConfig::class.java, parent).get()
    }

    @JvmStatic
    fun hallucinateItem(original: ItemStack): ItemStack {
        val config = HierophanticsConfig.client.itemHallucinations;

        val hash = original.hashCode();
        val timeScramble = (ClientTickCounter.ticksInGame/(180 + (hash % 40))).toInt();
        val rng = Math.abs((hash + timeScramble * (150 + (hash % 300))) % 10000);

        // hallucinate emeralds due to embedded minds
        val emeraldChance = Math.min(config.baseEmeraldRate * clientOwnedMinds, config.maxEmeraldRate);
        if (rng < emeraldChance * 10000) {
            if (original.getItem() is BlockItem)
                return ItemStack(Items.EMERALD_BLOCK, original.getCount());
            else
                return ItemStack(Items.EMERALD, original.getCount());
        }

        // hallucinate media items due to Manifold Mind
        if (MinecraftClient.getInstance().player!!.hasStatusEffect((HierophanticsEffects.MEDIA_DISCOUNT.value)) ) {
            if (rng > (1 - config.mediaRate) * 10000) {
                var items = listOf(
                    HexItems.AMETHYST_DUST, Items.AMETHYST_SHARD,
                    HexItems.CHARGED_AMETHYST, HexItems.QUENCHED_SHARD
                );
                var blocks = listOf(
                    Items.AMETHYST_BLOCK, Items.BUDDING_AMETHYST,
                    HexBlocks.QUENCHED_ALLAY.asItem()
                );
                if (original.getItem() is BlockItem)
                    return ItemStack(blocks.get(rng % 3), original.getCount());
                else if (original.getItem() is ToolItem || original.getItem() is ItemStaff)
                    return ItemStack(HexItems.STAFF_QUENCHED, original.getCount());
                else
                    return ItemStack(items.get(rng % 4), original.getCount());
            }
        }

        return original;
    }
}
