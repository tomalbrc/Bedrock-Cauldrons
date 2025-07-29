package de.tomalbrc.bedrockcauldrons.mixin;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DyeItem.class)
public interface DyeItemAccessor {
    @Accessor("ITEM_BY_COLOR")
    static Map<DyeColor, DyeItem> getITEM_BY_COLOR() {
        throw new AssertionError();
    }
}
