package mchorse.bbs_mod.ui.model_blocks;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.mixin.client.WorldRendererMixin;
import mchorse.bbs_mod.network.ClientNetwork;
import mchorse.bbs_mod.ui.dashboard.UIDashboard;
import mchorse.bbs_mod.ui.dashboard.panels.IFlightSupported;
import mchorse.bbs_mod.ui.dashboard.panels.UIDashboardPanel;
import mchorse.bbs_mod.ui.forms.UIFormPalette;
import mchorse.bbs_mod.ui.forms.UINestedEdit;
import mchorse.bbs_mod.ui.framework.elements.UIScrollView;
import mchorse.bbs_mod.ui.framework.elements.input.UIPropTransform;
import mchorse.bbs_mod.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs_mod.ui.utils.UI;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkBuilder;

public class UIModelBlockPanel extends UIDashboardPanel implements IFlightSupported
{
    public UIModelBlockEntityList modelBlocks;
    public UINestedEdit pickEdit;
    public UIPropTransform transform;

    private ModelBlockEntity modelBlock;

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
}