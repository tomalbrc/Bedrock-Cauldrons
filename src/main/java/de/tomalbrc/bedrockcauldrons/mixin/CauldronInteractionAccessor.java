package de.tomalbrc.bedrockcauldrons.mixin;

import net.minecraft.core.cauldron.CauldronInteraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CauldronInteraction.class)
public interface CauldronInteractionAccessor {
    @Invoker
    static CauldronInteraction.InteractionMap invokeNewInteractionMap(String string) {
        throw new AssertionError();
    }
}
