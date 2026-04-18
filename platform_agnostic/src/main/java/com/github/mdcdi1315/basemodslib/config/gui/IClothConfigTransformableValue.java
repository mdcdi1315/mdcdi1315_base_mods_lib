package com.github.mdcdi1315.basemodslib.config.gui;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

/**
 * Provides an interface for declaring custom configuration field instances to the Cloth Config API screen. <br />
 * The instances are all converted to strings and are represented as string fields at run-time.
 * @param <T> The type of the value to convert from/to a string.
 * @since 1.0.26
 */
public interface IClothConfigTransformableValue<T>
{
    /**
     * Converts a value of type {@link T} to a {@link String}.
     * @param value The value to convert.
     * @return The converted {@link String} value.
     * @throws ArgumentNullException {@code value} is {@code null}.
     */
    String AsString(T value) throws ArgumentNullException;

    /**
     * Parses a string into a new instance of the value of type {@link T}.
     * @param value The string value to convert as an instance of type {@link T}.
     * @return A new instance of type {@link T}.
     * @throws ArgumentNullException {@code value} is {@code null}.
     * @throws FormatException Can't parse the {@code value} string.
     */
    @NotNull
    T Parse(String value) throws ArgumentNullException, FormatException;

    /**
     * Gets the {@link Class} of transforming type {@link T}.
     * @return The {@link T} class.
     */
    @NotNull
    Class<T> GetTransformationClass();
}
