package mchorse.bbs_mod.ui.framework;

import mchorse.bbs_mod.graphics.RenderingContext;
import mchorse.bbs_mod.graphics.shaders.ShaderRepository;
import mchorse.bbs_mod.graphics.text.FontRenderer;
import mchorse.bbs_mod.graphics.texture.TextureManager;
import mchorse.bbs_mod.graphics.vao.VAOBuilder;
import mchorse.bbs_mod.ui.framework.elements.utils.Batcher2D;
import mchorse.bbs_mod.ui.framework.elements.utils.StencilMap;
import mchorse.bbs_mod.world.World;
import org.joml.Matrix4f;

public class UIRenderingContext extends RenderingContext
{
    /**
     * Orthographic projection.
     */
    public Matrix4f projection;

    public Batcher2D batcher;

    private RenderingContext render;
    private ShaderRepository pickingShaders = new ShaderRepository();
    private StencilMap stencil = new StencilMap();

    public UIRenderingContext(RenderingContext render, Matrix4f projection)
    {
        this.render = render;
        this.projection = projection;
        this.batcher = new Batcher2D(this);
    }

    public ShaderRepository getPickingShaders()
    {
        return this.pickingShaders;
    }

    public StencilMap getStencil()
    {
        return this.stencil;
    }

    /* Rendering context implementations */

    @Override
    public boolean isDebug()
    {
        return this.render.isDebug();
    }

    @Override
    public World getWorld()
    {
        return this.render.getWorld();
    }

    @Override
    public FontRenderer getFont()
    {
        return this.render.getFont();
    }

    @Override
    public VAOBuilder getVAO()
    {
        return this.render.getVAO();
    }

    @Override
    public TextureManager getTextures()
    {
        return this.render.getTextures();
    }
}
