package robotgiggle.hierophantics.mixin;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.world.poi.PointOfInterestTypes;
import robotgiggle.hierophantics.Hierophantics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import java.util.Set;
import java.util.stream.Stream;

@Mixin(PointOfInterestTypes.class)
public class PointOfInterestTypesMixin {
    @ModifyExpressionValue(
        method = "<clinit>", 
        at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;")
    )
    private static Stream<BlockState> addFlayBedToValidBeds(Stream<BlockState> original) {
        Stream<BlockState> flayBedHeadStates = Hierophantics.FLAY_BED_BLOCK
                                                .getStateManager().getStates().stream()
                                                .filter((blockState) -> {
                                                    return blockState.get(BedBlock.PART) == BedPart.HEAD;
                                                });
        return Stream.concat(original, flayBedHeadStates);
    }
}
