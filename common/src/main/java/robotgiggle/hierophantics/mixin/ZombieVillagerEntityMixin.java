package robotgiggle.hierophantics.mixin;

import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.village.VillagerProfession;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ZombieVillagerEntity.class)
public class ZombieVillagerEntityMixin {
    // NOTE: the profession checking is done via the ID string rather than directly comparing 
    //       to the registry entry because the quiltmind registry entry is a different object
    //       on fabric vs forge, and i'd rather not make two otherwise identical mixins

    @Inject(method = "<init>", at = @At("RETURN"))
    private void noNaturalZombieQuiltminds(CallbackInfo ci) {
        ZombieVillagerEntity entity = (ZombieVillagerEntity) (Object) this;
        if (entity.getVillagerData().getProfession().id().equals("quiltmind")) {
            Optional<Reference<VillagerProfession>> newProfOpt;
            do {
                newProfOpt = Registries.VILLAGER_PROFESSION.getRandom(entity.getRandom());
            } while (isQuiltmind(newProfOpt));
            newProfOpt.ifPresentOrElse(
                newProf -> entity.setVillagerData(entity.getVillagerData().withProfession(newProf.value())),
                () -> entity.setVillagerData(entity.getVillagerData().withProfession(VillagerProfession.NONE))
            );
        }
    }

    private boolean isQuiltmind(Optional<Reference<VillagerProfession>> optionalProf) {
        if (optionalProf.isPresent()) {
            return optionalProf.get().value().id().equals("quiltmind");
        } else {
            return false;
        }
    }
}