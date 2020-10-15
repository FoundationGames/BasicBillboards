package io.github.foundationgames.basicbillboards.block.entity;

import com.google.common.collect.Lists;
import io.github.foundationgames.basicbillboards.BasicBillboards;
import io.github.foundationgames.basicbillboards.screen.BillboardGuiDescription;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class BillboardBlockEntity extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory {

    private boolean showBackground = true;
    private boolean showBorder = true;
    private boolean centered = true;
    private boolean textShadow = true;
    private boolean glowing = false;
    private int backgroundColor = 0xd7cec5;
    private int borderColor = 0x353535;
    private int textColor = 0x756f69;
    private int size = 21;
    private float xAlign = 0.0f;
    private float yAlign = 0.0f;
    private final Int2ObjectMap<String> texts = Util.make(() -> {
        Int2ObjectMap<String> m = new Int2ObjectOpenHashMap<>();
        m.put(0, "Sample Text");
        return m;
    });

    private final Int2ObjectMap<LiteralText> cachedTexts = Util.make(() -> {
        Int2ObjectMap<LiteralText> m = new Int2ObjectOpenHashMap<>();
        m.put(0, new LiteralText("Sample Text"));
        return m;
    });

    public BillboardBlockEntity() {
        super(BasicBillboards.BILLBOARD_BLOCKENTITY);
    }

    public Int2ObjectMap<LiteralText> getTexts() { return cachedTexts; }

    public int getTextColor() { return textColor; }
    public int getBGColor() { return backgroundColor; }
    public int getBDColor() { return borderColor; }
    public int getSize() { return size; }

    public float getXAlign() { return xAlign; }
    public float getYAlign() { return yAlign; }

    public boolean showsBackground() { return showBackground; }
    public boolean showsBorder() { return showBorder; }
    public boolean centered() { return centered; }
    public boolean textShadow() { return textShadow; }
    public boolean glowing() { return glowing; }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        showBackground = tag.getBoolean("ShowBackground");
        showBorder = tag.getBoolean("ShowBorder");
        centered = tag.getBoolean("Centered");
        textShadow = tag.getBoolean("TextShadow");
        glowing = tag.getBoolean("Glowing");
        backgroundColor = tag.getInt("BackgroundColor");
        borderColor = tag.getInt("BorderColor");
        textColor = tag.getInt("TextColor");
        size = tag.getInt("Size");
        xAlign = tag.getFloat("XAlign");
        yAlign = tag.getFloat("YAlign");
        CompoundTag list = tag.getCompound("Texts");
        texts.clear();
        cachedTexts.clear();
        for(String s : list.getKeys()) {
            int i = Integer.parseInt(s);
            texts.put(i, list.getString(s));
            cachedTexts.put(i, new LiteralText(list.getString(s)));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putBoolean("ShowBackground", showBackground);
        tag.putBoolean("ShowBorder", showBorder);
        tag.putBoolean("Centered", centered);
        tag.putBoolean("TextShadow", textShadow);
        tag.putBoolean("Glowing", glowing);
        tag.putInt("BackgroundColor", backgroundColor);
        tag.putInt("BorderColor", borderColor);
        tag.putInt("TextColor", textColor);
        tag.putInt("Size", size);
        tag.putFloat("XAlign", xAlign);
        tag.putFloat("YAlign", yAlign);
        CompoundTag list = new CompoundTag();
        for(int i : texts.keySet()) {
            list.putString(String.valueOf(i), texts.get(i));
        }
        tag.put("Texts", list);
        return tag;
    }

    @Environment(EnvType.CLIENT)
    public void clientSyncedOperation(byte operation, int data) {
        performOperation(operation, data);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeByte(operation);
        buf.writeInt(data);
        ClientSidePacketRegistry.INSTANCE.sendToServer(BasicBillboards.id("billboard_operation"), buf);
    }

    @Environment(EnvType.CLIENT)
    public void clientSyncedModifyText(int line, String text, boolean delete) {
        modifyText(line, text, delete);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeInt(line);
        buf.writeBoolean(delete);
        buf.writeString(text);
        ClientSidePacketRegistry.INSTANCE.sendToServer(BasicBillboards.id("billboard_text_modif"), buf);
    }

    public void performOperation(byte operation, int data) {
        if(operation == Ops.SET_SHOW_BG) this.showBackground = data > 0;
        else if(operation == Ops.SET_SHOW_BORDER) this.showBorder = data > 0;
        else if(operation == Ops.SET_BG_COLOR) this.backgroundColor = data;
        else if(operation == Ops.SET_BORDER_COLOR) this.borderColor = data;
        else if(operation == Ops.SET_TXT_COLOR) this.textColor = data;
        else if(operation == Ops.SET_SIZE) this.size = data;
        else if(operation == Ops.SET_CENTERED) this.centered = data > 0;
        else if(operation == Ops.SET_X_ALIGN) this.xAlign = (float)data / 100;
        else if(operation == Ops.SET_Y_ALIGN) this.yAlign = (float)data / 100;
        else if(operation == Ops.SET_TXT_SHADOW) this.textShadow = data > 0;
        else if(operation == Ops.SET_GLOWING) this.glowing = data > 0;
        if(!world.isClient()) sync();
    }

    @Override
    public double getSquaredRenderDistance() {
        return 667D;
    }

    public void modifyText(int line, String text, boolean delete) {
        if(delete) texts.remove(line);
        else {
            texts.put(line, text);
            cachedTexts.put(line, new LiteralText(text));
        }
        if(!world.isClient()) sync();
    }

    public static class Ops {
        public static final byte SET_SHOW_BG = 0x00;
        public static final byte SET_SHOW_BORDER = 0x01;
        public static final byte SET_BG_COLOR = 0x02;
        public static final byte SET_BORDER_COLOR = 0x03;
        public static final byte SET_TXT_COLOR = 0x04;
        public static final byte SET_SIZE = 0x05;
        public static final byte SET_CENTERED = 0x06;
        public static final byte SET_X_ALIGN = 0x07;
        public static final byte SET_Y_ALIGN = 0x08;
        public static final byte SET_TXT_SHADOW = 0x09;
        public static final byte SET_GLOWING = 0x0A;
    }

    //-------------------------------------------------------------------------------------------------------------

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(world.getBlockState(pos), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new BillboardGuiDescription(syncId, inv, pos);
    }
}
