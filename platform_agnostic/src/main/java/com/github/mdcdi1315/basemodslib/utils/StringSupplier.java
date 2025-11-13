package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.FormatException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import java.util.function.Supplier;
import java.util.IllegalFormatException;

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
    /**
     * Constructs a new instance of the {@link StringSupplier} class.
     * @param str The {@link String} to provide as the supplied value. Can be {@code null} as well.
     */
    public StringSupplier(@MaybeNull String str) {
        super(str);
    }

    /**
     * Creates a new instance of the {@link StringSupplier} class provided by the specified formatted string and it's arguments. <br />
     * The created string is then passed to the {@link StringSupplier} constructor.
     * @param format The string to format. Formatting rules are the same as those specified in the {@link String#format(String, Object...)} API.
     * @param format_arguments The arguments to format the string from.
     * @return A string supplier containing the result of formatting {@code format} with {@code format_arguments}.
     * @throws FormatException A formatting error has been occurred. See exception details for more information.
     * @throws ArgumentNullException {@code format} is {@code null}.
     */
    public static StringSupplier FromFormatted(String format , @MaybeNull Object... format_arguments)
        throws FormatException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(format, "format");
        try {
            return new StringSupplier(String.format(format, format_arguments));
        } catch (IllegalFormatException ife) {
            throw new FormatException(ife.getMessage());
        }
    }

    /**
     * Creates a new instance of the {@link StringSupplier} class provided by the specified formatted string and it's argument. <br />
     * The created string is then passed to the {@link StringSupplier} constructor.
     * @param format The string to format. Formatting rules are the same as those specified in the {@link String#format(String, Object...)} API.
     * @param arg_0 The first argument to format the string from.
     * @return A string supplier containing the result of formatting {@code format} with {@code arg_0}.
     * @throws FormatException A formatting error has been occurred. See exception details for more information.
     * @throws ArgumentNullException {@code format} is {@code null}.
     */
    public static StringSupplier FromFormatted(String format, Object arg_0)
        throws FormatException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(format, "format");
        try {
            return new StringSupplier(String.format(format, arg_0));
        } catch (IllegalFormatException ife) {
            throw new FormatException(ife.getMessage());
        }
    }

    /**
     * Creates a new instance of the {@link StringSupplier} class provided by the specified formatted string and it's argument. <br />
     * The created string is then passed to the {@link StringSupplier} constructor.
     * @param format The string to format. Formatting rules are the same as those specified in the {@link String#format(String, Object...)} API.
     * @param arg_0 The first argument to format the string from.
     * @param arg_1 The second argument to format the string from.
     * @return A string supplier containing the result of formatting {@code format} with {@code arg_0} and {@code arg_1}.
     * @throws FormatException A formatting error has been occurred. See exception details for more information.
     * @throws ArgumentNullException {@code format} is {@code null}.
     */
    public static StringSupplier FromFormatted(String format, Object arg_0, Object arg_1)
            throws FormatException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(format, "format");
        try {
            return new StringSupplier(String.format(format, arg_0, arg_1));
        } catch (IllegalFormatException ife) {
            throw new FormatException(ife.getMessage());
        }
    }

    /**
     * Creates a new instance of the {@link StringSupplier} class provided by the specified formatted string and it's argument. <br />
     * The created string is then passed to the {@link StringSupplier} constructor.
     * @param format The string to format. Formatting rules are the same as those specified in the {@link String#format(String, Object...)} API.
     * @param arg_0 The first argument to format the string from.
     * @param arg_1 The second argument to format the string from.
     * @param arg_2 The third argument to format the string from.
     * @return A string supplier containing the result of formatting {@code format} with {@code arg_0}, {@code arg_1} and {@code arg_2}.
     * @throws FormatException A formatting error has been occurred. See exception details for more information.
     * @throws ArgumentNullException {@code format} is {@code null}.
     */
    public static StringSupplier FromFormatted(String format, Object arg_0, Object arg_1, Object arg_2)
            throws FormatException, ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(format, "format");
        try {
            return new StringSupplier(String.format(format, arg_0, arg_1, arg_2));
        } catch (IllegalFormatException ife) {
            throw new FormatException(ife.getMessage());
        }
    }
}
