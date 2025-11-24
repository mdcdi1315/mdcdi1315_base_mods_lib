package com.github.mdcdi1315.bml_lang_loader;

public final class FMLBMLJavaModLoadingContext
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
