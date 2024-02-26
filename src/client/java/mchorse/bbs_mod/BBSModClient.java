package mchorse.bbs_mod;

import mchorse.bbs_mod.client.renderer.ActorEntityRenderer;
import mchorse.bbs_mod.data.storage.DataStorage;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.graphics.texture.TextureManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

import java.io.ByteArrayOutputStream;

public class BBSModClient implements ClientModInitializer
{
    private static TextureManager textures;

    private static KeyBinding keyPlay;
    private static KeyBinding keyRecord;

    private static Recording recording = new Recording();

    public static TextureManager getTextures()
    {
        return textures;
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
                ClientPlayNetworking.send(BBSMod.PLAY_PACKET_ID, PacketByteBufs.empty());
            }

            while (keyRecord.wasPressed())
            {
                if (recording.state.isIdle())
                {
                    recording.record(client.player);
                }
                else if (recording.state.isRecording())
                {
                    recording.stop();

                    BaseType data = recording.replay.toData();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    try
                    {
                        DataStorage.writeToStream(stream, data);

                        PacketByteBuf buf = PacketByteBufs.create();
                        byte[] bytes = stream.toByteArray();

                        buf.writeByteArray(bytes);

                        ClientPlayNetworking.send(BBSMod.RECORD_PACKET_ID, buf);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        ClientTickEvents.END_WORLD_TICK.register((client) ->
        {
            recording.clientTick();
        });

        /* Entity renderers */
        EntityRendererRegistry.INSTANCE.register(BBSMod.ACTOR_ENTITY, ActorEntityRenderer::new);
    }
}