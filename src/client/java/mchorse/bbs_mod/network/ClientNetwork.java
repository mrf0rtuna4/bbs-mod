package mchorse.bbs_mod.network;

import mchorse.bbs_mod.data.DataStorageUtils;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.FormUtils;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.ModelForm;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class ClientNetwork
{
    public static void setup()
    {
        ClientPlayNetworking.registerGlobalReceiver(ServerNetwork.CLIENT_CLICKED_MODEL_BLOCK_PACKET, (client, handler, buf, responseSender) ->
        {
            BlockPos pos = buf.readBlockPos();

            client.execute(() ->
            {
                // Open dashboard with editing the model block
                System.out.println("What's up! " + pos);

                ModelForm form = new ModelForm();

                form.model.set("butterfly");

                sendModelBlockForm(pos, form);
            });
        });
    }

    public static void sendModelBlockForm(BlockPos pos, Form form)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        MapType mapType = FormUtils.toData(form);

        buf.writeBlockPos(pos);
        DataStorageUtils.writeToPacket(buf, mapType == null ? new MapType() : mapType);

        ClientPlayNetworking.send(ServerNetwork.SERVER_MODEL_BLOCK_FORM_PACKET, buf);
    }
}