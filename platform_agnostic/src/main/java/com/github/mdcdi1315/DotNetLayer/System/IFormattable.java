package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Provides functionality to format the value of an object into a string representation.
 */
public interface IFormattable
{
    /**
     * Formats the value of the current instance using the specified format.
     * @param format The format to use. -or- A {@code null} reference ({@code Nothing} in Visual Basic) to use the default format defined for the type of the {@link IFormattable} implementation.
     * @param formatProvider The provider to use to format the value. -or- A null reference ({@code Nothing} in Visual Basic) to obtain the numeric format information from the current locale setting of the operating system.
     * @return The value of the current instance in the specified format.
     */
    @NotNull
    String ToString(@MaybeNull String format, @MaybeNull IFormatProvider formatProvider);
}
