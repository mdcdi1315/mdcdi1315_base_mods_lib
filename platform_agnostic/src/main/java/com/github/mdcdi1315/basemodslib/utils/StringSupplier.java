package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.util.function.Supplier;

/**
 * Provides a {@link Supplier} for known string instances. <br />
 * Can be used by any consumer of this library, including mods. <br />
 * This is preferable to be used when something like this happens: <br />
 * <code>
 *     String astring = "my_string_contents"; <br />
 *     Supplier&lt;String&gt; string_supplier = () -> astring;
 * </code>
 */
public final class StringSupplier
        extends ElementSupplier<String>
{
    public StringSupplier(@MaybeNull String str) {
        super(str);
    }
}
