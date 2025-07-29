package de.tomalbrc.bedrockcauldrons.impl.block;

import com.mojang.serialization.MapCodec;
import de.tomalbrc.bedrockcauldrons.impl.CauldronElementHolder;
import de.tomalbrc.bedrockcauldrons.impl.CustomCauldronInteractions;
import de.tomalbrc.bedrockcauldrons.impl.block.entity.PotionCauldronBlockEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionCauldron extends PolymerCauldron {
    public MapCodec<LayeredCauldronBlock> CODEC = simpleCodec(PotionCauldron::new);

    public PotionCauldron(Properties properties) {
        super(properties, CustomCauldronInteractions.POTION);
    }

    @Override
    public boolean tickElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return true;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PotionCauldronBlockEntity(blockPos, blockState);
    }

    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new CauldronElementHolder(initialBlockState, true);
    }

    @Override
    public @NotNull MapCodec<LayeredCauldronBlock> codec() {
        return CODEC;
    }
}
