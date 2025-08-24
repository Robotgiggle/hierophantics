package robotgiggle.hierophantics.mixin;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ToolItem;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.client.ClientTickCounter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Random;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    final int RNG_SCALE = 10000;
    final Random RAND = new Random();
    
    @ModifyVariable(method = "getModel", at = @At("HEAD"), argsOnly = true)
    private ItemStack hallucinateItem(ItemStack original) {
        // TODO: get these dynamically
        // emeraldChance comes from number of embedded minds
        // mediaChance comes from level of Mental Convolution
        float emeraldChance = 0.01f;
        float mediaChance = 0.1f;
        
        int hash = original.hashCode();
        int timeScramble = (int)(ClientTickCounter.ticksInGame/(180 + (hash % 40)));
        int rng = (hash + timeScramble * (250 + (hash % 500))) % RNG_SCALE;

        // hallucinate emeralds due to embedded villagers
        if (rng < emeraldChance * RNG_SCALE) {
            if (original.getItem() instanceof BlockItem)
                return new ItemStack(Items.EMERALD_BLOCK, original.getCount());
            else
                return new ItemStack(Items.EMERALD, original.getCount());
        }

        // hallucinate media items due to embedded allays
        if (rng > (1 - mediaChance) * RNG_SCALE) {
            var items = List.of(
                HexItems.AMETHYST_DUST, Items.AMETHYST_SHARD,
                HexItems.CHARGED_AMETHYST, HexItems.QUENCHED_SHARD
            );
            var blocks = List.of(
                Items.AMETHYST_BLOCK, Items.BUDDING_AMETHYST,
                HexBlocks.QUENCHED_ALLAY.asItem()
            );
            if (original.getItem() instanceof BlockItem)
                return new ItemStack(blocks.get(rng % 3), original.getCount());
            else if (original.getItem() instanceof ToolItem)
                return new ItemStack(HexItems.STAFF_QUENCHED, original.getCount());
            else
                return new ItemStack(items.get(rng % 4), original.getCount());
        }

        return original;
    }
}
