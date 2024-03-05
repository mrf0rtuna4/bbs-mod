package mchorse.bbs_mod.graphics.line;

import net.minecraft.client.render.BufferBuilder;

public interface ILineRenderer <T>
{
    public void render(BufferBuilder builder, LinePoint<T> point);
}