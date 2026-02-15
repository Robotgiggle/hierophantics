package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import robotgiggle.hierophantics.Hierophantics;
import robotgiggle.hierophantics.HierophanticsClient;
import robotgiggle.hierophantics.inits.HierophanticsConfig;
import robotgiggle.hierophantics.inits.HierophanticsEffects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Unique
    int hierophantics$hallucinationCooldown = 70;
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void hallucinateAudio(CallbackInfo ci) {
        AbstractClientPlayerEntity cPlayer = (AbstractClientPlayerEntity) (Object) this;
        Random rand = cPlayer.getRandom();
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
            Vec3d source = hierophantics$randomSpherePoint((rand.nextDouble() * 2.5) + 3.5, rand).add(cPlayer.getPos());
            if (Hierophantics.isAprilFools()) {
                cPlayer.clientWorld.playSound(source.x, source.y, source.z, SoundEvents.ENTITY_SALMON_FLOP, SoundCategory.PLAYERS, 0.8f, 1f, true);
            } else {
                cPlayer.clientWorld.playSound(source.x, source.y, source.z, SoundEvents.ENTITY_VILLAGER_AMBIENT, SoundCategory.PLAYERS, 0.5f, 1f, true);
            }
            hierophantics$hallucinationCooldown = config.getCooldown();
            return;
        } 
        
        // hallucinate allay nosies and amethyst chimes due to Manifold Mind
        if (rand.nextDouble() < config.getAllayRate() && cPlayer.hasStatusEffect(HierophanticsEffects.MEDIA_DISCOUNT.getValue())) {
            Vec3d source = hierophantics$randomSpherePoint((rand.nextDouble() * 2.5) + 3, rand).add(cPlayer.getPos());
            if (Hierophantics.isAprilFools()) {
                cPlayer.clientWorld.playSound(source.x, source.y, source.z, SoundEvents.ENTITY_SALMON_FLOP, SoundCategory.PLAYERS, 0.8f, 1f, true);
            } else if (rand.nextDouble() < 0.5) {
                cPlayer.clientWorld.playSound(source.x, source.y, source.z, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1f, (0.5f + rand.nextFloat() * 1.2F), true);
            } else {
                cPlayer.clientWorld.playSound(source.x, source.y, source.z, SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, SoundCategory.PLAYERS, 0.3f, 1f, true);
            }
            hierophantics$hallucinationCooldown = config.getCooldown();
        }
    }

    @Unique
    private Vec3d hierophantics$randomSpherePoint(double radius, Random rand) {
        double x = rand.nextGaussian();
        double y = rand.nextGaussian();
        double z = rand.nextGaussian();
        return new Vec3d(x, y, z).normalize().multiply(radius);
    }
}
