package com.github.mdcdi1315.basemodslib.forge;

import com.github.mdcdi1315.DotNetLayer.System.Func1;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.IModLoaderLayer;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.IClientModLoaderLayer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BaseModsLib.MOD_ID)
public final class BaseModsLibMod
{
    private record ServerModLoaderLayerInitializer(FMLJavaModLoadingContext context)
            implements Func1<IModLoaderLayer>
    {
        @Override
        public IModLoaderLayer function() {
            return new ForgeModLoaderLayer(context);
        }
    }

    private record ClientModLoaderLayerInitializer(FMLJavaModLoadingContext context)
            implements Func1<IClientModLoaderLayer>
    {
        @Override
        public IClientModLoaderLayer function() {
            return new ForgeClientModLoaderLayer(context);
        }
    }

    public BaseModsLibMod(FMLJavaModLoadingContext cxt)
    {
        BaseModsLib.InitializeBaseModsLibrary(new ServerModLoaderLayerInitializer(cxt));
        if (FMLEnvironment.dist == Dist.CLIENT) {
            InitializeClientBaseModsLibMod(cxt);
        }
    }

    private static void InitializeClientBaseModsLibMod(FMLJavaModLoadingContext cxt) {
        BaseModsLibClient.InitializeBaseModsLibClient(new ClientModLoaderLayerInitializer(cxt));
    }
}
