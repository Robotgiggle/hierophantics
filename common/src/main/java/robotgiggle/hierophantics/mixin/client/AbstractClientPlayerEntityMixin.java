package robotgiggle.hierophantics.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import robotgiggle.hierophantics.Hierophantics;
import robotgiggle.hierophantics.HierophanticsClient;
import robotgiggle.hierophantics.inits.HierophanticsConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Unique
    int villagerCooldown = 300;
    @Unique
    int allayCooldown = 70;
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void hallucinateAudio(CallbackInfo ci) {
        if (HierophanticsConfig.getClient().getHallucinateAudio()) {
            AbstractClientPlayerEntity cPlayer = (AbstractClientPlayerEntity) (Object) this;
            Random rand = cPlayer.getRandom();

            // hallucinate villager nosies
            if (villagerCooldown > 0) {
                villagerCooldown--;
            } else if (rand.nextDouble() < 0.00004 * HierophanticsClient.getClientOwnedMinds()) {
                Vec3d source = randomSpherePoint((rand.nextDouble() * 2.5) + 3, rand).add(cPlayer.getPos());
                cPlayer.clientWorld.playSound(source.x, source.y, source.z, SoundEvents.ENTITY_VILLAGER_AMBIENT, SoundCategory.PLAYERS, 0.5f, 1f, true);
                villagerCooldown = 300;
            }

            // hallucinate allay nosies and amethyst chimes
            if (cPlayer.hasStatusEffect(Hierophantics.MEDIA_DISCOUNT_EFFECT.get())) {
                if (allayCooldown > 0) {
                    allayCooldown--;
                } else if (rand.nextDouble() < 0.004) {
                    Vec3d source = randomSpherePoint((rand.nextDouble() * 2.5) + 3, rand).add(cPlayer.getPos());
                    if (rand.nextDouble() < 0.5) {
                        cPlayer.clientWorld.playSound(source.x, source.y, source.z, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1f, (0.5f + rand.nextFloat() * 1.2F), true);
                    } else {
                        cPlayer.clientWorld.playSound(source.x, source.y, source.z, SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, SoundCategory.PLAYERS, 0.3f, 1f, true);
                    }
                    allayCooldown = 70;
                }
            }
        }
    }

    private Vec3d randomSpherePoint(double radius, Random rand) {
        double x = rand.nextGaussian();
        double y = rand.nextGaussian();
        double z = rand.nextGaussian();
        return new Vec3d(x, y, z).normalize().multiply(radius);
    }
}
