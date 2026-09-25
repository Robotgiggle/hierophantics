package robotgiggle.hierophantics.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import robotgiggle.hierophantics.data.HieroServerState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.jetbrains.annotations.Nullable;

import at.petrak.hexcasting.api.casting.iota.Vec3Iota;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "playerDestroy", at = @At("TAIL"))
    private void fireBreakTriggers(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
      if (player.level().isClientSide)
        return;
      HieroServerState.getPlayerState(player).triggerMinds((ServerPlayer) player, "break", new Vec3Iota(pos.getCenter()));
    }
}
