package de.tomalbrc.bedrockcauldrons.impl;

import de.tomalbrc.bedrockcauldrons.impl.block.PolymerCauldron;
import de.tomalbrc.bedrockcauldrons.impl.block.entity.UpdateableBlockEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class CauldronElementHolder extends ElementHolder {
    private final ItemDisplayElement levelDisplay;
    private final boolean effectParticles;
    private int color = 0x50_50_ff;

    int timeOffset = 0;

    public CauldronElementHolder(BlockState blockState, boolean effectParticles) {
        super();

        this.effectParticles = effectParticles;

        this.levelDisplay = new ItemDisplayElement(item());
        this.levelDisplay.setYaw(180);
        this.levelDisplay.setOffset(new Vec3(0, 0.0, 0));
        this.addElement(this.levelDisplay);

        this.setVisualLevel(blockState.getValue(PolymerCauldron.LEVEL));
    }

    private ItemStack item() {
        var item = Items.PAPER.getDefaultInstance();
        item.set(DataComponents.ITEM_MODEL, ResourceLocation.fromNamespaceAndPath("bedrock-cauldrons", "liquid_level"));
        item.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
        return item;
    }

    @Override
    public boolean startWatching(ServerGamePacketListenerImpl player) {
        if (this.getWatchingPlayers().isEmpty()) {
            if (getAttachment() instanceof BlockAwareAttachment blockAwareAttachment) {
                this.timeOffset = blockAwareAttachment.hashCode() % 20;
                var world = blockAwareAttachment.getWorld();
                var be = world.getBlockEntity(blockAwareAttachment.getBlockPos());
                if (be instanceof UpdateableBlockEntity updateableBlockEntity) {
                    updateableBlockEntity.update(this);
                }
            }
        }
        return super.startWatching(player);
    }

    @Override
    protected void onTick() {
        super.onTick();

        if (effectParticles && this.getAttachment() != null && (this.getAttachment().getWorld().getGameTime()+timeOffset) % 20 == 0) {
            this.getAttachment().getWorld().sendParticles(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, this.color), false, false, this.getPos().x, this.getPos().y + 0.5, this.getPos().z, 1, 0.125f, 0f, 0.125f, 0);
        }
    }

    @Override
    public void notifyUpdate(HolderAttachment.UpdateType updateType) {
        super.notifyUpdate(updateType);

        if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
            BlockAwareAttachment blockAwareAttachment = (BlockAwareAttachment)getAttachment();
            if (blockAwareAttachment != null) {
                var level = blockAwareAttachment.getBlockState().getValue(PolymerCauldron.LEVEL);
                this.setVisualLevel(level);
            }
        }
    }

    public void setColor(int c) {
        this.color = c;
        this.levelDisplay.setItem(item());
    }

    private void setVisualLevel(int level) {
        this.setColor(this.color);
        float y = switch (level) {
            case 3 -> 0f;
            case 2 -> -3/16f;
            case 1 -> -6/16f;
            default -> 0;
        };
        this.levelDisplay.setTranslation(new Vector3f(0, y, 0));
        this.levelDisplay.startInterpolationIfDirty();
        if (getAttachment() != null && !getAttachment().shouldTick()) this.tick();
    }
}
