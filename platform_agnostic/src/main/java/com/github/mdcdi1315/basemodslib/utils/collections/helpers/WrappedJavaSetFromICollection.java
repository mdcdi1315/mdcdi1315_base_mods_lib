package com.github.mdcdi1315.basemodslib.utils.collections.helpers;

import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.ICollection;

import java.util.Set;

public class WrappedJavaSetFromICollection<T, TC extends ICollection<T>>
    extends WrappedJavaCollectionFromICollection<T, TC>
    implements Set<T>
{
    public WrappedJavaSetFromICollection(TC collection) { super(collection); }
}
