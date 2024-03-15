package mchorse.bbs_mod.graphics.layers;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;

public class TestLayer extends RenderLayer
{
    public TestLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction)
    {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
}