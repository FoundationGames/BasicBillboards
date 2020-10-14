package io.github.foundationgames.basicbillboards.screen;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.foundationgames.basicbillboards.BasicBillboards;
import io.github.foundationgames.basicbillboards.block.entity.BillboardBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class BillboardGuiDescription extends SyncedGuiDescription {
    private final BillboardBlockEntity blockEntity;

    public BillboardGuiDescription(int syncId, PlayerInventory playerInv, BlockPos pos) {
        super(BasicBillboards.BILLBOARD_HANDLER, syncId, playerInv);
        if(playerInv.player.world.getBlockEntity(pos) instanceof BillboardBlockEntity) {
            blockEntity = (BillboardBlockEntity)playerInv.player.world.getBlockEntity(pos);
        } else blockEntity = null;

        WGridPanel root = new WGridPanel(9);
        root.setSize(300, 170);
        setRootPanel(root);

        WTextField line1 = new WTextField();
        line1.setMaxLength(32);
        root.add(line1, 0, 1, 23, 7);

        WButton button = new WButton();
        button.setLabel(new TranslatableText("button.basicbillboards.set_text"));
        button.setOnClick(() -> {
            blockEntity.clientSyncedModifyText(0, line1.getText(), false);
        });
        root.add(button, 24, 1, 6, 7);


        root.validate(this);
    }
}
