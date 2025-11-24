package com.github.mdcdi1315.bml_lang_loader;

import java.util.List;
import java.util.ArrayList;

public final class BMLWrappedModObject
{
    public final List<Object> instances;

    public BMLWrappedModObject() {
        this.instances = new ArrayList<>();
    }

    public void AddInstance(Object obj) {
        this.instances.add(obj);
    }
}
