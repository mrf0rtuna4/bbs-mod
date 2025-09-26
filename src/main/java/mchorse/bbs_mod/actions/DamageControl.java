package mchorse.bbs_mod.actions;

import mchorse.bbs_mod.BBSSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class DamageControl
{
    private List<BlockCapture> blocks = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();

    private ServerWorld world;

    public int nested;
    public boolean enable;

    public DamageControl(ServerWorld world)
    {
        this.world = world;
        this.enable = BBSSettings.damageControl.get();
    }

    public void addBlock(BlockPos pos, BlockState state, BlockEntity entity)
    {
        if (!this.enable)
        {
            return;
        }

        for (int i = 0; i < this.blocks.size(); i++)
        {
            BlockCapture blockCapture = this.blocks.get(i);

            if (blockCapture.pos.equals(pos))
            {
                return;
            }
        }

        RegistryWrapper.WrapperLookup lookup = this.world.getRegistryManager();
        NbtCompound tag = entity == null ? null : entity.createNbtWithId(lookup);
    }

    public void addEntity(Entity entity)
    {
        if (!this.enable)
        {
            return;
        }

        this.entities.add(entity);
    }

    public void restore()
    {
        for (BlockCapture block : this.blocks)
        {
            this.world.setBlockState(block.pos, block.lastState, 2);

            if (block.blockEntity != null)
            {
                RegistryWrapper.WrapperLookup registries = world.getRegistryManager();;
                BlockEntity blockEntity = BlockEntity.createFromNbt(block.pos, block.lastState, block.blockEntity, registries);

                if (blockEntity != null)
                {
                    this.world.addBlockEntity(blockEntity);
                }
            }
        }

        for (Entity entity : this.entities)
        {
            if (!entity.isRemoved())
            {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }

        this.blocks.clear();
        this.entities.clear();
    }

    private static class BlockCapture
    {
        public BlockPos pos;
        public BlockState lastState;
        public NbtCompound blockEntity;

        public BlockCapture(BlockPos pos, BlockState lastState, NbtCompound blockEntity)
        {
            this.pos = pos;
            this.lastState = lastState;
            this.blockEntity = blockEntity;
        }
    }
}