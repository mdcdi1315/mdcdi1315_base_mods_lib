package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.ShouldBeNamedAs;
import com.github.mdcdi1315.DotNetLayer.ByRefParameterType;
import com.github.mdcdi1315.DotNetLayer.DotNetByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImpl;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImplOptions;

/**
 * Provides utilities around the {@link String} type. <br />
 * Normally, this should be named as String, but that would cause many naming issues which I do not want to run into.
 */
@ShouldBeNamedAs("String")
public final class StringUtils
{
    private StringUtils() {}

    private static String GetEmpty() { return ""; }

    /**
     * Represents the empty string. This field is read-only.
     */
    @NotNull
    public static final String Empty = GetEmpty();

    /**
     * Indicates whether the specified string is {@code null} or an empty string ("").
     * @param value The string to test.
     * @return {@code true} if the {@code value} parameter is {@code null} or an empty string (""); otherwise, {@code false}.
     */
    @SuppressWarnings("StringEquality")
    public static boolean IsNullOrEmpty(@AllowNull String value)
    {
        // value == Empty: We want reference check instead.
        return value == null || value == Empty || value.isEmpty();
    }

    /**
     * Indicates whether the specified string is {@code null}, empty, or consists only of white-space characters.
     * @param value The string to test.
     * @return {@code true} if the {@code value} parameter is {@code null} or {@link #Empty}, or if {@code value} consists exclusively of white-space characters.
     */
    @SuppressWarnings("StringEquality")
    public static boolean IsNullOrWhiteSpace(@AllowNull String value)
    {
        // value == Empty: We want reference check instead.
        return value == null || value == Empty || value.isBlank();
    }

    /**
     * Retrieves an object that can iterate through the individual characters in this string.
     * @param value The string instance.
     * @return An enumerator object.
     * @throws NullReferenceException {@code value} is {@code null}.
     */
    @NotNull
    public static CharEnumerator GetEnumerator(String value)
            throws NullReferenceException
    {
        if (value == null) {
            throw new NullReferenceException();
        } else {
            return new CharEnumerator(value);
        }
    }

    /**
     * Concatenates the string representations of an array of objects, using the specified separator between each member.
     * @param separator The character to use as a separator. {@code separator} is included in the returned string only if {@code values} has more than one element.
     * @param values An array of objects whose string representations will be concatenated.
     * @return A string that consists of the elements of values delimited by the separator character. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@link #Empty} if {@code values} has zero elements.
     * @throws ArgumentNullException {@code values} is {@code null}.
     */
    @NotNull
    public static String Join(char separator, Object... values)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(values, "values");
        if (values.length == 0) {
            return Empty;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(values[0].toString());
            for (int I = 1; I < values.length; I++)
            {
                sb.append(separator);
                sb.append(values[I].toString());
            }
            return sb.toString();
        }
    }

    /**
     * Concatenates an array of strings, using the specified separator between each member.
     * @param separator The character to use as a separator. {@code separator} is included in the returned string only if {@code values} has more than one element.
     * @param values An array of strings to concatenate.
     * @return A string that consists of the elements of {@code values} delimited by the separator character. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@link #Empty} if {@code values} has zero elements.
     * @throws ArgumentNullException {@code values} is {@code null}.
     */
    @NotNull
    public static String Join(char separator, String... values)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(values, "values");
        if (values.length == 0) {
            return Empty;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(values[0]);
            for (int I = 1; I < values.length; I++)
            {
                sb.append(separator);
                sb.append(values[I]);
            }
            return sb.toString();
        }
    }

    /**
     * Concatenates the members of a constructed {@link IEnumerable} collection of type {@link String}, using the specified separator between each member.
     * @param separator The string to use as a separator. {@code separator} is included in the returned string only if values has more than one element.
     * @param values A collection that contains the strings to concatenate.
     * @return A string that consists of the elements of {@code values} delimited by the separator character. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@link #Empty} if {@code values} has zero elements.
     * @throws ArgumentNullException {@code values} is {@code null}.
     */
    @NotNull
    public static String Join(@AllowNull String separator, IEnumerable<String> values)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(values, "values");
        separator = separator == null ? Empty : separator;
        try (var en = values.GetEnumerator())
        {
            StringBuilder builder = new StringBuilder();
            if (en.MoveNext()) {
                builder.append(en.getCurrent());
            } else {
                return Empty;
            }
            while (en.MoveNext()) {
                builder.append(separator);
                builder.append(en.getCurrent());
            }
            return builder.toString();
        }
    }

    /**
     * Concatenates two specified instances of {@link String}.
     * @param str0 The first string to concatenate.
     * @param str1 The second string to concatenate.
     * @return The concatenation of {@code str0} and {@code str1}.
     */
    @NotNull
    public static String Concat(@AllowNull String str0, @AllowNull String str1) { return ((str0 == null) ? Empty : str0).concat((str1 == null) ? Empty : str1); }

    /**
     * Concatenates three specified instances of {@link String}.
     * @param str0 The first string to concatenate.
     * @param str1 The second string to concatenate.
     * @param str2 The third string to concatenate.
     * @return The concatenation of {@code str0}, {@code str1}, and {@code str2}.
     */
    @NotNull
    public static String Concat(@AllowNull String str0, @AllowNull String str1, @AllowNull String str2) { return Concat(str0, Concat(str1 , str2)); }

    /**
     * Concatenates four specified instances of {@link String}.
     * @param str0 The first string to concatenate.
     * @param str1 The second string to concatenate.
     * @param str2 The third string to concatenate.
     * @param str3 The fourth string to concatenate.
     * @return The concatenation of {@code str0}, {@code str1}, {@code str2}, and {@code str3}.
     */
    @NotNull
    public static String Concat(@AllowNull String str0, @AllowNull String str1, @AllowNull String str2, @AllowNull String str3) { return Concat(str0, str1, Concat(str2, str3)); }

    /**
     * Concatenates the elements of a specified {@link String} array.
     * @param values An array of string instances.
     * @return The concatenated elements of {@code values}.
     * @throws ArgumentNullException {@code values} is {@code null}.
     */
    @NotNull
    @CLSCompliant(IsCompliant = false)
    public static String Concat(String... values)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(values, "values");
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            builder.append(value);
        }
        return builder.toString();
    }

    /**
     * Concatenates the elements of a specified {@link Object} array.
     * @param values An object array that contains the elements to concatenate.
     * @return The concatenated string representations of the values of the elements in {@code values}.
     * @throws ArgumentNullException {@code values} is {@code null}.
     */
    @NotNull
    public static String Concat(Object... values)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(values, "values");
        StringBuilder builder = new StringBuilder();
        for (Object value : values) {
            builder.append(value);
        }
        return builder.toString();
    }

    /**
     * Concatenates the members of an {@link IEnumerable} implementation.
     * @param values A collection object that implements the {@link IEnumerable} interface.
     * @return The concatenated members in {@code values}.
     * @param <T> The type of the members of {@code values}.
     * @throws ArgumentNullException {@code values} is {@code null}.
     */
    @NotNull
    public static <T> String Concat(IEnumerable<T> values)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(values, "values");
        StringBuilder builder = new StringBuilder();
        try (var en = values.GetEnumerator())
        {
            while (en.MoveNext()) {
                builder.append(en.getCurrent());
            }
        }
        return builder.toString();
    }

    /**
     * Replaces the format item in a specified string with the string representation of a corresponding object in a specified array.
     * @param format A <a href="https://learn.microsoft.com/en-us/dotnet/standard/base-types/composite-formatting">composite format string</a>.
     * @param args An object array that contains zero or more objects to format.
     * @return A copy of {@code format} in which the format items have been replaced by the string representation of the corresponding objects in {@code args}.
     * @throws ArgumentNullException {@code format} or {@code args} is null.
     * @throws FormatException {@code format} is invalid. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The index of a format item is less than zero, or greater than or equal to the length of the {@code args} array.
     */
    @NotNull
    public static String Format(String format, Object... args)
        throws ArgumentNullException, FormatException
    {
        ArgumentNullException.ThrowIfNull(args, "args");
        StringBuilder sb = new StringBuilder();
        AppendFormatHelper(sb, null , format, args);
        return sb.toString();
    }

    /**
     * Replaces one or more format items in a string with the string representation of a specified object.
     * @param format A <a href="https://learn.microsoft.com/en-us/dotnet/standard/base-types/composite-formatting">composite format string</a>.
     * @param arg0 The object to format.
     * @return A copy of {@code format} in which any format items are replaced by the string representation of {@code arg0}.
     * @throws ArgumentNullException {@code format} is null.
     * @throws FormatException The format item in {@code format} is invalid. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The index of a format item is not zero.
     */
    @NotNull
    public static String Format(String format, @AllowNull Object arg0)
            throws ArgumentNullException, FormatException
    {
        StringBuilder sb = new StringBuilder();
        AppendFormatHelper(sb, null , format, new Object[] { arg0 });
        return sb.toString();
    }

    /**
     * Replaces the format items in a string with the string representations of corresponding objects in a specified array. A parameter supplies culture-specific formatting information.
     * @param provider An object that supplies culture-specific formatting information.
     * @param format A <a href="https://learn.microsoft.com/en-us/dotnet/standard/base-types/composite-formatting">composite format string</a>.
     * @param args An object array that contains zero or more objects to format.
     * @return A copy of {@code format} in which the format items have been replaced by the string representation of the corresponding objects in {@code args}.
     * @throws ArgumentNullException {@code format} or {@code args} is null.
     * @throws FormatException {@code format} is invalid. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * The index of a format item is less than zero, or greater than or equal to the length of the {@code args} array.
     */
    @NotNull
    public static String Format(@AllowNull IFormatProvider provider, String format, Object... args)
            throws ArgumentNullException, FormatException
    {
        ArgumentNullException.ThrowIfNull(args, "args");
        StringBuilder sb = new StringBuilder();
        AppendFormatHelper(sb, provider , format, args);
        return sb.toString();
    }

    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    private static char MoveNext(String format, @DotNetByRefParameter(ByRefParameterType.REF) ByRefParameter<Integer> pos)
    {
        ByRefParameter.AssertERef(pos);
        pos.Value++;
        if (pos.Value >= format.length() || pos.Value < 0)
        {
            // ThrowHelper.ThrowFormatInvalidString(pos, ExceptionResource.Format_UnclosedFormatItem);
            throw new FormatException("The format was not closed.");
        }
        return format.charAt(pos.Value);
    }

    private static int IndexOfAnyHelper(String s, char... chars)
    {
        int pos = -1;
        for (char c : chars) {
            if ((pos = s.indexOf(c)) > -1) {
                return pos;
            }
        }
        return pos;
    }

    private static boolean IsAsciiDigitHelper(char ch) { return ch >= '0' & ch <= '9'; }

    // See file https://github.com/dotnet/runtime/blob/main/src/libraries/System.Private.CoreLib/src/System/Text/ValueStringBuilder.AppendFormat.cs for more information about this.
    @StackTraceHidden
    private static void AppendFormatHelper(StringBuilder builder, @AllowNull IFormatProvider provider, String format, Object[] args)
    {
        ArgumentNullException.ThrowIfNull(format, "format");

        // Undocumented exclusive limits on the range for Argument Hole Index and Argument Hole Alignment.
        final int IndexLimit = 1_000_000; // Note:            0 <= ArgIndex < IndexLimit
        final int WidthLimit = 1_000_000; // Note:  -WidthLimit <  ArgAlign < WidthLimit

        // Query the provider (if one was supplied) for an ICustomFormatter.  If there is one,
        // it needs to be used to transform all arguments.
        ICustomFormatter cf = (ICustomFormatter) ((provider == null) ? null : provider.GetFormat(ICustomFormatter.class));

        ByRefParameter<Integer> pos = new ByRefParameter<>(0);
        char ch;

        while (true)
        {
            // Skip until either the end of the input or the first unescaped opening brace, whichever comes first.
            // Along the way we need to also unescape escaped closing braces.
            while (true)
            {
                // Find the next brace.  If there isn't one, the remainder of the input is text to be appended, and we're done.
                if (pos.Value < 0 || pos.Value >= format.length()) { return; }

                String remainder = format.substring(pos.Value);
                int countUntilNextBrace = IndexOfAnyHelper(remainder, '{', '}');
                if (countUntilNextBrace < 0)
                {
                    builder.append(remainder);
                    return;
                }

                // Append the text until the brace.
                builder.append(remainder, 0, countUntilNextBrace);
                pos.Value += countUntilNextBrace;

                // Get the brace.  It must be followed by another character, either a copy of itself in the case of being
                // escaped, or an arbitrary character that's part of the hole in the case of an opening brace.
                char brace = format.charAt(pos.Value);
                ch = MoveNext(format, pos);
                if (brace == ch)
                {
                    builder.append(ch);
                    pos.Value++;
                    continue;
                }

                // This wasn't an escape, so it must be an opening brace.
                if (brace != '{')
                {
                    // ThrowHelper.ThrowFormatInvalidString(pos, ExceptionResource.Format_UnexpectedClosingBrace);
                    throw new FormatException("The specified format is invalid. A formatting brace was not closed.");
                }

                // Proceed to parse the hole.
                break;
            }

            // We're now positioned just after the opening brace of an argument hole, which consists of
            // an opening brace, an index, an optional width preceded by a comma, and an optional format
            // preceded by a colon, with arbitrary amounts of spaces throughout.
            int width = 0;
            boolean leftJustify = false;
            char[] itemFormatSpan = new char[0]; // used if itemFormat is null

            // First up is the index parameter, which is of the form:
            //     at least on digit
            //     optional any number of spaces
            // We've already read the first digit into ch.
            assert (format.charAt(pos.Value - 1) == '{');
            assert ch != '{';
            int index = ch - '0';
            if (index > 9 || index < 0)
            {
                // ThrowHelper.ThrowFormatInvalidString(pos, ExceptionResource.Format_ExpectedAsciiDigit);
                throw new FormatException("Expected an ASCII digit, but that was not found.");
            }

            // Common case is a single digit index followed by a closing brace.  If it's not a closing brace,
            // proceed to finish parsing the full hole format.
            ch = MoveNext(format, pos);
            if (ch != '}')
            {
                // Continue consuming optional additional digits.
                while (IsAsciiDigitHelper(ch) && index < IndexLimit)
                {
                    index = index * 10 + ch - '0';
                    ch = MoveNext(format, pos);
                }

                // Consume optional whitespace.
                while (ch == ' ')
                {
                    ch = MoveNext(format, pos);
                }

                // Parse the optional alignment, which is of the form:
                //     comma
                //     optional any number of spaces
                //     optional -
                //     at least one digit
                //     optional any number of spaces
                if (ch == ',')
                {
                    // Consume optional whitespace.
                    do {
                        ch = MoveNext(format, pos);
                    } while (ch == ' ');

                    // Consume an optional minus sign indicating left alignment.
                    if (ch == '-')
                    {
                        leftJustify = true;
                        ch = MoveNext(format, pos);
                    }

                    // Parse alignment digits. The read character must be a digit.
                    width = ch - '0';
                    if (width > 9 || width < 0)
                    {
                        // ThrowHelper.ThrowFormatInvalidString(pos, ExceptionResource.Format_ExpectedAsciiDigit);
                        throw new FormatException("Expected an ASCII digit, but that was not found.");
                    }
                    ch = MoveNext(format, pos);
                    while (IsAsciiDigitHelper(ch) && width < WidthLimit)
                    {
                        width = width * 10 + ch - '0';
                        ch = MoveNext(format, pos);
                    }

                    // Consume optional whitespace
                    while (ch == ' ')
                    {
                        ch = MoveNext(format, pos);
                    }
                }

                // The next character needs to either be a closing brace for the end of the hole,
                // or a colon indicating the start of the format.
                if (ch != '}')
                {
                    if (ch != ':')
                    {
                        // Unexpected character
                        //ThrowHelper.ThrowFormatInvalidString(pos, ExceptionResource.Format_UnclosedFormatItem);
                        throw new FormatException("Unexpected character: " + ch);
                    }

                    // Search for the closing brace; everything in between is the format,
                    // but opening braces aren't allowed.
                    int startingPos = pos.Value;
                    while (true)
                    {
                        ch = MoveNext(format, pos);

                        if (ch == '}')
                        {
                            // Argument hole closed
                            break;
                        }

                        if (ch == '{')
                        {
                            // Braces inside the argument hole are not supported
                            // ThrowHelper.ThrowFormatInvalidString(pos, ExceptionResource.Format_UnclosedFormatItem);
                            throw new FormatException("Braces inside the format argument are not supported.");
                        }
                    }

                    startingPos++;
                    // itemFormatSpan = format.AsSpan(startingPos, pos - startingPos);
                    itemFormatSpan = format.substring(startingPos , pos.Value - startingPos).toCharArray();
                }
            }

            // Construct the output for this arg hole.
            assert (format.charAt(pos.Value) == '}');
            pos.Value++;
            String s = null;
            String itemFormat = null;

            if (index >= args.length || index < 0)
            {
                // ThrowHelper.ThrowFormatIndexOutOfRange();
                throw new ArgumentException(String.format("Specified format index (%d) is out of range of the specified values." , index));
            }
            Object arg = args[index];

            if (cf != null)
            {
                if (!(itemFormatSpan.length == 0))
                {
                    itemFormat = new String(itemFormatSpan);
                }

                s = cf.Format(itemFormat, arg, provider);
            }

            if (s == null)
            {
                // If arg is ISpanFormattable and the beginning doesn't need padding,
                // try formatting it into the remaining current chunk.
                /*
                if ((leftJustify || width == 0) &&
                        arg is ISpanFormattable spanFormattableArg &&
                    spanFormattableArg.TryFormat(_chars.Slice(_pos), out int charsWritten, itemFormatSpan, provider))
                {
                    _pos += charsWritten;

                    // Pad the end, if needed.
                    if (leftJustify && width > charsWritten)
                    {
                        Append(' ', width - charsWritten);
                    }

                    // Continue to parse other characters.
                    continue;
                }
                 */

                // Otherwise, fallback to trying IFormattable or calling ToString.
                if (arg instanceof IFormattable formattableArg)
                {
                    if (itemFormatSpan.length != 0 && itemFormat == null)
                    {
                        itemFormat = new String(itemFormatSpan);
                    }
                    s = formattableArg.ToString(itemFormat, provider);
                } else if (arg != null) {
                    s = arg.toString();
                } else {
                    s = Empty;
                }
            }

            // Append it to the final output of the Format String.
            if (width <= s.length()) {
                builder.append(s);
            } else if (leftJustify) {
                builder.append(s);
                builder.append(" ".repeat(width - s.length()));
            } else {
                builder.append(" ".repeat(width - s.length()));
                builder.append(s);
            }
        }
    }
}
