package mchorse.bbs_mod;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.client.renderer.ActorEntityRenderer;
import mchorse.bbs_mod.graphics.texture.TextureManager;
import mchorse.bbs_mod.ui.TestScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.Collections;

public class BBSModClient implements ClientModInitializer
{
    private static TextureManager textures;

    private static KeyBinding keyPlay;
    private static KeyBinding keyRecord;

    public static TextureManager getTextures()
    {
        return textures;
    }

    public static void handleCameraASM()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.gameRenderer.getCamera();
        Entity oldEntity = camera.getFocusedEntity();
        ClientWorld world = mc.world;
        LivingEntity entity = new LivingEntity(BBSMod.ACTOR_ENTITY, world)
        {
            @Override
            public Iterable<ItemStack> getArmorItems()
            {
                return Collections.emptyList();
            }

            @Override
            public ItemStack getEquippedStack(EquipmentSlot slot)
            {
                return ItemStack.EMPTY;
            }

            @Override
            public void equipStack(EquipmentSlot slot, ItemStack stack)
            {}

            @Override
            public Arm getMainArm()
            {
                return Arm.RIGHT;
            }
        };

        entity.setPosition(0, -59, -5);
        entity.lastRenderX = 0;
        entity.lastRenderY = -59;
        entity.lastRenderZ = -5;
        entity.prevX = 0;
        entity.prevY = -59;
        entity.prevZ = -5;
        entity.setYaw(0F);
        entity.prevYaw = 0F;
        entity.setHeadYaw(0F);
        entity.prevHeadYaw = 0F;
        entity.setBodyYaw(0F);
        entity.prevBodyYaw = 0F;
        entity.setPitch(0F);
        entity.prevPitch = 0F;

        camera.update(world, entity, false, false, mc.getTickDelta());

        try
        {
            Field focusedEntity = camera.getClass().getDeclaredField("focusedEntity");

            focusedEntity.setAccessible(true);
            focusedEntity.set(camera, oldEntity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitializeClient()
    {
        textures = new TextureManager(BBSMod.getProvider());

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

        ClientTickEvents.END_CLIENT_TICK.register((client) ->
        {
            while (keyPlay.wasPressed())
            {
                MinecraftClient.getInstance().setScreen(new TestScreen(Text.literal("Hello")));
            }

            while (keyRecord.wasPressed())
            {
                // ...
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
            MatrixStack matrixStack = listener.matrixStack();
            Vec3d pos = listener.camera().getPos();

            matrixStack.push();
            matrixStack.translate(0 - pos.x, (-58) - pos.y, 0 - pos.z);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            matrixStack.multiply(listener.camera().getRotation());
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            MatrixStack.Entry entry = matrixStack.peek();
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            VertexConsumer vertexConsumer = listener.consumers().getBuffer(RenderLayer.getEntityCutout(new Identifier("bbs:icon.png")));
            vertex(vertexConsumer, matrix4f, matrix3f, LightmapTextureManager.pack(15, 15), 0.0f, 0, 0, 1);
            vertex(vertexConsumer, matrix4f, matrix3f, LightmapTextureManager.pack(15, 15), 1.0f, 0, 1, 1);
            vertex(vertexConsumer, matrix4f, matrix3f, LightmapTextureManager.pack(15, 15), 1.0f, 1, 1, 0);
            vertex(vertexConsumer, matrix4f, matrix3f, LightmapTextureManager.pack(15, 15), 0.0f, 1, 0, 0);
            matrixStack.pop();
        });
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v)
    {
        buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }
}