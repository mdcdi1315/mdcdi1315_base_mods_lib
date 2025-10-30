package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Defines a method that supports custom formatting of the value of an object.
 */
public interface ICustomFormatter
{
    /**
     * Converts the value of a specified object to an equivalent string representation using specified format and culture-specific formatting information.
     * @param format A format string containing formatting specifications.
     * @param arg An object to format.
     * @param formatProvider An object that supplies format information about the current instance.
     * @return The string representation of the value of {@code arg}, formatted as specified by {@code format} and {@code formatProvider}.
     */
    @NotNull
    String Format(@MaybeNull String format, @MaybeNull Object arg, @MaybeNull IFormatProvider formatProvider);
}
