package io.github.foundationgames.basicbillboards.block.entity.render;

import io.github.foundationgames.basicbillboards.block.BillboardBlock;
import io.github.foundationgames.basicbillboards.block.entity.BillboardBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Direction;

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
        matrices.translate(0.5, 0.8, 0.5);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rot));
        matrices.translate(0.5, 0, 0.49);

        matrices.scale(1f/12f, 1f/12f, 1f/12f);

        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));

        TextRenderer textRenderer = this.dispatcher.getTextRenderer();

        for(int i : entity.getTexts().keySet()) {
            LiteralText txt = entity.getTexts().get(i);
            //float x = (float)(-textRenderer.getWidth(txt) / 2);
            textRenderer.draw(txt, 3, i * 10, entity.getTextColor(), true, matrices.peek().getModel(), vertexConsumers, false, 0, light);
        }

        matrices.pop();
    }
}
