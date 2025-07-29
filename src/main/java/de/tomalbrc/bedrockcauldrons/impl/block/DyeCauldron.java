package de.tomalbrc.bedrockcauldrons.impl.block;

import com.mojang.serialization.MapCodec;
import de.tomalbrc.bedrockcauldrons.impl.CauldronElementHolder;
import de.tomalbrc.bedrockcauldrons.impl.CustomCauldronInteractions;
import de.tomalbrc.bedrockcauldrons.impl.block.entity.DyeCauldronBlockEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DyeCauldron extends PolymerCauldron {
    public MapCodec<LayeredCauldronBlock> CODEC = simpleCodec(DyeCauldron::new);

    public DyeCauldron(Properties properties) {
        super(properties, CustomCauldronInteractions.DYE);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DyeCauldronBlockEntity(blockPos, blockState);
    }

    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new CauldronElementHolder(initialBlockState, false);
    }


    @Override
    public @NotNull MapCodec<LayeredCauldronBlock> codec() {
        return CODEC;
    }
}
