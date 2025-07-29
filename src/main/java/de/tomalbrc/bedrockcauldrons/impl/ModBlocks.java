package de.tomalbrc.bedrockcauldrons.impl;

import de.tomalbrc.bedrockcauldrons.impl.block.DyeCauldron;
import de.tomalbrc.bedrockcauldrons.impl.block.PotionCauldron;
import de.tomalbrc.bedrockcauldrons.impl.block.entity.DyeCauldronBlockEntity;
import de.tomalbrc.bedrockcauldrons.impl.block.entity.PotionCauldronBlockEntity;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlocks {
    public static final Block POTION_CAULDON = registerBlock(ResourceLocation.withDefaultNamespace("potion_cauldron"), PotionCauldron::new, BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON));
    public static final Block DYE_CAULDON = registerBlock(ResourceLocation.withDefaultNamespace("dye_cauldron"), DyeCauldron::new, BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON));
    public static final BlockEntityType<DyeCauldronBlockEntity> DYE_CAULDON_ENTITY = registerBlockEntity(ResourceLocation.withDefaultNamespace("dye_cauldron_entity"), FabricBlockEntityTypeBuilder.create(DyeCauldronBlockEntity::new, DYE_CAULDON).build());
    public static final BlockEntityType<PotionCauldronBlockEntity> POTION_CAULDON_ENTITY = registerBlockEntity(ResourceLocation.withDefaultNamespace("potion_cauldron_entity"), FabricBlockEntityTypeBuilder.create(PotionCauldronBlockEntity::new, POTION_CAULDON).build());

    public static <T extends Block> T registerBlock(ResourceLocation id, Function<BlockBehaviour.Properties, T> function, BlockBehaviour.Properties properties) {
        var key = ResourceKey.create(Registries.BLOCK, id);
        T block = function.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }

    public static ResourceKey<BlockEntityType<?>> key(ResourceLocation id) {
        return ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, id);
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(ResourceLocation id, BlockEntityType<T> type) {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key(id), type);
        PolymerBlockUtils.registerBlockEntity(type);
        return type;
    }

    public static void init() {
        Item.BY_BLOCK.put(DYE_CAULDON, Items.CAULDRON);
        Item.BY_BLOCK.put(POTION_CAULDON, Items.CAULDRON);
    }
}
