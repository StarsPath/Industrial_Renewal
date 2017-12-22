package cassiokf.industrialrenewal.blocks;

import cassiokf.industrialrenewal.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnergyCable extends BlockPipeBase implements IEnergyStorage {

    public BlockEnergyCable(String name) {
        super(name);
    }

    @Override
    protected boolean isValidConnection(final IBlockState ownState, final IBlockState neighbourState, final IBlockAccess world, final BlockPos ownPos, final EnumFacing neighbourDirection) {
        // Connect if the neighbouring block is another pipe
        //if (super.isValidConnection(ownState, neighbourState, world, ownPos, neighbourDirection)) {
        //    return true;
        //}

        final BlockPos neighbourPos = ownPos.offset(neighbourDirection);
        final Block neighbourBlock = neighbourState.getBlock();

        if (neighbourBlock.hasTileEntity(neighbourState)) {
            final TileEntity tileEntity = world.getTileEntity(neighbourPos);
            return tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, neighbourDirection.getOpposite());
        }

        return neighbourBlock instanceof IEnergyStorage;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (entity.inventory.getCurrentItem().getItem() == ModItems.screwDrive) {
            world.playSound(null, (double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("industrialrenewal:drill")), SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.setBlockToAir(new BlockPos(i, j, k));
            if (!entity.isCreative()) {
                entity.inventory.addItemStackToInventory(new ItemStack(net.minecraft.item.ItemBlock.getItemFromBlock(ModBlocks.energyCable), 1));
            }
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    //IEnergyStorage
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 1000;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 1000;
    }

    @Override
    public int getEnergyStored() {
        return 1000;
    }

    @Override
    public int getMaxEnergyStored() {
        return 1000;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
