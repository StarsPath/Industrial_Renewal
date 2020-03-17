package cassiokf.industrialrenewal.tileentity;

import cassiokf.industrialrenewal.blocks.BlockDamIntake;
import cassiokf.industrialrenewal.util.Utils;
import cassiokf.industrialrenewal.util.interfaces.ICompressedFluidCapability;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.List;

public class TileEntityDamIntake extends TileEntity implements ITickable, ICompressedFluidCapability
{
    int waterAmount = -1;
    List<BlockPos> connectedWalls = new ArrayList<BlockPos>();
    BlockPos neighborPos = null;
    private EnumFacing facing = null;

    @Override
    public void onLoad()
    {
        initializeMultiblockIfNecessary();
    }

    @Override
    public void update()
    {
        if (!world.isRemote && waterAmount > 0)
        {
            TileEntity te = world.getTileEntity(getOutPutPos());
            if (te instanceof ICompressedFluidCapability
                    && ((ICompressedFluidCapability) te).canAccept(getBlockFacing().getOpposite(), getOutPutPos()))
            {
                ((ICompressedFluidCapability) te).passCompressedFluid(waterAmount, pos.getY(), false);
            }
        }
    }

    private BlockPos getOutPutPos()
    {
        if (neighborPos != null) return neighborPos;
        return neighborPos = pos.offset(getBlockFacing().getOpposite());
    }

    @Override
    public boolean canAccept(EnumFacing face, BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean canPipeConnect(EnumFacing face, BlockPos pos)
    {
        return face.equals(getBlockFacing().getOpposite());
    }

    @Override
    public int passCompressedFluid(int amount, int y, boolean simulate)
    {
        return 0;
    }

    public int getWaterAmount()
    {
        initializeMultiblockIfNecessary();
        return waterAmount;
    }

    private void initializeMultiblockIfNecessary()
    {
        if (world.isRemote) return;
        if (waterAmount < 0)
        {
            connectedWalls.clear();
            connectedWalls.add(pos);

            EnumFacing facing = getBlockFacing();
            for (int i = -6; i <= 6; i++)
            {
                for (int y = 0; y <= 10; y++)
                {
                    BlockPos cPos = (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) ?
                            (new BlockPos(pos.getX() + i, pos.getY() + y, pos.getZ())) :
                            (new BlockPos(pos.getX(), pos.getY() + y, pos.getZ() + i));

                    TileEntity te = world.getTileEntity(cPos);
                    if (te instanceof TileEntityConcrete && !((TileEntityConcrete) te).isUsed())
                    {
                        ((TileEntityConcrete) te).setUsed(true);
                        connectedWalls.add(cPos);
                    }
                }
            }
            waterAmount = 0;
            for (BlockPos wall : connectedWalls)
            {
                int f = 1;
                while (world.getBlockState(wall.offset(facing, f)).getMaterial() == Material.WATER
                        && world.getBlockState(wall.offset(facing, f)).getValue(BlockFluidBase.LEVEL) == 0
                        && f <= 10)
                {
                    f++;
                }
                waterAmount += f - 1;
            }
            waterAmount = (int) (Utils.normalize(waterAmount, 0, 1440) * Fluid.BUCKET_VOLUME);
        }
    }

    @Override
    public void invalidate()
    {
        for (BlockPos wall : connectedWalls)
        {
            TileEntity te = world.getTileEntity(wall);
            if (te instanceof TileEntityConcrete) ((TileEntityConcrete) te).setUsed(false);
        }
        super.invalidate();
    }

    private EnumFacing getBlockFacing()
    {
        if (facing != null) return facing;
        IBlockState state = world.getBlockState(pos);
        return facing = state.getBlock() instanceof BlockDamIntake ? state.getValue(BlockDamIntake.FACING) : EnumFacing.NORTH;
    }
}
