package com.cassiokf.IndustrialRenewal.blocks;

import com.cassiokf.IndustrialRenewal.blocks.abstracts.BlockAbstractSixWayConnections;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Objects;

public class BlockPillar extends BlockAbstractSixWayConnections {


    public BlockPillar()
    {
        super(Block.Properties.of(Material.METAL), 8, 16);
    }

    @Override
    public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext context) {
        if(!context.getPlayer().isCrouching())
            return context.getItemInHand().getItem() == this.asItem();
        return super.canBeReplaced(p_196253_1_, context);
    }

    @Override
    public boolean canConnectTo(IWorld worldIn, BlockPos currentPos, Direction neighborDirection) {
        final BlockPos neighborPos = currentPos.relative(neighborDirection);
        final BlockState neighborState = worldIn.getBlockState(neighborPos);
        Block nb = neighborState.getBlock();
        if (neighborDirection != Direction.UP && neighborDirection != Direction.DOWN)
        {
            return nb instanceof LeverBlock
//                    || (nb instanceof BlockHVIsolator && neighborState.get(BlockHVIsolator.FACING) == neighborDirection.getOpposite())
                    || nb instanceof RedstoneTorchBlock
                    || nb instanceof TripWireHookBlock
                    || nb instanceof BlockColumn
//                    || (nb instanceof BlockCableTray && neighborState.get(BlockCableTray.BASE).equals(EnumBaseDirection.byIndex(neighborDirection.getOpposite().getIndex())))
                    || nb instanceof LadderBlock
//                    || (nb instanceof BlockLight && neighborState.get(BlockLight.FACING) == neighborDirection.getOpposite())
//                    || nb instanceof BlockRoof
                    || (nb instanceof BlockBrace && Objects.equals(neighborState.getValue(BlockBrace.FACING).getName(), neighborDirection.getOpposite().getName()))
                    || (nb instanceof BlockBrace && Objects.equals(neighborState.getValue(BlockBrace.FACING).getName(), "down_" + neighborDirection.getName()))
//                    || (nb instanceof BlockAlarm && neighborState.get(BlockAlarm.FACING) == neighborDirection)
//                    || (nb instanceof BlockSignBase && neighborState.get(BlockSignBase.ONWALL) && Objects.equals(neighborState.get(BlockSignBase.FACING).getName(), neighborDirection.getOpposite().getName()))
//                    || Objects.requireNonNull(nb.getRegistryName()).toString().matches("immersiveengineering:connector")
//                    || Objects.requireNonNull(nb.getRegistryName()).toString().matches("immersiveengineering:metal_decoration2")
//                    || Objects.requireNonNull(nb.getRegistryName()).toString().matches("immersiveengineering:wooden_device1")
//                    || Objects.requireNonNull(nb.getRegistryName()).toString().matches("immersiveengineering:metal_device1")
//                    //start Industrial floor side connection
//                    || nb instanceof BlockIndustrialFloor || nb instanceof BlockFloorLamp
//                    || nb instanceof BlockFloorPipe || nb instanceof BlockFloorCable;
            ;
            //end
        }
        if (neighborDirection == Direction.DOWN)
        {
            return Block.canSupportRigidBlock(worldIn, neighborPos);
        }
        return Block.canSupportRigidBlock(worldIn, neighborPos) || nb instanceof BlockCatwalk;
        //return false;
//        return neighborState.isSolid() || nb instanceof BlockIndustrialFloor || nb instanceof BlockFloorLamp
//                || nb instanceof BlockFloorPipe || nb instanceof BlockFloorCable || nb instanceof BlockCatWalk;
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
