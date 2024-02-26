package mchorse.bbs_mod.cubic.render;

import mchorse.bbs_mod.cubic.data.model.Model;
import mchorse.bbs_mod.cubic.data.model.ModelGroup;
import mchorse.bbs_mod.graphics.vao.VAOBuilder;
import mchorse.bbs_mod.utils.pose.MatrixStack;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class CubicMatrixRenderer implements ICubicRenderer
{
    public List<Matrix4f> matrices;

    public CubicMatrixRenderer(Model model)
    {
        this.matrices = new ArrayList<>();

        for (int i = 0; i < model.getAllGroupKeys().size(); i++)
        {
            this.matrices.add(new Matrix4f());
        }
    }

    @Override
    public boolean renderGroup(VAOBuilder builder, MatrixStack stack, ModelGroup group, Model model)
    {
        this.matrices.get(group.index).set(stack.getModelMatrix());

        return false;
    }
}