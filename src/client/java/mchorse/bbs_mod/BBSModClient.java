package mchorse.bbs_mod;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.audio.SoundManager;
import mchorse.bbs_mod.camera.Camera;
import mchorse.bbs_mod.camera.clips.ClipFactoryData;
import mchorse.bbs_mod.camera.clips.misc.AudioClientClip;
import mchorse.bbs_mod.camera.controller.CameraController;
import mchorse.bbs_mod.client.renderer.ActorEntityRenderer;
import mchorse.bbs_mod.forms.categories.FormCategories;
import mchorse.bbs_mod.graphics.FramebufferManager;
import mchorse.bbs_mod.graphics.texture.TextureManager;
import mchorse.bbs_mod.l10n.L10n;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.dashboard.UIDashboard;
import mchorse.bbs_mod.ui.framework.UIScreen;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.ui.utils.keys.KeybindSettings;
import mchorse.bbs_mod.utils.RayTracing;
import mchorse.bbs_mod.utils.colors.Colors;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.Collections;

public class BBSModClient implements ClientModInitializer
{
    private static TextureManager textures;
    private static FramebufferManager framebuffers;
    private static SoundManager sounds;
    private static L10n l10n;

    private static FormCategories formCategories;

    private static KeyBinding keyPlay;
    private static KeyBinding keyRecord;

    private static CameraController cameraController = new CameraController();
    public static boolean lockCamera = false;

    public static TextureManager getTextures()
    {
        return textures;
    }

    public static FramebufferManager getFramebuffers()
    {
        return framebuffers;
    }

    public static SoundManager getSounds()
    {
        return sounds;
    }

    public static L10n getL10n()
    {
        return l10n;
    }

    public static FormCategories getFormCategories()
    {
        return formCategories;
    }

    public static CameraController getCameraController()
    {
        return cameraController;
    }

    @Override
    public void onInitializeClient()
    {
        textures = new TextureManager(BBSMod.getProvider());
        framebuffers = new FramebufferManager();
        sounds = new SoundManager(BBSMod.getProvider());
        l10n = new L10n();
        l10n.register((lang) -> Collections.singletonList(Link.assets("strings/" + lang + ".json")));
        l10n.reload();

        formCategories = new FormCategories();
        formCategories.setup();

        KeybindSettings.registerClasses();

        BBSMod.setupConfig(Icons.KEY_CAP, "keybinds", new File(BBSMod.getSettingsFolder(), "keybinds.json"), KeybindSettings::register);
        BBSData.load(BBSMod.getDataFolder());

        BBSSettings.tooltipStyle.modes(
            UIKeys.ENGINE_TOOLTIP_STYLE_LIGHT,
            UIKeys.ENGINE_TOOLTIP_STYLE_DARK
        );

        BBSSettings.keystrokeMode.modes(
            UIKeys.ENGINE_KEYSTROKES_POSITION_AUTO,
            UIKeys.ENGINE_KEYSTROKES_POSITION_BOTTOM_LEFT,
            UIKeys.ENGINE_KEYSTROKES_POSITION_BOTTOM_RIGHT,
            UIKeys.ENGINE_KEYSTROKES_POSITION_TOP_RIGHT,
            UIKeys.ENGINE_KEYSTROKES_POSITION_TOP_LEFT
        );

        /* Replace audio clip with client version that plays audio */
        BBSMod.getFactoryCameraClips()
            .register(Link.bbs("audio"), AudioClientClip.class, new ClipFactoryData(Icons.SOUND, 0xffc825));

        /* Keybind shenanigans */
        keyPlay = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key." + BBSMod.MOD_ID + ".play",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category." + BBSMod.MOD_ID + ".test"
        ));

        keyRecord = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key." + BBSMod.MOD_ID + ".record",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category." + BBSMod.MOD_ID + ".test"
        ));

        WorldRenderEvents.START.register((client) ->
        {
            cameraController.setup(new Camera(), client.tickDelta());
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) ->
        {
            if (MinecraftClient.getInstance().currentScreen instanceof UIScreen screen)
            {
                screen.update();
            }

            cameraController.update();

            while (keyPlay.wasPressed())
            {
                MinecraftClient.getInstance().setScreen(new UIScreen(Text.literal("Dashboard"), new UIDashboard()));
            }

            while (keyRecord.wasPressed())
            {
                MinecraftClient mc = MinecraftClient.getInstance();

                HitResult result = RayTracing.rayTraceEntity(mc.player, mc.world, mc.cameraEntity.getEyePos(), mc.cameraEntity.getRotationVec(0F), 64);

                if (result != null && result.getType() != HitResult.Type.MISS)
                {
                    Vec3d pos = result.getPos();

                    mc.world.addParticle(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 0D, 0D, 0D);
                }
                else
                {
                    mc.player.sendMessage(Text.literal("I guess you have missed, huh?"));
                }
            }
        });

        registerHUDRender();
        registerWorldRenderer();

        /* Entity renderers */
        EntityRendererRegistry.register(BBSMod.ACTOR_ENTITY, ActorEntityRenderer::new);
    }

    private void registerHUDRender()
    {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->
        {
            // Get the transformation matrix from the matrix stack, alongside the tessellator instance and a new buffer builder.
            Matrix4f transformationMatrix = drawContext.getMatrices().peek().getPositionMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            // Initialize the buffer using the specified format and draw mode.
            buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR_TEXTURE);

            // Write our vertices, Z doesn't really matter since it's on the HUD.
            buffer.vertex(transformationMatrix, 20, 20, 5).color(0xffffffff).texture(1F, 0F).next();
            buffer.vertex(transformationMatrix, 5, 40, 5).color(0xffffffff).texture(0F, 0F).next();
            buffer.vertex(transformationMatrix, 35, 40, 5).color(0xffffffff).texture(1F, 1F).next();
            buffer.vertex(transformationMatrix, 20, 60, 5).color(0xffffffff).texture(0F, 1F).next();

            // We'll get to this bit in the next section.
            RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, new Identifier("bbs:icon.png"));

            // Draw the buffer onto the screen.
            tessellator.draw();
        });
    }

    private void registerWorldRenderer()
    {
        WorldRenderEvents.BEFORE_ENTITIES.register((listener) ->
        {
            MatrixStack stack = listener.matrixStack();
            Vec3d pos = listener.camera().getPos();

            stack.push();
            stack.translate(0 - pos.x, (-58) - pos.y, 0 - pos.z);
            stack.scale(0.5f, 0.5f, 0.5f);
            stack.multiply(listener.camera().getRotation());
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            MatrixStack.Entry entry = stack.peek();
            Matrix4f mv = entry.getPositionMatrix();
            Matrix3f normal = entry.getNormalMatrix();
            VertexConsumer vertexConsumer = listener.consumers().getBuffer(RenderLayer.getEntityCutout(new Identifier("bbs:icon.png")));
            vertex(vertexConsumer, mv, normal, LightmapTextureManager.pack(15, 15), 0.0f, 0, 0, 1);
            vertex(vertexConsumer, mv, normal, LightmapTextureManager.pack(15, 15), 1.0f, 0, 1, 1);
            vertex(vertexConsumer, mv, normal, LightmapTextureManager.pack(15, 15), 1.0f, 1, 1, 0);
            vertex(vertexConsumer, mv, normal, LightmapTextureManager.pack(15, 15), 0.0f, 1, 0, 0);
            stack.pop();
        });
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v)
    {
        buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(Colors.WHITE).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }
}