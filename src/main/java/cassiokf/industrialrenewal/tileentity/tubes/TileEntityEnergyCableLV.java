package cassiokf.industrialrenewal.tileentity.tubes;

import cassiokf.industrialrenewal.config.IRConfig;
import net.minecraft.tileentity.TileEntity;

public class TileEntityEnergyCableLV extends TileEntityEnergyCable
{
    @Override
    public int getMaxEnergyToTransport()
    {
        return IRConfig.MainConfig.Main.maxLVEnergyCableTransferAmount;
    }

    @Override
    public boolean instanceOf(TileEntity te)
    {
        return te instanceof TileEntityEnergyCableLV || te instanceof TileEntityEnergyCableLVGauge || te instanceof TileEntityCableTray;
    }
}
