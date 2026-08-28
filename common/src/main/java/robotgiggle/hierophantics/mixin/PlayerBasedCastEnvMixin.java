package robotgiggle.hierophantics.mixin;

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import robotgiggle.hierophantics.Hierophantics;
import robotgiggle.hierophantics.data.HieroServerState;
import robotgiggle.hierophantics.networking.msg.MsgHallucinationTriggerS2C;

@Mixin(value = PlayerBasedCastEnv.class, remap = false)
public class PlayerBasedCastEnvMixin {
    @Shadow
    private ServerPlayerEntity caster;

    @Inject(method = "extractMediaFromInventory", at = @At(value = "INVOKE", target = "trulyHurt"))
    private void triggerHallucinationsWhenOvercasting(CallbackInfoReturnable<Long> cir) {
        if (HieroServerState.getPlayerState(caster).getOwnedMinds() > 0) {
            var msg = new MsgHallucinationTriggerS2C(0.6);
            msg.sendToPlayer(caster);
        }
    }
}
