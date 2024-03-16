package mchorse.bbs_mod.ui.model_blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.graphics.Draw;
import mchorse.bbs_mod.mixin.client.WorldRendererMixin;
import mchorse.bbs_mod.network.ClientNetwork;
import mchorse.bbs_mod.ui.dashboard.UIDashboard;
import mchorse.bbs_mod.ui.dashboard.panels.IFlightSupported;
import mchorse.bbs_mod.ui.dashboard.panels.UIDashboardPanel;
import mchorse.bbs_mod.ui.forms.UIFormPalette;
import mchorse.bbs_mod.ui.forms.UINestedEdit;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIScrollView;
import mchorse.bbs_mod.ui.framework.elements.input.UIPropTransform;
import mchorse.bbs_mod.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.utils.AABB;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class UIModelBlockPanel extends UIDashboardPanel implements IFlightSupported
{
    public UIModelBlockEntityList modelBlocks;
    public UINestedEdit pickEdit;
    public UIPropTransform transform;

    private ModelBlockEntity modelBlock;
    private ModelBlockEntity hovered;

    public UIModelBlockPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.modelBlocks = new UIModelBlockEntityList((l) ->
        {
            this.save();
            this.fill(l.get(0), false);
        });
        this.modelBlocks.background();
        this.modelBlocks.h(UIStringList.DEFAULT_HEIGHT * 9);

        this.pickEdit = new UINestedEdit((editing) ->
        {
            UIFormPalette.open(this, editing, this.modelBlock.getForm(), (f) -> this.modelBlock.setForm(f));
        });

        this.transform = new UIPropTransform();
        this.transform.verticalCompact();

        UIScrollView scrollView = UI.scrollView(5, 10, this.modelBlocks, this.pickEdit, this.transform);

        scrollView.scroll.opposite().cancelScrolling();
        scrollView.relative(this).w(200).h(1F);

        this.fill(null, false);

        this.add(scrollView);
    }

    @Override
    public boolean needsBackground()
    {
        return false;
    }

    @Override
    public boolean canPause()
    {
        return false;
    }

    @Override
    public void open()
    {
        super.open();

        this.updateList();
    }

    @Override
    public void close()
    {
        super.close();

        this.save();
    }

    private void updateList()
    {
        this.modelBlocks.clear();

        ObjectArrayList<ChunkBuilder.BuiltChunk> chunks = ((WorldRendererMixin) MinecraftClient.getInstance().worldRenderer).getField_45616();

        for (ChunkBuilder.BuiltChunk chunk : chunks)
        {
            for (BlockEntity entity : chunk.getData().getBlockEntities())
            {
                if (entity instanceof ModelBlockEntity modelBlock)
                {
                    this.modelBlocks.add(modelBlock);
                }
            }
        }
    }

    public void fill(ModelBlockEntity modelBlock, boolean select)
    {
        this.modelBlock = modelBlock;

        if (modelBlock != null)
        {
            this.pickEdit.setForm(modelBlock.getForm());
            this.transform.setTransform(modelBlock.getTransform());
        }

        if (select)
        {
            this.modelBlocks.setCurrentScroll(modelBlock);
        }
    }

    private void save()
    {
        if (this.modelBlock != null)
        {
            ClientNetwork.sendModelBlockForm(this.modelBlock.getPos(), this.modelBlock);
        }
    }

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        if (this.hovered != null && context.mouseButton == 0)
        {
            this.fill(this.hovered, true);

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public void renderInWorld(WorldRenderContext context)
    {
        super.renderInWorld(context);

        Camera camera = context.camera();
        Vec3d pos = camera.getPos();

        MinecraftClient mc = MinecraftClient.getInstance();
        double x = mc.mouse.getX();
        double y = mc.mouse.getY();
        int w2 = (int) (mc.getWindow().getWidth() / 2F);
        int h2 = (int) (mc.getWindow().getHeight() / 2F);

        x = (x - w2) / w2;
        y = (-y + h2) / h2;

        Vector3f vector = mchorse.bbs_mod.camera.Camera.getMouseDirectionNormalized(RenderSystem.getProjectionMatrix(), context.matrixStack().peek().getPositionMatrix(), (float) x, (float) y);

        this.hovered = this.getClosestObject(new Vector3d(pos.x, pos.y, pos.z), vector);

        RenderSystem.enableDepthTest();

        for (ModelBlockEntity entity : this.modelBlocks.getList())
        {
            BlockPos blockPos = entity.getPos();

            context.matrixStack().push();
            context.matrixStack().translate(blockPos.getX() - pos.x, blockPos.getY() - pos.y, blockPos.getZ() - pos.z);

            if (this.hovered == entity)
            {
                Draw.renderBox(context.matrixStack(), 0D, 0D, 0D, 1D, 1D, 1D, 0, 0.5F, 1F);
            }
            else
            {
                Draw.renderBox(context.matrixStack(), 0D, 0D, 0D, 1D, 1D, 1D);
            }

            context.matrixStack().pop();
        }

        RenderSystem.disableDepthTest();
    }

    private ModelBlockEntity getClosestObject(Vector3d finalPosition, Vector3f mouseDirection)
    {
        ModelBlockEntity closest = null;

        for (ModelBlockEntity object : this.modelBlocks.getList())
        {
            AABB aabb = this.getHitbox(object);

            if (aabb.intersectsRay(finalPosition, mouseDirection))
            {
                if (closest == null)
                {
                    closest = object;
                }
                else
                {
                    AABB aabb2 = this.getHitbox(closest);

                    if (finalPosition.distanceSquared(aabb.x, aabb.y, aabb.z) < finalPosition.distanceSquared(aabb2.x, aabb2.y, aabb2.z))
                    {
                        closest = object;
                    }
                }
            }
        }
        return closest;
    }

    private AABB getHitbox(ModelBlockEntity closest)
    {
        BlockPos pos = closest.getPos();

        return new AABB(pos.getX(), pos.getY(), pos.getZ(), 1D, 1D, 1D);
    }
}