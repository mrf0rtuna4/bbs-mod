package mchorse.bbs_mod.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.entities.StubEntity;
import mchorse.bbs_mod.forms.renderers.FormRenderingContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class ModelBlockEntityRenderer implements BlockEntityRenderer<ModelBlockEntity>
{
    public ModelBlockEntityRenderer(BlockEntityRendererFactory.Context ctx)
    {}

    @Override
    public void render(ModelBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        matrices.translate(0.5F, 0F, 0.5F);
        matrices.multiplyPositionMatrix(entity.getTransform().createMatrix());

        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos());

        if (entity.getForm() != null)
        {
            RenderSystem.enableDepthTest();
            FormUtilsClient.render(entity.getForm(), FormRenderingContext
                .set(new StubEntity(), matrices, lightAbove, tickDelta)
                .camera(MinecraftClient.getInstance().gameRenderer.getCamera()));
            RenderSystem.disableDepthTest();
        }

        matrices.pop();
    }
}