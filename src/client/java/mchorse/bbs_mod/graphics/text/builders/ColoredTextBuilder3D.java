package mchorse.bbs_mod.graphics.text.builders;

import mchorse.bbs_mod.graphics.vao.VBOAttributes;
import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.render.BufferBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ColoredTextBuilder3D extends BaseColoredTextBuilder
{
    private Vector3f offset = new Vector3f();

    public ColoredTextBuilder3D setup(int color)
    {
        return this.setup(color, 0, 0, 0);
    }

    public ColoredTextBuilder3D setup(int color, float x, float y, float z)
    {
        this.color.set(color);
        this.offset.set(x, y, z);

        return this;
    }

    @Override
    public VBOAttributes getAttributes()
    {
        return VBOAttributes.VERTEX_NORMAL_UV_RGBA;
    }

    @Override
    public BufferBuilder put(BufferBuilder builder, Matrix4f matrix4f, float x, float y, float u, float v, float tw, float th, Color color)
    {
        if (this.multiply)
        {
            builder.vertex(x + this.offset.x, y + this.offset.y, this.offset.z)
                .vertex(0F, 0F, 1F)
                .texture(u / tw, v / th)
                .color(this.color.r * color.r, this.color.g * color.g, this.color.b * color.b, this.color.a * color.a)
                .next();

            return builder;
        }

        Color c = this.color;

        if (color.r < 1F || color.g < 1F || color.b < 1F)
        {
            c = color;
        }

        builder.vertex(x + this.offset.x, y + this.offset.y, this.offset.z)
            .vertex(0F, 0F, 1F)
            .texture(u / tw, v / th)
            .color(c.r, c.g, c.b, this.color.a * color.a)
            .next();

        return builder;
    }
}