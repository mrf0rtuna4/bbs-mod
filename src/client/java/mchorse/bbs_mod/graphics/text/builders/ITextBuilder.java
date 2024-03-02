package mchorse.bbs_mod.graphics.text.builders;

import mchorse.bbs_mod.graphics.vao.VBOAttributes;
import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.render.BufferBuilder;
import org.joml.Matrix4f;

public interface ITextBuilder
{
    public static final ColoredTextBuilder2D colored2D = new ColoredTextBuilder2D();
    public static final ColoredTextBuilder3D colored3D = new ColoredTextBuilder3D();

    public void setMultiplicative(boolean multiplicative);

    public VBOAttributes getAttributes();

    public BufferBuilder put(BufferBuilder builder, Matrix4f matrix4f, float x, float y, float u, float v, float tw, float th, Color color);
}