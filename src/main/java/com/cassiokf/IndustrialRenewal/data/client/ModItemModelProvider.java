package com.cassiokf.IndustrialRenewal.data.client;

import com.cassiokf.IndustrialRenewal.References;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper){
        super(generator, References.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent("block_hazard", modLoc("block/block_hazard"));
        withExistingParent("caution_hazard", modLoc("block/caution_hazard"));
        withExistingParent("defective_hazard", modLoc("block/defective_hazard"));
        withExistingParent("safety_hazard", modLoc("block/safety_hazard"));
        withExistingParent("radiation_hazard", modLoc("block/radiation_hazard"));
        withExistingParent("aisle_hazard", modLoc("block/aisle_hazard"));
        withExistingParent("fire_hazard", modLoc("block/fire_hazard"));
        withExistingParent("block_steel", modLoc("block/block_steel"));
        withExistingParent("concrete", modLoc("block/concrete"));

        withExistingParent("concrete_wall", modLoc("block/concrete_wall_inventory"));

        withExistingParent("solar_panel", modLoc("block/solar_panel"));
        withExistingParent("battery_bank", modLoc("block/battery_bank"));
        withExistingParent("barrel", modLoc("block/fluid_barrel"));
        withExistingParent("portable_generator", modLoc("block/portable_generator"));
        withExistingParent("trash", modLoc("block/trash"));

        withExistingParent("small_wind_turbine_pillar", modLoc("block/small_wind_turbine_pillar"));
        withExistingParent("small_wind_turbine", modLoc("block/small_wind_turbine"));

        withExistingParent("steam_boiler", modLoc("block/steam_boiler"));
        withExistingParent("steam_turbine", modLoc("block/steam_turbine"));
        withExistingParent("mining", modLoc("block/mining_drill"));

        withExistingParent("locker", modLoc("block/locker"));
        withExistingParent("storage_chest", modLoc("block/storage/master_chest"));


        withExistingParent("ind_battery_bank", modLoc("block/battery/ind_battery_item"));
        withExistingParent("fluid_tank", modLoc("block/tank/tank_item"));

        withExistingParent("lathe", modLoc("block/lathe"));

        withExistingParent("catwalk_pillar", modLoc("block/pillar/catwalk_pillar_base"));
        withExistingParent("catwalk_steel_pillar", modLoc("block/pillar/catwalk_steel_pillar_base"));

        withExistingParent("catwalk_column", modLoc("block/catwalk_column_base"));
        withExistingParent("catwalk_column_steel", modLoc("block/catwalk_column_steel_base"));

        withExistingParent("catwalk", modLoc("block/catwalk_inventory"));
        withExistingParent("catwalk_steel", modLoc("block/catwalk_steel_inventory"));

        withExistingParent("catwalk_stair", modLoc("block/catwalk_stair_inventory"));
        withExistingParent("catwalk_stair_steel", modLoc("block/catwalk_stair_steel_inventory"));

        withExistingParent("handrail", modLoc("block/catwalk_side"));
        withExistingParent("handrail_steel", modLoc("block/catwalk_steel_side"));

        withExistingParent("catwalk_ladder", modLoc("block/catwalk_ladder_inventory"));
        withExistingParent("catwalk_ladder_steel", modLoc("block/catwalk_ladder_steel_inventory"));

        withExistingParent("frame", modLoc("block/frame_base"));
        withExistingParent("platform", modLoc("block/platform_inventory"));
        withExistingParent("scaffold", modLoc("block/scaffold_inventory"));

        withExistingParent("catwalk_gate", modLoc("block/catwalk_gate_0"));
        withExistingParent("catwalk_hatch", modLoc("block/catwalk_hatch_0"));

        withExistingParent("brace", modLoc("block/brace"));
        withExistingParent("brace_steel", modLoc("block/brace_steel"));

        withExistingParent("fence_big_column", modLoc("block/fence_big/fence_big_core_165"));
        withExistingParent("fence_big_wire", modLoc("block/fence_big_wire_item"));

        withExistingParent("electric_fence", modLoc("block/electric_fence_inventory"));
        withExistingParent("electric_gate", modLoc("block/electric_gate_base"));

        withExistingParent("razor_wire", modLoc("block/razor_wire"));

    }
}
