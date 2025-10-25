package com.github.mdcdi1315.basemodslib.forge;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("mdcdi1315_base_mods_lib")
public final class BaseModsLibMod
{
    public BaseModsLibMod(FMLJavaModLoadingContext cxt) {
        BaseModsLib.InitializeBaseModsLibrary(new ForgeModLoaderLayer(cxt));
    }
}
