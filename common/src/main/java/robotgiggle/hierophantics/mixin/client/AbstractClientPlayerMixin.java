package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import robotgiggle.hierophantics.Hierophantics;
import robotgiggle.hierophantics.HierophanticsClient;
import robotgiggle.hierophantics.inits.HierophanticsConfig;
import robotgiggle.hierophantics.inits.HierophanticsEffects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    @Unique
    int hierophantics$hallucinationCooldown = 70;
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void hallucinateAudio(CallbackInfo ci) {
        AbstractClientPlayer cPlayer = (AbstractClientPlayer) (Object) this;
        RandomSource rand = cPlayer.getRandom();
        var config = HierophanticsConfig.getClient().getAudioHallucinations();

        if (hierophantics$hallucinationCooldown > 0) {
            hierophantics$hallucinationCooldown--;
            return;
        }

        // hallucinate villager nosies due to embedded minds
        double villagerChance = Math.min(
            config.getBaseVillagerRate() * HierophanticsClient.clientOwnedMinds * HierophanticsClient.getHallucinationScaling(),
            config.getMaxVillagerRate()
        );
        if (rand.nextDouble() < villagerChance) {
            Vec3 source = hierophantics$randomSpherePoint((rand.nextDouble() * 2.5) + 3.5, rand).add(cPlayer.position());
            if (Hierophantics.isAprilFools()) {
                cPlayer.clientLevel.playLocalSound(source.x, source.y, source.z, SoundEvents.SALMON_FLOP, SoundSource.PLAYERS, 0.8f, 1f, true);
            } else {
                cPlayer.clientLevel.playLocalSound(source.x, source.y, source.z, SoundEvents.VILLAGER_AMBIENT, SoundSource.PLAYERS, 0.5f, 1f, true);
            }
            hierophantics$hallucinationCooldown = config.getCooldown();
            return;
        } 
        
        // hallucinate allay nosies and amethyst chimes due to Manifold Mind
        if (rand.nextDouble() < config.getAllayRate() && cPlayer.hasEffect(HierophanticsEffects.MEDIA_DISCOUNT.getValue())) {
            Vec3 source = hierophantics$randomSpherePoint((rand.nextDouble() * 2.5) + 3, rand).add(cPlayer.position());
            if (Hierophantics.isAprilFools()) {
                cPlayer.clientLevel.playLocalSound(source.x, source.y, source.z, SoundEvents.SALMON_FLOP, SoundSource.PLAYERS, 0.8f, 1f, true);
            } else if (rand.nextDouble() < 0.5) {
                cPlayer.clientLevel.playLocalSound(source.x, source.y, source.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1f, (0.5f + rand.nextFloat() * 1.2F), true);
            } else {
                cPlayer.clientLevel.playLocalSound(source.x, source.y, source.z, SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.PLAYERS, 0.3f, 1f, true);
            }
            hierophantics$hallucinationCooldown = config.getCooldown();
        }
    }

    @Unique
    private Vec3 hierophantics$randomSpherePoint(double radius, RandomSource rand) {
        double x = rand.nextGaussian();
        double y = rand.nextGaussian();
        double z = rand.nextGaussian();
        return new Vec3(x, y, z).normalize().scale(radius);
    }
}
