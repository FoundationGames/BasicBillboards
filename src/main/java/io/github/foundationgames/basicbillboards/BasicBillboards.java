package io.github.foundationgames.basicbillboards;

import io.github.foundationgames.basicbillboards.block.BillboardBlock;
import io.github.foundationgames.basicbillboards.block.entity.BillboardBlockEntity;
import io.github.foundationgames.basicbillboards.screen.BillboardGuiDescription;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class BasicBillboards implements ModInitializer {

    public static final Block BILLBOARD = Registry.register(Registry.BLOCK, id("billboard"), new BillboardBlock(FabricBlockSettings.copy(Blocks.OAK_BUTTON)));

    public static final ScreenHandlerType<BillboardGuiDescription> BILLBOARD_HANDLER = ScreenHandlerRegistry.registerExtended(id("billboard"), (syncId, inventory, buf) -> new BillboardGuiDescription(syncId, inventory, buf.readBlockPos()));

    public static final BlockEntityType<BillboardBlockEntity> BILLBOARD_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("billboard"), BlockEntityType.Builder.create(BillboardBlockEntity::new, BILLBOARD).build(null));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, id("billboard"), new BlockItem(BILLBOARD, new Item.Settings().group(ItemGroup.DECORATIONS)));

        ServerSidePacketRegistry.INSTANCE.register(id("billboard_operation"), (ctx, buf) -> {
            BlockPos pos = buf.readBlockPos();
            byte operation = buf.readByte();
            int data = buf.readInt();
            ctx.getTaskQueue().execute(() -> {
                BlockEntity b = ctx.getPlayer().getEntityWorld().getBlockEntity(pos);
                if(b instanceof BillboardBlockEntity) {
                    BillboardBlockEntity be = (BillboardBlockEntity)b;
                    be.performOperation(operation, data);
                }
            });
        });
        ServerSidePacketRegistry.INSTANCE.register(id("billboard_text_modif"), (ctx, buf) -> {
            BlockPos pos = buf.readBlockPos();
            int line = buf.readInt();
            boolean delete = buf.readBoolean();
            String text = buf.readString(32767);
            ctx.getTaskQueue().execute(() -> {
                BlockEntity b = ctx.getPlayer().getEntityWorld().getBlockEntity(pos);
                if(b instanceof BillboardBlockEntity) {
                    BillboardBlockEntity be = (BillboardBlockEntity)b;
                    be.modifyText(line, text, delete);
                }
            });
        });
    }

    public static Identifier id(String path) {
        return new Identifier("basicbillboards", path);
    }
}
