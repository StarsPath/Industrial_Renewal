package com.cassiokf.IndustrialRenewal.blocks;

import com.cassiokf.IndustrialRenewal.blocks.abstracts.BlockSaveContent;
import com.cassiokf.IndustrialRenewal.init.ModTileEntities;
import com.cassiokf.IndustrialRenewal.tileentity.TileEntityBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BlockBarrel extends BlockSaveContent {

    public static final BooleanProperty FRAME = BooleanProperty.create("frame");

    public BlockBarrel(Properties props) {
        super(props);
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(FRAME, false));
    }
    public BlockBarrel() {
        super(Properties.of(Material.METAL).strength(2f));
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(FRAME, false));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context)
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(FRAME, context.getPlayer().isCrouching());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FRAME, FACING);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityBarrel(ModTileEntities.BARREL_TILE.get());
    }
}
