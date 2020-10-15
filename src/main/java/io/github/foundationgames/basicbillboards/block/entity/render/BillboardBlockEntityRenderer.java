package io.github.foundationgames.basicbillboards.block.entity.render;

import io.github.foundationgames.basicbillboards.BasicBillboards;
import io.github.foundationgames.basicbillboards.block.BillboardBlock;
import io.github.foundationgames.basicbillboards.block.entity.BillboardBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

import java.awt.*;

public class BillboardBlockEntityRenderer extends BlockEntityRenderer<BillboardBlockEntity> {

    public BillboardBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BillboardBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        Direction dir = entity.getWorld().getBlockState(entity.getPos()).get(BillboardBlock.FACING);
        int rot = 0;
        if(dir == Direction.SOUTH) rot = 180;
        else if(dir == Direction.EAST) rot = 270;
        else if(dir == Direction.WEST) rot = 90;
        matrices.translate(0.5, 1, 0.5);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rot));
        matrices.translate(0.5, 0, 0.49);

        int s = entity.getSize();
        matrices.scale(1f/s, 1f/s, 1f/s);
        matrices.translate(0, 0, (1f/s)*-0.07);

        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));

        TextRenderer textRenderer = this.dispatcher.getTextRenderer();

        int mw = 0;
        int h = 0;

        matrices.translate(-1, -1, 0);

        Color bdc = new Color(entity.getBDColor());
        Color bgc = new Color(entity.getBGColor());

        for (int i = 0; i < 3; i++) {
            LiteralText txt = entity.getTexts().getOrDefault(i, null);
            //float x = (float)(-textRenderer.getWidth(txt) / 2);
            if(txt != null && txt.asString().length() > 0) {
                textRenderer.draw(txt, 4, 4 + (i * 14), entity.getTextColor(), true, matrices.peek().getModel(), vertexConsumers, false, 0, light);
                mw = Math.max(mw, textRenderer.getWidth(txt));
                h++;
            }
        }

        matrices.translate(0, 0, 0.02);

        if(entity.showsBackground()) fill(vertexConsumers, matrices, light, overlay, (float)bgc.getRed() / 255, (float)bgc.getGreen() / 255, (float)bgc.getBlue() / 255, 1f, 2, 2, mw + 4, (h * 14) - 2);

        matrices.translate(0, 0, 0.022);

        if(entity.showsBorder()) {
            fill(vertexConsumers, matrices, light, overlay, (float)bdc.getRed() / 255, (float)bdc.getGreen() / 255, (float)bdc.getBlue() / 255, 1f, 1, 1, mw + 6, (h * 14));
        }

        matrices.pop();
    }

    private static void fill(VertexConsumerProvider vertexConsumers, MatrixStack matrices, int light, int overlay, float r, float g, float b, float a, int x, int y, int width, int height) {
        Matrix4f matrix4f = matrices.peek().getModel();
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(BasicBillboards.id("textures/gui/draw_texture.png")));
        buffer.vertex(matrix4f, (float)x, (float)height+y, (float)0).color(r, g, b, a).texture(0f, 1f).overlay(overlay).light(light).normal((float)x, (float)height+y, (float)0).next();
        buffer.vertex(matrix4f, (float)width+x, (float)height+y, (float)0).color(r, g, b, a).texture(1f, 1f).overlay(overlay).light(light).normal((float)width+x, (float)height+y, (float)0).next();
        buffer.vertex(matrix4f, (float)width+x, (float)y, (float)0).color(r, g, b, a).texture(1f, 0f).overlay(overlay).light(light).normal((float)width+x, (float)y, (float)0).next();
        buffer.vertex(matrix4f, (float)x, (float)y, (float)0).color(r, g, b, a).texture(0f, 0f).overlay(overlay).light(light).normal((float)x, (float)y, (float)0).next();
    }

    /*private static void drawTexture(Identifier texture, VertexConsumerProvider vertexConsumers, MatrixStack matrices, int light, int overlay, float r, float g, float b, float a, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight) {
        float u0 = (float)u / (float)texWidth;
        float u1 = ((float)u + (float)width) / (float)texWidth;
        float v0 = (float)v / (float)texHeight;
        float v1 = ((float)v + (float)height) / (float)texHeight;
        Matrix4f matrix4f = matrices.peek().getModel();
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));
        buffer.vertex(matrix4f, (float)x, (float)height+y, (float)0).color(r, g, b, a).texture(u0, v1).overlay(overlay).light(light).normal((float)x, (float)height+y, (float)0).next();
        buffer.vertex(matrix4f, (float)width+x, (float)height+y, (float)0).color(r, g, b, a).texture(u1, v1).overlay(overlay).light(light).normal((float)width+x, (float)height+y, (float)0).next();
        buffer.vertex(matrix4f, (float)width+x, (float)y, (float)0).color(r, g, b, a).texture(u1, v0).overlay(overlay).light(light).normal((float)width+x, (float)y, (float)0).next();
        buffer.vertex(matrix4f, (float)x, (float)y, (float)0).color(r, g, b, a).texture(u0, v0).overlay(overlay).light(light).normal((float)x, (float)y, (float)0).next();
    }*/
}
