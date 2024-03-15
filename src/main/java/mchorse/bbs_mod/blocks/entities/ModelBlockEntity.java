package mchorse.bbs_mod.blocks.entities;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.entities.StubEntity;
import mchorse.bbs_mod.forms.forms.BillboardForm;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.resources.Link;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModelBlockEntity extends BlockEntity
{
    private Form form;
    private IEntity entity = new StubEntity();

    public ModelBlockEntity(BlockPos pos, BlockState state)
    {
        super(BBSMod.MODEL_BLOCK_ENTITY, pos, state);

        BillboardForm model = new BillboardForm();

        model.texture.set(Link.create("assets:textures/bee.png"));

        this.form = model;
    }

    public Form getForm()
    {
        return this.form;
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        if (this.form != null)
        {
            this.form.update(this.entity);
        }
    }
}