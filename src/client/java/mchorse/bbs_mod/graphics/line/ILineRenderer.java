package mchorse.bbs_mod.graphics.line;

import mchorse.bbs_mod.graphics.vao.VAOBuilder;

public interface ILineRenderer <T>
{
    public void render(VAOBuilder builder, LinePoint<T> point);
}