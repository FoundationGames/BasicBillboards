package io.github.foundationgames.basicbillboards.screen;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.foundationgames.basicbillboards.BasicBillboards;
import io.github.foundationgames.basicbillboards.block.entity.BillboardBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

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
        line1.setText(blockEntity.getTexts().getOrDefault(0, new LiteralText("")).getString());
        root.add(line1, 0, 1, 23, 7);
        WTextField line2 = new WTextField();
        line2.setMaxLength(32);
        line2.setText(blockEntity.getTexts().getOrDefault(1, new LiteralText("")).getString());
        root.add(line2, 0, 3, 23, 7);
        WTextField line3 = new WTextField();
        line3.setMaxLength(32);
        line3.setText(blockEntity.getTexts().getOrDefault(2, new LiteralText("")).getString());
        root.add(line3, 0, 5, 23, 7);

        WButton button = new WButton();
        button.setLabel(new TranslatableText("button.basicbillboards.set_text"));
        button.setOnClick(() -> {
            String l1 = line1.getText();
            String l2 = line2.getText();
            String l3 = line3.getText();
            boolean dl2 = false;
            boolean dl3 = false;
            if(l3.length() < 1) {
                dl3 = true;
            }
            if(l2.length() < 1) {
                l2 = l3;
                if(dl3) dl2 = true;
                dl3 = true;
            }
            if(l1.length() < 1) {
                l1 = l2;
                if(dl2) l1 = "Sample Text";
                dl2 = true;
                if(!dl3) {
                    l2 = l3;
                    dl3 = true;
                }
            }
            blockEntity.clientSyncedModifyText(0, l1, false);
            blockEntity.clientSyncedModifyText(1, l2, dl2);
            blockEntity.clientSyncedModifyText(2, l3, dl3);
        });
        root.add(button, 24, 3, 6, 7);

        WTextField txtcolor = new WTextField();
        txtcolor.setMaxLength(6);
        txtcolor.setText(Integer.toHexString(blockEntity.getTextColor()));
        root.add(txtcolor, 0, 8, 7, 7);

        WButton setTextColor = new WButton();
        setTextColor.setLabel(new TranslatableText("button.basicbillboards.set_text_color"));
        setTextColor.setOnClick(() -> {
            int color = tryParseInt("0x"+txtcolor.getText());
            blockEntity.clientSyncedOperation(BillboardBlockEntity.Ops.SET_TXT_COLOR, color);
        });
        root.add(setTextColor, 8, 8, 14, 7);


        WTextField bgcolor = new WTextField();
        bgcolor.setMaxLength(6);
        bgcolor.setText(Integer.toHexString(blockEntity.getBGColor()));
        root.add(bgcolor, 0, 11, 7, 7);

        WButton setBGColor = new WButton();
        setBGColor.setLabel(new TranslatableText("button.basicbillboards.set_background_color"));
        setBGColor.setOnClick(() -> {
            int color = tryParseInt("0x"+bgcolor.getText());
            blockEntity.clientSyncedOperation(BillboardBlockEntity.Ops.SET_BG_COLOR, color);
        });
        root.add(setBGColor, 8, 11, 14, 7);


        WTextField bdcolor = new WTextField();
        bdcolor.setMaxLength(6);
        bdcolor.setText(Integer.toHexString(blockEntity.getBDColor()));
        root.add(bdcolor, 0, 14, 7, 7);

        WButton setBdColor = new WButton();
        setBdColor.setLabel(new TranslatableText("button.basicbillboards.set_border_color"));
        setBdColor.setOnClick(() -> {
            int color = tryParseInt("0x"+bdcolor.getText());
            blockEntity.clientSyncedOperation(BillboardBlockEntity.Ops.SET_BORDER_COLOR, color);
        });
        root.add(setBdColor, 8, 14, 14, 7);


        WButton toggleBd = new WButton();
        toggleBd.setLabel(new TranslatableText(blockEntity.showsBorder() ? "button.basicbillboards.hide_border" : "button.basicbillboards.show_border"));
        toggleBd.setOnClick(() -> {
            toggleBd.setLabel(new TranslatableText(!blockEntity.showsBorder() ? "button.basicbillboards.hide_border" : "button.basicbillboards.show_border"));
            blockEntity.clientSyncedOperation(BillboardBlockEntity.Ops.SET_SHOW_BORDER, !blockEntity.showsBorder() ? 1 : 0);
        });
        root.add(toggleBd, 23, 14, 10, 7);

        WButton toggleBG = new WButton();
        toggleBG.setLabel(new TranslatableText(blockEntity.showsBackground() ? "button.basicbillboards.hide_background" : "button.basicbillboards.show_background"));
        toggleBG.setOnClick(() -> {
            toggleBG.setLabel(new TranslatableText(!blockEntity.showsBackground() ? "button.basicbillboards.hide_background" : "button.basicbillboards.show_background"));
            blockEntity.clientSyncedOperation(BillboardBlockEntity.Ops.SET_SHOW_BG, !blockEntity.showsBackground() ? 1 : 0);
        });
        root.add(toggleBG, 23, 11, 10, 7);


        WSlider sizeSlider = new WSlider(1, 21, Axis.HORIZONTAL);
        root.add(sizeSlider, 0, 17, 10, 3);
        sizeSlider.setValue(24 - blockEntity.getSize());

        WButton setSize = new WButton();
        setSize.setLabel(new TranslatableText("button.basicbillboards.set_size"));
        setSize.setOnClick(() -> {
            System.out.println(24 - sizeSlider.getValue());
            blockEntity.clientSyncedOperation(BillboardBlockEntity.Ops.SET_SIZE, 24 - sizeSlider.getValue());
        });
        root.add(setSize, 11, 17, 11, 7);

        root.validate(this);
    }

    private static int tryParseInt(String integer) {
        try {
            return Integer.decode(integer);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
