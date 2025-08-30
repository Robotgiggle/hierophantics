package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ToolItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.client.ClientTickCounter;
import robotgiggle.hierophantics.Hierophantics;
import robotgiggle.hierophantics.HierophanticsClient;
import robotgiggle.hierophantics.inits.HierophanticsConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyVariable(method = "getModel", at = @At("HEAD"), argsOnly = true)
    private ItemStack hallucinateItem(ItemStack original, ItemStack stack, World world, LivingEntity entity, int seed) {
        // this prevents non-standard item renders (like EMI recipes) from spazzing out
        if (seed == 0) return original;
        
        int hash = original.hashCode();
        int timeScramble = (int)(ClientTickCounter.ticksInGame/(180 + (hash % 40)));
        int rng = Math.abs((hash + timeScramble * (150 + (hash % 300))) % Hierophantics.RNG_SCALE);

        var config = HierophanticsConfig.getClient().getItemHallucinations();

        // hallucinate emeralds due to embedded villagers
        double emeraldChance = Math.min(
            config.getBaseEmeraldRate() * HierophanticsClient.getClientOwnedMinds(), 
            config.getMaxEmeraldRate()
        );
        if (rng < emeraldChance * Hierophantics.RNG_SCALE) {
            if (original.getItem() instanceof BlockItem)
                return new ItemStack(Items.EMERALD_BLOCK, original.getCount());
            else
                return new ItemStack(Items.EMERALD, original.getCount());
        }

        // hallucinate media items due to embedded allays
        if (MinecraftClient.getInstance().player.hasStatusEffect(Hierophantics.MEDIA_DISCOUNT_EFFECT.get())) {
            if (rng > (1 - config.getMediaRate()) * Hierophantics.RNG_SCALE) {
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
                else if (original.getItem() instanceof ToolItem || original.getItem() instanceof ItemStaff)
                    return new ItemStack(HexItems.STAFF_QUENCHED, original.getCount());
                else
                    return new ItemStack(items.get(rng % 4), original.getCount());
            }
        }

        return original;
    }
}
