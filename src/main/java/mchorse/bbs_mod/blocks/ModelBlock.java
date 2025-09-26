package mchorse.bbs_mod.blocks;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.network.ServerNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class ModelBlock extends Block implements BlockEntityProvider, Waterloggable
{
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> validateTicker(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker)
    {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    public ModelBlock(Settings settings)
    {
        super(settings);

        this.setDefaultState(getDefaultState()
            .with(Properties.WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(Properties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState()
            .with(Properties.WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state)
    {
        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof ModelBlockEntity modelBlock)
        {
            ItemStack stack = new ItemStack(this);
            NbtCompound compound = new NbtCompound();

            compound.put("BlockEntityTag", modelBlock.createFromNbt());
            stack.set(DataComponentTypes.CUSTOM_DATA, compound);


            return stack;
        }

        return super.getPickStack(world, pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos)
    {
        return true;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        if (world.isClient())
        {
            return validateTicker(type, BBSMod.MODEL_BLOCK_ENTITY, (theWorld, blockPos, blockState, blockEntity) -> blockEntity.tick(theWorld, blockPos, blockState));
        }

        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new ModelBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (hand == Hand.MAIN_HAND)
        {
            if (player instanceof ServerPlayerEntity serverPlayer)
            {
                ServerNetwork.sendClickedModelBlock(serverPlayer, pos);
            }

            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    /* Waterloggable implementation */

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity be, ItemStack tool)
    {
        if (!world.isClient && !player.getAbilities().creativeMode)
        {
            if (be instanceof ModelBlockEntity model)
            {
                ItemStack stack = new ItemStack(this);
                NbtCompound wrapper = new NbtCompound();

                wrapper.put("BlockEntityTag", model.createNbtWithId());
                stack.setNbt(wrapper);

                ItemScatterer.spawn(world, pos, DefaultedList.ofSize(1, stack));
            }
        }

        super.afterBreak(world, player, pos, state, be, tool);
    }
}