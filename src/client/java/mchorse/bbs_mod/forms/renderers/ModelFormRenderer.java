package mchorse.bbs_mod.forms.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.cubic.CubicModel;
import mchorse.bbs_mod.cubic.CubicModelAnimator;
import mchorse.bbs_mod.cubic.animation.Animator;
import mchorse.bbs_mod.cubic.data.model.Model;
import mchorse.bbs_mod.cubic.data.model.ModelGroup;
import mchorse.bbs_mod.cubic.render.CubicCubeRenderer;
import mchorse.bbs_mod.cubic.render.CubicMatrixRenderer;
import mchorse.bbs_mod.cubic.render.CubicRenderer;
import mchorse.bbs_mod.forms.FormUtilsClient;
import mchorse.bbs_mod.forms.ITickable;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.entities.MCEntity;
import mchorse.bbs_mod.forms.entities.StubEntity;
import mchorse.bbs_mod.forms.forms.BodyPart;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.utils.StringUtils;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.math.MathUtils;
import mchorse.bbs_mod.utils.pose.Pose;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelFormRenderer extends FormRenderer<ModelForm> implements ITickable
{
    private Matrix4f uiMatrix = new Matrix4f();
    private Map<String, Matrix4f> bones = new HashMap<>();

    private Animator animator;
    private long lastCheck;
    private IEntity entity = new StubEntity();

    public static CubicModel getModel(ModelForm form)
    {
        return BBSModClient.getModels().getModel(form.model.get());
    }

    public ModelFormRenderer(ModelForm form)
    {
        super(form);
    }

    public CubicModel getModel()
    {
        return getModel(this.form);
    }

    public Pose getPose(float transition)
    {
        return this.form.pose.get(transition);
    }

    public void resetAnimator()
    {
        this.animator = null;
        this.lastCheck = 0;
    }

    public void ensureAnimator()
    {
        CubicModel model = this.getModel();

        if (model == null || this.lastCheck >= model.loadTime)
        {
            return;
        }

        this.animator = new Animator();
        this.animator.setup(model, this.form.actions.get());

        this.lastCheck = model.loadTime;
    }

    @Override
    public List<String> getBones()
    {
        return new ArrayList<>(this.getModel().model.getAllGroupKeys());
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        context.batcher.flush();

        this.ensureAnimator();

        CubicModel model = this.getModel();

        if (this.animator != null && model != null)
        {
            MatrixStack stack = context.batcher.getContext().getMatrices();

            stack.push();
            stack.peek().getNormalMatrix().scale(1, -1, 1);

            float scale = (y2 - y1) / 2.5F;
            int x = x1 + (x2 - x1) / 2;
            float y = y1 + (y2 - y1) * 0.85F;

            this.uiMatrix.identity();
            this.uiMatrix.translate(x, y, 40);
            this.uiMatrix.scale(scale, -scale, scale);
            this.uiMatrix.rotateX(MathUtils.PI / 8);
            this.uiMatrix.rotateY(MathUtils.toRad(context.getTickTransition()));
            this.uiMatrix.mul(this.form.transform.get(context.getTransition()).createMatrix());

            Link link = this.form.texture.get(context.getTransition());
            Link texture = link == null ? model.texture : link;
            Color color = this.form.color.get(context.getTransition());

            CubicModelAnimator.resetPose(model.model);

            this.animator.applyActions(null, model.model, context.getTransition());
            model.model.apply(this.getPose(context.getTransition()));

            stack.multiplyPositionMatrix(this.uiMatrix);

            RenderSystem.setShaderTexture(0, BBSModClient.getTextures().getTexture(texture).id);
            RenderSystem.setShaderColor(color.r, color.g, color.b, color.a);
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);

            this.renderModel(stack, model.model);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            /* Render body parts */
            this.captureMatrices(model);
            this.renderBodyParts(new FormRenderingContext(this.entity, stack, context.getTransition()));

            RenderSystem.depthFunc(GL11.GL_ALWAYS);

            stack.pop();
        }
    }

    private void renderModel(MatrixStack stack, Model model)
    {
        BufferBuilder builder = Tessellator.getInstance().getBuffer();

        builder.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

        mchorse.bbs_mod.utils.pose.MatrixStack newStack = new mchorse.bbs_mod.utils.pose.MatrixStack();

        newStack.push(stack.peek().getPositionMatrix());
        CubicRenderer.processRenderModel(new CubicCubeRenderer(), builder, newStack, model);

        BufferRenderer.drawWithGlobalProgram(builder.end());
    }

    @Override
    public void render3D(FormRenderingContext context)
    {
        this.ensureAnimator();

        CubicModel model = this.getModel();

        if (this.animator != null && model != null)
        {
            Link link = this.form.texture.get(context.getTransition());
            Link texture = link == null ? model.texture : link;
            Color color = this.form.color.get(context.getTransition());

            CubicModelAnimator.resetPose(model.model);

            this.animator.applyActions(context.entity, model.model, context.getTransition());
            model.model.apply(this.getPose(context.getTransition()));

            context.stack.multiply(RotationAxis.POSITIVE_Y.rotation(MathUtils.PI));

            RenderSystem.setShaderTexture(0, BBSModClient.getTextures().getTexture(texture).id);
            RenderSystem.setShaderColor(color.r, color.g, color.b, color.a);
            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);

            this.renderModel(context.stack, model.model);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            this.captureMatrices(model);
        }
    }

//    @Override
//    protected void handlePicking(UIRenderingContext context)
//    {
//        CubicModel model = this.form.getModel();
//
//        if (model == null || model.model == null)
//        {
//            return;
//        }
//
//        for (ModelGroup group : model.model.getOrderedGroups())
//        {
//            context.getStencil().addPicking(this.form, group.id);
//        }
//    }

    private void captureMatrices(CubicModel model)
    {
        mchorse.bbs_mod.utils.pose.MatrixStack stack = new mchorse.bbs_mod.utils.pose.MatrixStack();
        CubicMatrixRenderer renderer = new CubicMatrixRenderer(model.model);

        CubicRenderer.processRenderModel(renderer, null, stack, model.model);

        List<Matrix4f> matrices = renderer.matrices;

        for (ModelGroup group : model.model.getAllGroups())
        {
            Matrix4f matrix = new Matrix4f(matrices.get(group.index));

            matrix.translate(
                group.initial.translate.x / 16,
                group.initial.translate.y / 16,
                group.initial.translate.z / 16
            );
            matrix.rotateY(MathUtils.PI);
            this.bones.put(group.id, matrix);
        }
    }

    @Override
    public void renderBodyParts(FormRenderingContext context)
    {
        for (BodyPart part : this.form.parts.getAll())
        {
            Matrix4f matrix = this.bones.get(part.bone);

            context.stack.push();

            if (matrix != null)
            {
                context.stack.multiplyPositionMatrix(matrix);
            }
            else
            {
                context.stack.multiply(RotationAxis.POSITIVE_Y.rotation(MathUtils.PI));
            }

            // TODO: part.render(target, context);

            context.stack.pop();
        }

        this.bones.clear();
    }

    @Override
    public void collectMatrices(Entity entity, MatrixStack stack, Map<String, Matrix4f> matrices, String prefix, float transition)
    {
        stack.push();
        stack.multiplyPositionMatrix(this.form.transform.get(transition).createMatrix());

        matrices.put(prefix, new Matrix4f(stack.peek().getPositionMatrix()));

        /* Collect bones and add them to matrix list */
        CubicModel model = this.getModel();

        if (this.animator != null && model != null)
        {
            CubicModelAnimator.resetPose(model.model);

            this.animator.applyActions(new MCEntity(entity), model.model, transition);
            model.model.apply(this.getPose(transition));

            stack.multiply(RotationAxis.POSITIVE_Y.rotation(MathUtils.PI));
            this.captureMatrices(model);
        }

        for (Map.Entry<String, Matrix4f> entry : this.bones.entrySet())
        {
            stack.push();
            stack.multiplyPositionMatrix(entry.getValue());
            matrices.put(StringUtils.combinePaths(prefix, entry.getKey()), new Matrix4f(stack.peek().getPositionMatrix()));
            stack.pop();
        }

        int i = 0;

        /* Recursively do the same thing with body parts */
        for (BodyPart part : this.form.parts.getAll())
        {
            Form form = part.getForm();

            if (form != null)
            {
                Matrix4f matrix = this.bones.get(part.bone);

                stack.push();

                if (matrix != null)
                {
                    stack.multiplyPositionMatrix(matrix);
                }
                else
                {
                    stack.multiply(RotationAxis.POSITIVE_Y.rotation(MathUtils.PI));
                }

                stack.push();
                stack.multiplyPositionMatrix(part.getTransform().createMatrix());

                FormRenderer formRenderer = FormUtilsClient.getRenderer(form);

                if (formRenderer != null)
                {
                    formRenderer.collectMatrices(entity, stack, matrices, StringUtils.combinePaths(prefix, String.valueOf(i)), transition);
                }

                stack.pop();

                stack.pop();
            }

            i += 1;
        }

        stack.pop();

        this.bones.clear();
    }

    @Override
    public void tick(IEntity entity)
    {
        this.ensureAnimator();

        if (this.animator != null)
        {
            this.animator.update(entity);
        }
    }
}