package de.tomalbrc.bedrockcauldrons.impl.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.packettweaker.PacketContext;

public abstract class PolymerCauldron extends LayeredCauldronBlock implements EntityBlock, PolymerBlock, BlockWithElementHolder {
    private final BlockState cauldron = Blocks.CAULDRON.defaultBlockState();

    public PolymerCauldron(Properties properties, CauldronInteraction.InteractionMap interactionMap) {
        super(Biome.Precipitation.NONE, interactionMap, properties);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return cauldron;
    }
}
