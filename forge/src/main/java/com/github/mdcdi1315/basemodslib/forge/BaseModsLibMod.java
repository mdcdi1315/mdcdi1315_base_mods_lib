package com.github.mdcdi1315.basemodslib.forge;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("mdcdi1315_base_mods_lib")
public final class BaseModsLibMod
{
    public BaseModsLibMod(FMLJavaModLoadingContext cxt)
    {
        BaseModsLib.InitializeBaseModsLibrary(new ForgeModLoaderLayer(cxt));
        if (FMLEnvironment.dist == Dist.CLIENT) {
            InitializeClientBaseModsLibMod(cxt);
        }
    }

    private static void InitializeClientBaseModsLibMod(FMLJavaModLoadingContext cxt) {
        BaseModsLibClient.InitializeBaseModsLibClient(new ForgeClientModLoaderLayer(cxt));
    }
}
