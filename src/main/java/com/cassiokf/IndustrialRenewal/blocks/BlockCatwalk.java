package com.cassiokf.IndustrialRenewal.blocks;

import com.cassiokf.IndustrialRenewal.blocks.abstracts.BlockAbstractSixWayConnections;
import com.cassiokf.IndustrialRenewal.blocks.pipes.BlockEnergyCable;
import com.cassiokf.IndustrialRenewal.init.ModBlocks;
import com.cassiokf.IndustrialRenewal.init.ModItems;
import com.cassiokf.IndustrialRenewal.util.Utils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

public class BlockCatwalk extends BlockAbstractSixWayConnections {

    protected static final VoxelShape BASE_AABB = Block.box(0, 0, 0, 16, 0.5, 16);

    protected static final VoxelShape RNORTH_AABB = Block.box(0, 0, 0, 16, 16, 0.5);
    protected static final VoxelShape RSOUTH_AABB = Block.box(0, 0, 15.5, 16, 16, 16);
    protected static final VoxelShape RWEST_AABB = Block.box(0, 0, 0, 0.5, 16, 16);
    protected static final VoxelShape REAST_AABB = Block.box(15.5, 0, 0, 16, 16, 16);

    protected static final VoxelShape NORTH_AABB = Block.box(0, 0, 0, 16, 24, 0.5);
    protected static final VoxelShape SOUTH_AABB = Block.box(0, 0, 15.5, 16, 24, 16);
    protected static final VoxelShape WEST_AABB = Block.box(0, 0, 0, 0.5, 24, 16);
    protected static final VoxelShape EAST_AABB = Block.box(15.5, 0, 0, 16, 24, 16);

    public BlockCatwalk()
    {
        super(Block.Properties.of(Material.METAL).speedFactor(1.2F), 16, 2);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        for (Direction direction : Direction.values())
        {
            state = state.setValue(getPropertyBasedOnDirection(direction), canConnectTo(context.getLevel(), context.getClickedPos(), direction));
        }
        return state;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(!worldIn.isClientSide){
            if (handIn == Hand.MAIN_HAND) {
                Item playerItem = player.getMainHandItem().getItem();
                if (playerItem.equals(ModItems.screwDrive)) {
                    Utils.debug("Cycling", player.isCrouching(), player.isShiftKeyDown());

                    if(hit.getDirection() == Direction.UP && player.isShiftKeyDown())
                        state = state.cycle(getBooleanProperty(player.getDirection().getOpposite()));
                    else if (hit.getDirection() == Direction.UP)
                        state = state.cycle(getBooleanProperty(player.getDirection()));
                    else
                        state = state.cycle(getBooleanProperty(hit.getDirection()));

                    worldIn.setBlock(pos, state, 2);
//                TileEntityCatWalk te = (TileEntityCatWalk) worldIn.getTileEntity(pos);
//                if (te != null)
//                {
//                    te.toggleFacing(hit.getFace());
//                    if (!worldIn.isRemote) ItemPowerScrewDrive.playDrillSound(worldIn, pos);
//                    worldIn.setBlockState(pos, updatePostPlacement(state, hit.getFace(), null, worldIn, pos, null));
                    return ActionResultType.SUCCESS;
//                }
                }
                BlockPos posOffset = pos.relative(player.getDirection());
                BlockState stateOffset = worldIn.getBlockState(posOffset);
                BlockCatwalk catwalk = playerItem.equals(ModBlocks.CATWALK.get().asItem()) ? ModBlocks.CATWALK.get() : (playerItem.equals(ModBlocks.CATWALK_STEEL.get().asItem()) ? ModBlocks.CATWALK_STEEL.get() : null);
                Utils.debug("CATWALK", catwalk);
                if (catwalk != null) {
                    if (hit.getDirection() == Direction.UP) {
                        if (stateOffset.getMaterial().isReplaceable()) {
                            worldIn.setBlockAndUpdate(pos.relative(hit.getDirection()), catwalk.defaultBlockState());
                            worldIn.playSound(null, pos, SoundEvents.METAL_PLACE, SoundCategory.BLOCKS, 1f, 1f);
                            if (!player.isCreative()) {
                                //player.getHeldItemMainhand().shrink(1);
                                player.getMainHandItem().shrink(1);
                            }
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
//            if (playerItem.equals(BlocksRegistration.CATWALKSTAIR_ITEM.get()) || playerItem.equals(BlocksRegistration.CATWALKSTAIRSTEEL_ITEM.get()))
//            {
//                if (stateOffset.getBlock().isAir(stateOffset, worldIn, posOffset))
//                {
//                    worldIn.setBlockState(posOffset, getBlockFromItem(playerItem).getDefaultState().with(BlockCatwalkStair.FACING, player.getHorizontalFacing()), 3);
//                    if (!player.isCreative())
//                    {
//                        player.getHeldItemMainhand().shrink(1);
//                    }
//                    return ActionResultType.SUCCESS;
//                }
//            }
            }
        }
        return ActionResultType.PASS;
    }

    public BooleanProperty getBooleanProperty(Direction face){
        switch (face){
            default:
            case NORTH: return NORTH;
            case SOUTH: return SOUTH;
            case EAST: return EAST;
            case WEST: return WEST;
            case UP: return UP;
            case DOWN: return DOWN;
        }
    }

    protected boolean isValidConnection(final BlockState neighborState, final IBlockReader world, final BlockPos ownPos, final Direction neighborDirection)
    {
//        TileEntityCatWalk te = (TileEntityCatWalk) world.getTileEntity(ownPos);
//        if (te != null && te.isFacingBlackListed(neighborDirection)) return true;

        BlockState downstate = world.getBlockState(ownPos.relative(neighborDirection).below());
        Block nb = neighborState.getBlock();

        if (neighborDirection != Direction.UP && neighborDirection != Direction.DOWN)
        {
            return nb instanceof BlockCatwalk
                    || nb instanceof DoorBlock
                    //|| nb instanceof BlockElectricGate
                    || (nb instanceof StairsBlock && (neighborState.getValue(StairsBlock.FACING) == neighborDirection || neighborState.getValue(StairsBlock.FACING) == neighborDirection.getOpposite()))
                    || (downstate.getBlock() instanceof StairsBlock && downstate.getValue(StairsBlock.FACING) == neighborDirection.getOpposite())
//                    || (nb instanceof BlockCatwalkHatch && neighborState.get(BlockCatwalkHatch.FACING) == neighborDirection)
//                    || (nb instanceof BlockCatwalkGate && neighborState.get(BlockCatwalkGate.FACING) == neighborDirection.getOpposite())
//                    || (nb instanceof BlockCatwalkStair && neighborState.get(BlockCatwalkStair.FACING) == neighborDirection)
//                    || (downstate.getBlock() instanceof BlockCatwalkStair && downstate.get(BlockCatwalkStair.FACING) == neighborDirection.getOpposite())
//                    || (downstate.getBlock() instanceof BlockCatwalkLadder && downstate.get(BlockCatwalkLadder.FACING) == neighborDirection.getOpposite())
//                    || (nb instanceof BlockCatwalkLadder && neighborState.get(BlockCatwalkLadder.FACING) == neighborDirection && !neighborState.get(BlockCatwalkLadder.ACTIVE));
            ;
        }
        if (neighborDirection == Direction.DOWN)
        {
            return nb instanceof LadderBlock;
//            return nb instanceof BlockCatwalkLadder
//                    || nb instanceof LadderBlock
//                    || nb instanceof BlockIndustrialFloor || nb instanceof BlockFloorCable || nb instanceof BlockFloorPipe
//                    || nb instanceof BlockCatWalk;
        }
        return !(neighborState.getBlock() instanceof BlockEnergyCable);
    }

    @Override
    public boolean canConnectTo(IWorld worldIn, BlockPos currentPos, Direction neighborDirection) {
        final BlockPos neighborPos = currentPos.relative(neighborDirection);
        final BlockState neighborState = worldIn.getBlockState(neighborPos);

        return !isValidConnection(neighborState, worldIn, currentPos, neighborDirection);
    }

    public final boolean isConnected(final BlockState state, final Direction facing)
    {
        return state.getValue(getPropertyBasedOnDirection(facing));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape SHAPE = NULL_SHAPE;
        SHAPE = VoxelShapes.or(SHAPE, BASE_AABB);
//        if (isConnected(state, Direction.DOWN))
//        {
//            SHAPE = VoxelShapes.or(SHAPE, BASE_AABB);
//        }
//        if (isConnected(state, Direction.NORTH))
//        {
//            SHAPE = VoxelShapes.or(SHAPE, RNORTH_AABB);
//        }
//        if (isConnected(state, Direction.SOUTH))
//        {
//            SHAPE = VoxelShapes.or(SHAPE, RSOUTH_AABB);
//        }
//        if (isConnected(state, Direction.WEST))
//        {
//            SHAPE = VoxelShapes.or(SHAPE, RWEST_AABB);
//        }
//        if (isConnected(state, Direction.EAST))
//        {
//            SHAPE = VoxelShapes.or(SHAPE, REAST_AABB);
//        }
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape SHAPE = NULL_SHAPE;
        if (isConnected(state, Direction.DOWN))
        {
            SHAPE = VoxelShapes.or(SHAPE, BASE_AABB);
        }
        if (isConnected(state, Direction.NORTH))
        {
            SHAPE = VoxelShapes.or(SHAPE, NORTH_AABB);
        }
        if (isConnected(state, Direction.SOUTH))
        {
            SHAPE = VoxelShapes.or(SHAPE, SOUTH_AABB);
        }
        if (isConnected(state, Direction.WEST))
        {
            SHAPE = VoxelShapes.or(SHAPE, WEST_AABB);
        }
        if (isConnected(state, Direction.EAST))
        {
            SHAPE = VoxelShapes.or(SHAPE, EAST_AABB);
        }
        return SHAPE;
    }

    @Override
    public boolean collisionExtendsVertically(BlockState state, IBlockReader world, BlockPos pos, Entity collidingEntity)
    {
        return true;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean flag) {
        //Utils.debug("neighbor changed", state, world, pos, block, neighbor, flag);
        for (Direction face : Direction.values())
        {
            state = state.setValue(getPropertyBasedOnDirection(face), canConnectTo(world, pos, face));
        }
        world.setBlockAndUpdate(pos, state);
        super.neighborChanged(state, world, pos, block, neighbor, flag);
    }
}
