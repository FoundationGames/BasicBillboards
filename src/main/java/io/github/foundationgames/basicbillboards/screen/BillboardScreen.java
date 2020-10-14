package io.github.foundationgames.basicbillboards.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class BillboardScreen extends CottonInventoryScreen<BillboardGuiDescription> {
    public BillboardScreen(BillboardGuiDescription description, PlayerEntity player) {
        super(description, player);
    }
}
