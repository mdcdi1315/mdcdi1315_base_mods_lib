package com.github.mdcdi1315.basemodslib.config.reflect.constraints;

import java.lang.annotation.Annotation;

record ConfigFieldConstraintCreator_OfStaticInstanceImpl<T extends Annotation>(IConfigFieldConstraint instance)
    implements ConfigFieldConstraintCreator<T>
{
    @Override
    public IConfigFieldConstraint apply(T t) { return instance; }

    @Override
    public IConfigFieldConstraint function(T input) { return instance; }
}
