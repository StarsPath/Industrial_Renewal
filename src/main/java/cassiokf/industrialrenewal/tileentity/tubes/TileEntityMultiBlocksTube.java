package cassiokf.industrialrenewal.tileentity.tubes;

import cassiokf.industrialrenewal.tileentity.TileEntitySyncable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class TileEntityMultiBlocksTube<TE extends TileEntityMultiBlocksTube> extends TileEntitySyncable implements ICapabilityProvider
{
    private TE master;
    private boolean isMaster;
    private Map<BlockPos, EnumFacing> posSet = new ConcurrentHashMap<>();
    public Map<BlockPos, Integer> limitedOutPutMap = new ConcurrentHashMap<>();
    public int outPut;
    int outPutCount;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void onLoad()
    {
        initializeMultiblockIfNecessary();
    }

    public int getOutPut()
    {
        return outPut;
    }

    public int getOutPutCount()
    {
        return outPutCount;
    }

    private void initializeMultiblockIfNecessary()
    {
        if (isMasterInvalid() && !world.isRemote)
        {
            if (isTray()) return;
            List<TileEntityMultiBlocksTube> connectedCables = new CopyOnWriteArrayList<>();
            Stack<TileEntityMultiBlocksTube> traversingCables = new Stack<>();
            TE master = (TE) this;
            traversingCables.add(this);
            while (!traversingCables.isEmpty())
            {
                TileEntityMultiBlocksTube storage = traversingCables.pop();
                if (storage.isMaster())
                {
                    master = (TE) storage;
                }
                connectedCables.add(storage);
                for (EnumFacing d : getFacesToCheck())
                {
                    TileEntity te = world.getTileEntity(storage.getPos().offset(d));
                    if (instanceOf(te) && !connectedCables.contains(te))
                    {
                        traversingCables.add((TE) te);
                    }
                }
            }
            master.getMachinesPosSet().clear();
            if (canBeMaster(master))
            {
                for (TileEntityMultiBlocksTube storage : connectedCables)
                {
                    if (!canBeMaster(storage)) continue;
                    storage.setMaster((TE) master);
                    storage.checkForOutPuts(storage.getPos());
                    storage.markDirty();
                }
            } else
            {
                for (TileEntityMultiBlocksTube storage : connectedCables)
                {
                    if (!canBeMaster(storage)) continue;
                    storage.getMachinesPosSet().clear();
                    storage.setMaster(null);
                }
            }
            Sync();
        }
    }

    public int getLimitedValueForOutPut(int value, int maxOutPut, BlockPos storagePos, boolean simulate)
    {
        if (!limitedOutPutMap.containsKey(storagePos))
        {
            if (!simulate) limitedOutPutMap.put(storagePos, value);
            return value;
        }
        int currentValue = limitedOutPutMap.get(storagePos);
        int maxValue = maxOutPut - currentValue;
        maxValue = Math.min(value, maxValue);
        if (!simulate) limitedOutPutMap.put(storagePos, currentValue + maxValue);
        return maxValue;
    }

    public boolean isTray()
    {
        return false;
    }

    private boolean canBeMaster(TileEntity te)
    {
        return te != null && !(te instanceof TileEntityCableTray);
    }

    public boolean isMasterInvalid()
    {
        return master == null || master.isInvalid();
    }

    public EnumFacing[] getFacesToCheck()
    {
        return EnumFacing.values();
    }

    public abstract boolean instanceOf(TileEntity te);

    public abstract void checkForOutPuts(BlockPos bPos);

    public boolean isMaster()
    {
        return isMaster;
    }

    public TE getMaster()
    {
        initializeMultiblockIfNecessary();
        if (master != null && !master.isMaster()) Sync();
        return master != null ? master : (TE) this;
    }

    public void setMaster(TE master)
    {
        this.master = master;
        isMaster = master == this;
        if (!isMaster) posSet.clear();
    }

    public Map<BlockPos, EnumFacing> getMachinesPosSet()
    {
        return posSet;
    }

    @Override
    public void invalidate()
    {
        super.invalidate();

        if (master != null)
        {
            master.setMaster(null);
            if (master != null) master.getMaster();
            else getMaster();
        }

        for (EnumFacing d : EnumFacing.VALUES)
        {
            TileEntity te = world.getTileEntity(pos.offset(d));
            if (instanceOf(te))
            {
                ((TileEntityMultiBlocksTube) te).master = null;

                if (te instanceof TileEntityCableTray)
                    ((TileEntityCableTray) te).refreshConnections();
                else
                    ((TileEntityMultiBlocksTube) te).initializeMultiblockIfNecessary();
            }
        }
    }

    public void addMachine(BlockPos pos, EnumFacing face)
    {
        if (!isMaster()) getMaster().addMachine(pos, face);
        posSet.put(pos, face);
    }

    public void removeMachine(BlockPos ownPos, BlockPos machinePos)
    {
        if (!isMaster()) getMaster().removeMachine(ownPos, machinePos);
        posSet.remove(machinePos);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        isMaster = compound.getBoolean("isMaster");
        if (hasWorld() && world.isRemote)
        {
            TE te = (TE) world.getTileEntity(BlockPos.fromLong(compound.getLong("masterPos")));
            if (te != null) master = te;
        }
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setBoolean("isMaster", isMaster);
        if (master != null && !master.isInvalid()) compound.setLong("masterPos", master.getPos().toLong());
        return super.writeToNBT(compound);
    }
}
