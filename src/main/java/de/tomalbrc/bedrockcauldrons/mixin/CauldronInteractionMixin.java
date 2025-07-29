package de.tomalbrc.bedrockcauldrons.mixin;

import de.tomalbrc.bedrockcauldrons.impl.CustomCauldronInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CauldronInteraction.class)
public interface CauldronInteractionMixin {
    @Inject(method = "method_32222", at = @At("RETURN"), cancellable = true)
    private static void bc$insertPotion(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() == InteractionResult.TRY_WITH_EMPTY_HAND) {
            var res = CustomCauldronInteractions.POTION_BOTTLE.interact(blockState, level, blockPos, player, interactionHand, itemStack);
            if (res == InteractionResult.SUCCESS)
                cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
