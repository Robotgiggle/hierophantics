package robotgiggle.hierophantics.mixin;

import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.entity.npc.VillagerProfession;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ZombieVillager.class)
public class ZombieVillagerMixin {
    // NOTE: the profession checking is done via the ID string rather than directly comparing 
    //       to the registry entry because the quiltmind registry entry is a different object
    //       on fabric vs forge, and i'd rather not make two otherwise identical mixins

    @Inject(method = "<init>", at = @At("RETURN"))
    private void noNaturalZombieQuiltminds(CallbackInfo ci) {
        ZombieVillager entity = (ZombieVillager) (Object) this;
        if (entity.getVillagerData().getProfession().name().equals("quiltmind")) {
            Optional<Reference<VillagerProfession>> newProfOpt;
            do {
                newProfOpt = BuiltInRegistries.VILLAGER_PROFESSION.getRandom(entity.getRandom());
            } while (hierophantics$isQuiltmind(newProfOpt));
            newProfOpt.ifPresentOrElse(
                newProf -> entity.setVillagerData(entity.getVillagerData().setProfession(newProf.value())),
                () -> entity.setVillagerData(entity.getVillagerData().setProfession(VillagerProfession.NONE))
            );
        }
    }

    @Unique
    private boolean hierophantics$isQuiltmind(Optional<Reference<VillagerProfession>> optionalProf) {
        if (optionalProf.isPresent()) {
            return optionalProf.get().value().name().equals("quiltmind");
        } else {
            return false;
        }
    }
}