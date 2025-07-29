package de.tomalbrc.bedrockcauldrons.impl.block.entity;

import de.tomalbrc.bedrockcauldrons.impl.CauldronElementHolder;
import de.tomalbrc.bedrockcauldrons.impl.ModBlocks;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class PotionCauldronBlockEntity extends BlockEntity implements UpdateableBlockEntity {
    @Nullable
    private PotionContents potion;

    public PotionCauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlocks.POTION_CAULDON_ENTITY, blockPos, blockState);
    }

    public void setPotion(PotionContents potion) {
        if (this.getLevel() != null) {
            this.potion = potion;
            this.update(null);
            this.setChanged();
        }
    }

    public void update(CauldronElementHolder elementHolder) {
        if (this.potion != null && this.level != null) {
            var attachment = BlockAwareAttachment.get(this.level, this.getBlockPos());
            if (attachment != null && attachment.holder() instanceof CauldronElementHolder cauldronElementHolder) {
                cauldronElementHolder.setColor(potion.getColor());
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);

        valueInput.read("Potion", PotionContents.CODEC).ifPresent(x -> {
            this.potion = x;
            this.update(null);
        });
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        valueOutput.store("Potion", PotionContents.CODEC, potion);
    }

    public Holder<Potion> getPotion() {
        return this.potion != null ? this.potion.potion().orElseThrow() : null;
    }
}
