package mchorse.bbs_mod.forms.renderers;

import mchorse.bbs_mod.forms.entities.IEntity;
import net.minecraft.client.util.math.MatrixStack;

public class FormRenderingContext
{
    public IEntity entity;
    public MatrixStack stack;
    public float transition;

    public FormRenderingContext(IEntity entity, MatrixStack stack, float transition)
    {
        this.entity = entity;
        this.stack = stack;
        this.transition = transition;
    }

    public float getTransition()
    {
        return this.transition;
    }
}