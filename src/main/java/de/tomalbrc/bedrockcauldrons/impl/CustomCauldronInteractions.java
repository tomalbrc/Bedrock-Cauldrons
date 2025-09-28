package de.tomalbrc.bedrockcauldrons.impl;

import de.tomalbrc.bedrockcauldrons.ModConfig;
import de.tomalbrc.bedrockcauldrons.impl.block.PolymerCauldron;
import de.tomalbrc.bedrockcauldrons.impl.block.entity.DyeCauldronBlockEntity;
import de.tomalbrc.bedrockcauldrons.impl.block.entity.PotionCauldronBlockEntity;
import de.tomalbrc.bedrockcauldrons.mixin.CauldronInteractionAccessor;
import de.tomalbrc.bedrockcauldrons.mixin.DyeItemAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class CustomCauldronInteractions {
    public static final CauldronInteraction.InteractionMap POTION = CauldronInteractionAccessor.invokeNewInteractionMap("potion");
    public static final CauldronInteraction.InteractionMap DYE = CauldronInteractionAccessor.invokeNewInteractionMap("dye");

    public static void init() {
        initPotion();
        ServerLifecycleEvents.SERVER_STARTED.register(x -> initDye());

        var map = CauldronInteraction.WATER.map();
        var map2 = DYE.map();

        var list = DyeItemAccessor.getITEM_BY_COLOR().values();
        for (Item item : list) {
            map.put(item, DYE_WATER);
            map2.put(item, DYE_WATER);
        }
    }

    private static void initPotion() {
        var map = POTION.map();

        if (ModConfig.getInstance().potions) {
            map.put(Items.POTION, POTION_BOTTLE);
            map.put(Items.LINGERING_POTION, POTION_BOTTLE);
            map.put(Items.SPLASH_POTION, POTION_BOTTLE);

            map.put(Items.GLASS_BOTTLE, ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
                if (!level.isClientSide && level.getBlockEntity(blockPos) instanceof PotionCauldronBlockEntity cauldronBlock) {
                    Item item = itemStack.getItem();
                    player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, PotionContents.createItemStack(Items.POTION, cauldronBlock.getPotion())));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(item));
                    lowerFillLevel(blockState, level, blockPos);
                    level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
                }

                return InteractionResult.SUCCESS;
            }));
        }

        if (ModConfig.getInstance().tippedArrows) map.put(Items.ARROW, ((blockState, level, blockPos, player, interactionHand, itemStack) -> {
            if (!level.isClientSide && level.getBlockEntity(blockPos) instanceof PotionCauldronBlockEntity cauldronBlock) {
                Item item = itemStack.getItem();
                int count = itemStack.getCount();
                int levels = blockState.getValue(PolymerCauldron.LEVEL);
                int canConvert = levels * 16;

                count = Math.min(canConvert, count);

                int useLevels = (count + 15) / 16;
                ItemStack result = PotionContents.createItemStack(
                        Items.TIPPED_ARROW, cauldronBlock.getPotion());
                result.setCount(count);

                if (count > 1) itemStack.consume(count-1, player);

                player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, result));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                lowerFillLevel(blockState, level, blockPos, useLevels);

                level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
            }

            return InteractionResult.SUCCESS;
        }));
    }

    private static void initDye() {
        var map = DYE.map();
        for (Holder<Item> itemHolder : BuiltInRegistries.ITEM.get(ItemTags.DYEABLE).orElseThrow()) {
            map.put(itemHolder.value(), DYE_LEATHER);
        }
    }

    public static void lowerFillLevel(BlockState blockState, Level level, BlockPos blockPos) {
        lowerFillLevel(blockState, level, blockPos, 1);
    }

    public static void lowerFillLevel(BlockState blockState, Level level, BlockPos blockPos, int lowerBy) {
        int i = blockState.getValue(PolymerCauldron.LEVEL) - lowerBy;
        BlockState blockState2 = i <= 0 ? Blocks.CAULDRON.defaultBlockState() : blockState.setValue(PolymerCauldron.LEVEL, i);
        level.setBlockAndUpdate(blockPos, blockState2);
        level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(blockState2));
    }

    public static int mixColors(int baseColor, List<Integer> dyes) {
        int cred = 0;
        int cgreen = 0;
        int cblue = 0;
        int l = 0;
        int count = 0;

        {
            int red = ARGB.red(baseColor);
            int green = ARGB.green(baseColor);
            int blue = ARGB.blue(baseColor);
            l += Math.max(red, Math.max(green, blue));
            cred += red;
            cgreen += green;
            cblue += blue;
            ++count;
        }

        for (int color : dyes) {
            int red = ARGB.red(color);
            int green = ARGB.green(color);
            int blue = ARGB.blue(color);
            l += Math.max(red, Math.max(green, blue));
            cred += red;
            cgreen += green;
            cblue += blue;
            ++count;
        }

        if (count == 0) {
            return baseColor;
        }

        int newR = cred / count;
        int newG = cgreen / count;
        int newB = cblue / count;
        float f = (float) l / count;
        float g = (float) Math.max(newR, Math.max(newG, newB));

        if (g > 0.0F) {
            newR = (int) (newR * f / g);
            newG = (int) (newG * f / g);
            newB = (int) (newB * f / g);
        }

        return ARGB.color(0, newR, newG, newB);
    }

    public static ItemStack applyDyes(ItemStack itemStack, List<Integer> list) {
        if (!itemStack.is(ItemTags.DYEABLE)) {
            return ItemStack.EMPTY;
        } else {
            ItemStack copied = itemStack.copyWithCount(1);
            int cred = 0;
            int cgreen = 0;
            int cblue = 0;
            int l = 0;
            int count = 0;
            DyedItemColor dyedItemColor = copied.get(DataComponents.DYED_COLOR);
            if (dyedItemColor != null) {
                int red = ARGB.red(dyedItemColor.rgb());
                int green = ARGB.green(dyedItemColor.rgb());
                int blue = ARGB.blue(dyedItemColor.rgb());
                l += Math.max(red, Math.max(green, blue));
                cred += red;
                cgreen += green;
                cblue += blue;
                ++count;
            }

            for (Integer color : list) {
                int q = ARGB.red(color);
                int r = ARGB.green(color);
                int s = ARGB.blue(color);
                l += Math.max(q, Math.max(r, s));
                cred += q;
                cgreen += r;
                cblue += s;
                ++count;
            }

            int newR = cred / count;
            int newG = cgreen / count;
            int newB = cblue / count;
            float f = (float) l / (float) count;
            float g = (float) Math.max(newR, Math.max(newG, newB));
            newR = (int) ((float) newR * f / g);
            newG = (int) ((float) newG * f / g);
            newB = (int) ((float) newB * f / g);
            int s = ARGB.color(0, newR, newG, newB);
            copied.set(DataComponents.DYED_COLOR, new DyedItemColor(s));
            return copied;
        }
    }

    public static CauldronInteraction DYE_LEATHER = (blockState, level, blockPos, player, interactionHand, itemStack) -> {
        if (!level.isClientSide && level.getBlockEntity(blockPos) instanceof DyeCauldronBlockEntity cauldronBlock) {
            ItemStack result = applyDyes(itemStack, List.of(cauldronBlock.getColor()));
            player.setItemInHand(interactionHand, result);
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            lowerFillLevel(blockState, level, blockPos);

            level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
        }

        return InteractionResult.SUCCESS;
    };


    public static CauldronInteraction POTION_BOTTLE = (blockState, level, blockPos, player, interactionHand, itemStack) -> {
        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
        if (potionContents != null && !potionContents.is(Potions.WATER) && !potionContents.is(Potions.MUNDANE) && !potionContents.is(Potions.AWKWARD) && !potionContents.is(Potions.THICK)) {
            if (!level.isClientSide) {
                if (blockState.is(ModBlocks.POTION_CAULDON) && level.getBlockEntity(blockPos) instanceof PotionCauldronBlockEntity cauldronBlock && !cauldronBlock.getPotion().is(potionContents.potion().get())) {
                    return InteractionResult.PASS;
                }

                Item item = itemStack.getItem();
                player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));

                int cauldronLevel;
                if (blockState.is(Blocks.CAULDRON)) {
                    cauldronLevel = 1;
                } else {
                    cauldronLevel = Math.min(3, blockState.getValue(PolymerCauldron.LEVEL) + 1);
                }
                level.setBlockAndUpdate(blockPos, ModBlocks.POTION_CAULDON.defaultBlockState().setValue(PolymerCauldron.LEVEL, cauldronLevel));
                var blockEntity = level.getBlockEntity(blockPos);
                if (blockEntity instanceof PotionCauldronBlockEntity cauldronBlock) {
                    cauldronBlock.setPotion(potionContents);
                }

                level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    };

    public static CauldronInteraction DYE_WATER = (blockState, level, blockPos, player, interactionHand, itemStack) -> {
        if (!level.isClientSide && itemStack.getItem() instanceof DyeItem dyeItem) {
            Item item = itemStack.getItem();
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));

            int cauldronLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
            level.setBlockAndUpdate(blockPos, ModBlocks.DYE_CAULDON.defaultBlockState().setValue(PolymerCauldron.LEVEL, cauldronLevel));
            var blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof DyeCauldronBlockEntity cauldronBlock) {
                var c = cauldronBlock.getColor();
                if (c != null) {
                    c = mixColors(c, List.of(dyeItem.getDyeColor().getTextureDiffuseColor()));
                } else {
                    c = dyeItem.getDyeColor().getTextureDiffuseColor();
                }
                cauldronBlock.setColor(c);
            }

            itemStack.consume(1, player);
            level.playSound(null, blockPos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    };
}
