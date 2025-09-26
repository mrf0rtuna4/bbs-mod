package mchorse.bbs_mod.blocks.entities;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.data.DataStorageUtils;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.events.ModelBlockEntityUpdateCallback;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.entities.StubEntity;
import mchorse.bbs_mod.forms.forms.Form;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ModelBlockEntity extends BlockEntity
{
    private ModelProperties properties = new ModelProperties();
    private IEntity entity = new StubEntity();

    public ModelBlockEntity(BlockPos pos, BlockState state)
    {
        super(BBSMod.MODEL_BLOCK_ENTITY, pos, state);
    }

    public String getName()
    {
        BlockPos pos = this.getPos();
        Form form = this.getProperties().getForm();
        String s = "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";

        if (form != null)
        {
            s += " " + form.getDisplayName();
        }

        return s;
    }

    public ModelProperties getProperties()
    {
        return this.properties;
    }

    public IEntity getEntity()
    {
        return this.entity;
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        ModelBlockEntityUpdateCallback.EVENT.invoker().update(this);

        this.entity.update();
        this.entity.setWorld(world);
        this.properties.update(this.entity);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt()
    {
        // теперь createNbt требует RegistryWrapper
        return createNbt(this.world != null ? this.world.getRegistryManager() : RegistryWrapper.WrapperLookup.of(Stream.empty()));
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.writeNbt(nbt, registries);

        MapType data = this.properties.toData();
        DataStorageUtils.writeToNbtCompound(nbt, "Properties", data);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.readNbt(nbt, registries);

        BaseType baseType = DataStorageUtils.readFromNbtCompound(nbt, "Properties");

        if (baseType instanceof MapType mapType)
        {
            this.properties.fromData(mapType);
        }
    }

    public void updateForm(MapType data, World world)
    {
        this.properties.fromData(data);

        BlockPos pos = this.getPos();
        BlockState blockState = world.getBlockState(pos);

        world.updateListeners(pos, blockState, blockState, Block.NOTIFY_LISTENERS);
        world.markDirty(pos);
    }
}
