package com.github.mdcdi1315.bml_lang_loader;

import net.minecraftforge.fml.ModLoadingContext;

public final class FMLBMLJavaModLoadingContext
    extends ModLoadingContext
{
    private final FMLBMLModContainer container;

    public FMLBMLJavaModLoadingContext(FMLBMLModContainer container) {
        super();
        if (container == null) {
            throw new IllegalArgumentException("container parameter is null!!");
        }
        this.container = container;
    }

    public FMLBMLModContainer getContainer() {
        return container;
    }
}
