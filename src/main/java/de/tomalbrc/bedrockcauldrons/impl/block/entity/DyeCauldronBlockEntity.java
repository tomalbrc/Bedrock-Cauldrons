package de.tomalbrc.bedrockcauldrons.impl.block.entity;

import de.tomalbrc.bedrockcauldrons.impl.CauldronElementHolder;
import de.tomalbrc.bedrockcauldrons.impl.ModBlocks;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DyeCauldronBlockEntity extends BlockEntity implements UpdateableBlockEntity {
    private Integer color = null;

    public DyeCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlocks.DYE_CAULDON_ENTITY, blockPos, blockState);
    }

    public void setColor(int color) {
        if (this.getLevel() != null) {
            this.color = color;
            this.update(null);
            this.setChanged();
        }
    }

    public Integer getColor() {
        return this.color;
    }

    public void update(CauldronElementHolder elementHolder) {
        if (this.level != null) {
            if (elementHolder == null) {
                var attachment = BlockAwareAttachment.get(this.level, this.getBlockPos());
                if (attachment != null && attachment.holder() instanceof CauldronElementHolder cauldronElementHolder) {
                    elementHolder = cauldronElementHolder;
                }
            }

            if (elementHolder != null) {
                elementHolder.setColor(color);
                elementHolder.tick();
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);

        valueInput.read("Color", DyedItemColor.CODEC).ifPresent(x -> {
            this.color = x.rgb();
            this.update(null);
        });
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        valueOutput.store("Color", DyedItemColor.CODEC, new DyedItemColor(this.color));
    }
}
