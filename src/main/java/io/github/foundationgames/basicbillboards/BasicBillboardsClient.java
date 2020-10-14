package io.github.foundationgames.basicbillboards;

import io.github.foundationgames.basicbillboards.block.entity.render.BillboardBlockEntityRenderer;
import io.github.foundationgames.basicbillboards.screen.BillboardGuiDescription;
import io.github.foundationgames.basicbillboards.screen.BillboardScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class BasicBillboardsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.<BillboardGuiDescription, BillboardScreen>register(BasicBillboards.BILLBOARD_HANDLER, (gui, inventory, title) -> new BillboardScreen(gui, inventory.player));
        BlockEntityRendererRegistry.INSTANCE.register(BasicBillboards.BILLBOARD_BLOCKENTITY, BillboardBlockEntityRenderer::new);
    }
}
