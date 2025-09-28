package de.tomalbrc.bedrockcauldrons;

import de.tomalbrc.bedrockcauldrons.impl.CustomCauldronInteractions;
import de.tomalbrc.bedrockcauldrons.impl.ModBlocks;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

public class BedrockCauldrons implements ModInitializer {
    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets("bedrock-cauldrons");
        PolymerResourcePackUtils.markAsRequired();

        ModConfig.load();
        CustomCauldronInteractions.init();
        ModBlocks.init();
    }
}
