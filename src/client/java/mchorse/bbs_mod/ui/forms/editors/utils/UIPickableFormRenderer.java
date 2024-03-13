package mchorse.bbs_mod.ui.forms.editors.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.renderers.FormRenderingContext;
import mchorse.bbs_mod.graphics.Draw;
import mchorse.bbs_mod.graphics.texture.Texture;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.forms.editors.UIFormEditor;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.utils.StencilFormFramebuffer;
import mchorse.bbs_mod.utils.Pair;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class UIPickableFormRenderer extends UIFormRenderer
{
    public UIFormEditor formEditor;

    private boolean update;

    private StencilFormFramebuffer stencil = new StencilFormFramebuffer();

    public UIPickableFormRenderer(UIFormEditor formEditor)
    {
        this.formEditor = formEditor;
    }

    public void updatable()
    {
        this.update = true;
    }

    private void ensureFramebuffer()
    {
        this.stencil.setup(Link.bbs("stencil_form"));
        this.stencil.resizeGUI(this.area.w, this.area.h);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.ensureFramebuffer();
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.stencil.hasPicked() && context.mouseButton == 0)
        {
            Pair<Form, String> pair = this.stencil.getPicked();

            if (pair != null)
            {
                this.formEditor.pickFormFromRenderer(pair);

                return true;
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    protected void renderUserModel(UIContext context)
    {
        if (this.form == null)
        {
            return;
        }

        FormRenderingContext formContext = new FormRenderingContext(this.entity, context.batcher.getContext().getMatrices(), context.getTransition());

        FormUtilsClient.render(this.form, formContext);

        if (this.form.hitbox.get())
        {
            this.renderFormHitbox(context);
        }

        this.renderAxes(context);

        if (this.area.isInside(context))
        {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            this.stencil.apply(context);
            FormUtilsClient.render(this.form, formContext);

            this.stencil.pickGUI(context, this.area);
            this.stencil.unbind(context);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        }
        else
        {
            this.stencil.clearPicking();
        }
    }

    private void renderAxes(UIContext context)
    {
        Matrix4f matrix = this.formEditor.editor.getOrigin(context.getTransition());
        final float axisSize = 0.1F;
        final float axisOffset = 0.005F;
        final float outlineSize = axisSize + 0.005F;
        final float outlineOffset = axisOffset + 0.005F;

        MatrixStack stack = context.render.batcher.getContext().getMatrices();

        stack.push();

        if (matrix != null)
        {
            stack.multiplyPositionMatrix(matrix);
        }

        /* Draw axes */
        BufferBuilder builder = Tessellator.getInstance().getBuffer();

        RenderSystem.disableDepthTest();

        builder.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

        Draw.fillBox(builder, stack, 0, -outlineOffset, -outlineOffset, outlineSize, outlineOffset, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, stack, -outlineOffset, 0, -outlineOffset, outlineOffset, outlineSize, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, stack, -outlineOffset, -outlineOffset, 0, outlineOffset, outlineOffset, outlineSize, 0, 0, 0);
        Draw.fillBox(builder, stack, -outlineOffset, -outlineOffset, -outlineOffset, outlineOffset, outlineOffset, outlineOffset, 0, 0, 0);

        Draw.fillBox(builder, stack, 0, -axisOffset, -axisOffset, axisSize, axisOffset, axisOffset, 1, 0, 0);
        Draw.fillBox(builder, stack, -axisOffset, 0, -axisOffset, axisOffset, axisSize, axisOffset, 0, 1, 0);
        Draw.fillBox(builder, stack, -axisOffset, -axisOffset, 0, axisOffset, axisOffset, axisSize, 0, 0, 1);
        Draw.fillBox(builder, stack, -axisOffset, -axisOffset, -axisOffset, axisOffset, axisOffset, axisOffset, 1, 1, 1);

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.enableBlend();

        stack.pop();
    }

    private void renderFormHitbox(UIContext context)
    {
        float hitboxW = this.form.hitboxWidth.get();
        float hitboxH = this.form.hitboxHeight.get();
        float eyeHeight = hitboxH * this.form.hitboxEyeHeight.get();

        /* Draw look vector */
        final float thickness = 0.01F;
        Draw.renderBox(context.batcher.getContext().getMatrices(), -thickness, -thickness + eyeHeight, -thickness, thickness, thickness, 2F, 1F, 0F, 0F);

        /* Draw hitbox */
        Draw.renderBox(context.batcher.getContext().getMatrices(), -hitboxW / 2, 0, -hitboxW / 2, hitboxW, hitboxH, hitboxW);
    }

    @Override
    protected void update()
    {
        super.update();

        if (this.update)
        {
            this.form.update(this.entity);
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (!this.stencil.hasPicked())
        {
            return;
        }

        int index = this.stencil.getIndex();
        Texture texture = this.stencil.getFramebuffer().getMainTexture();
        Pair<Form, String> pair = this.stencil.getPicked();
        int w = texture.width;
        int h = texture.height;

        /* TODO: Shader shader = context.render.getPickingShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D);

        CommonShaderAccess.setTarget(shader, index);
        context.batcher.texturedBox(shader, texture, Colors.WHITE, this.area.x, this.area.y, this.area.w, this.area.h, 0, h, w, 0, w, h);

        if (pair != null)
        {
            String label = pair.a.getIdOrName();

            if (!pair.b.isEmpty())
            {
                label += " - " + pair.b;
            }

            context.batcher.textCard(context.font, label, context.mouseX + 12, context.mouseY + 8);
        } */
    }
}