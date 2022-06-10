package com.cassiokf.IndustrialRenewal.blocks.abstracts;

import com.cassiokf.IndustrialRenewal.tileentity.abstracts.TileEntity3x3x3MachineBase;
import com.cassiokf.IndustrialRenewal.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Block3x3x3Base <TE extends TileEntity3x3x3MachineBase> extends BlockAbstractHorizontalFacing{

    public static final BooleanProperty MASTER = BooleanProperty.create("master");
    public Block3x3x3Base(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(MASTER, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MASTER);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World world = context.getLevel();
        BlockPos pos =  context.getClickedPos();
        if(isValidPosition(world, pos, context.getHorizontalDirection()))
            return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(MASTER, false);
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
        return 1.0f;
        //return super.getShadeBrightness(p_220080_1_, p_220080_2_, p_220080_3_);
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        //world.setBlockAndUpdate(pos.relative(state.getValue(FACING)).above(), state.setValue(MASTER, true));
        if(!world.isClientSide)
        {
            Direction facing = state.getValue(FACING);
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            if (isValidPosition(world, pos, facing)) {
                world.setBlockAndUpdate(pos.above(), state.setValue(MASTER, true));
                for (int y = 0; y < 3; y++) {
                    for (int z = -1; z < 2; z++) {
                        for (int x = -1; x < 2; x++) {
                            //Utils.debug("checking", x, y, z, (x != 0 && y != 1 && z != 0 ));
                            if (!(x == 0 && y == 1 && z == 0)) {
                                BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                                //Utils.debug("placing", currentPos, x, y, z);
                                world.setBlockAndUpdate(currentPos, state.setValue(MASTER, false));
                                TileEntity3x3x3MachineBase te = (TileEntity3x3x3MachineBase) world.getBlockEntity(currentPos);
                                //te.getMaster();
                                //Utils.debug("Master pos", te.getMaster().getBlockPos());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void destroy(IWorld world, BlockPos pos, BlockState state) {
        //if (state.getBlock() == newState.getBlock()) return;
        if(!world.isClientSide())
        {
            List<BlockPos> blocks = Utils.getBlocksIn3x3x3Centered(pos);

            for(BlockPos blockPos : blocks){
                TileEntity te = world.getBlockEntity(blockPos);
                if(te != null){
                    if(te instanceof TileEntity3x3x3MachineBase && ((TileEntity3x3x3MachineBase)te).isMaster()){
                        ((TileEntity3x3x3MachineBase)te).breakMultiBlocks();
                        popResource((World) world, te.getBlockPos(), new ItemStack(this.asItem()));
                    }
                }
            }
        }
        super.destroy(world, pos, state);
    }

    public boolean isValidPosition(World worldIn, BlockPos pos, Direction facing)
    {
        PlayerEntity player = worldIn.getNearestPlayer(EntityPredicate.DEFAULT, pos.getX(), pos.getY(), pos.getZ());
        if (player == null) return false;
        for (int y = 0; y < 3; y++)
        {
            for (int z = -1; z < 2; z++)
            {
                for (int x = -1; x < 2; x++)
                {
                    BlockPos currentPos = new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z);
                    BlockState currentState = worldIn.getBlockState(currentPos);
                    if (!currentState.getMaterial().isReplaceable())
                        return false;
                }
            }
        }
        //Utils.debug("valid for placement");
        return true;
    }
}
