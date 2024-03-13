package mchorse.bbs_mod.forms.renderers;

import mchorse.bbs_mod.BBSData;
import mchorse.bbs_mod.forms.ITickable;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.forms.ParticleForm;
import mchorse.bbs_mod.particles.ParticleScheme;
import mchorse.bbs_mod.particles.emitter.ParticleEmitter;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.utils.joml.Vectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ParticleFormRenderer extends FormRenderer<ParticleForm> implements ITickable
{
    private ParticleEmitter emitter;
    private boolean checked;

    public ParticleFormRenderer(ParticleForm form)
    {
        super(form);
    }

    public ParticleEmitter getEmitter()
    {
        return this.emitter;
    }

    public void ensureEmitter(World world)
    {
        if (this.checked)
        {
            return;
        }

        ParticleScheme scheme = BBSData.getParticles().load(this.form.effect.get());

        if (scheme != null)
        {
            this.emitter = new ParticleEmitter();
            this.emitter.setScheme(scheme);
            this.emitter.setWorld(world);
        }

        this.checked = true;
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        this.ensureEmitter(MinecraftClient.getInstance().world);

        ParticleEmitter emitter = this.emitter;

        if (emitter != null)
        {
            MatrixStack stack = context.batcher.getContext().getMatrices();
            int scale = (y2 - y1) / 2;

            stack.push();
            stack.translate((x2 + x1) / 2, (y2 + y1) / 2, 40);
            stack.scale(scale, scale, scale);

            this.updateTexture(context.getTransition());
            emitter.lastGlobal.set(new Vector3f(0, 0, 0));
            emitter.rotation.identity();
            emitter.renderUI(stack, context.getTransition());

            stack.pop();
        }
    }

    @Override
    public void render3D(FormRenderingContext context)
    {
        this.ensureEmitter(MinecraftClient.getInstance().world);

        ParticleEmitter emitter = this.emitter;

        if (emitter != null)
        {
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

            if (camera == null)
            {
                return;
            }

            Matrix4f matrix = context.stack.peek().getPositionMatrix();
            Vector3d vector = new Vector3d().set(matrix.getTranslation(Vectors.TEMP_3F));

            this.updateTexture(context.getTransition());
            vector.add(camera.getPos().x, camera.getPos().y, camera.getPos().z);
            emitter.lastGlobal.set(vector);
            emitter.rotation.set(matrix);
            emitter.setupCameraProperties(camera);
            emitter.render(context.stack, context.getTransition());
        }
    }

    private void updateTexture(float transition)
    {
        if (this.emitter != null)
        {
            this.emitter.texture = this.form.texture.get(transition);
        }
    }

    @Override
    public void tick(IEntity entity)
    {
        this.ensureEmitter(entity.getWorld());

        if (this.emitter != null)
        {
            this.emitter.paused = this.form.paused.get();

            this.emitter.update();
        }
    }
}