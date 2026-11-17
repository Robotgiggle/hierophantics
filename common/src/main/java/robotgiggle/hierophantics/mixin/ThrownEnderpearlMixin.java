package robotgiggle.hierophantics.mixin;

import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import robotgiggle.hierophantics.data.HieroServerState;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {
    // if you throw an enderpearl while mounted, rather than calling the normal teleport method, it instead directly
    // calls dismountTo with the destination as the dismount spot. so we need a whole other mixin. thanks mojang.
    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;dismountTo(DDD)V", shift = At.Shift.AFTER))
    private void fireTeleportTriggers(CallbackInfo ci) {
        ThrownEnderpearl pearl = (ThrownEnderpearl) (Object) this;
        if (pearl.getOwner() instanceof ServerPlayer player) {
            var state = HieroServerState.getPlayerState(player);
            if (state.getOwnedMinds() > 0) {
                var offsetVec = player.position().subtract(state.getPrevPos());
                state.setVelSkipTimer(4);
                state.setPrevPos(player.position());
                state.triggerMinds(player, "teleport", new Vec3Iota(offsetVec));
            }
        }
    }
}
