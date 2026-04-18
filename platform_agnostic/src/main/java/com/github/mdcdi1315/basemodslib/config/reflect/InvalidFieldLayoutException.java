package com.github.mdcdi1315.basemodslib.config.reflect;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.config.ConfigException;

/**
 * Exception class that is thrown when a reflected configuration field has an invalid layout. <br />
 * This happens, for example, when a field of type {@link java.util.List} does not have specified a
 * {@link com.github.mdcdi1315.basemodslib.config.ListField} annotation instance.
 */
public class InvalidFieldLayoutException
    extends ConfigException
{
    /**
     * Creates a new, default instance of the {@link InvalidFieldLayoutException} class with a default specified message.
     */
    public InvalidFieldLayoutException() { this("A field had an invalid layout according to config reflection rules."); }

    /**
     * Creates a new instance of the {@link InvalidFieldLayoutException} class, <br />
     * providing the error message describing the cause of this exception.
     * @param message An error message describing the exception's cause.
     */
    public InvalidFieldLayoutException(@AllowNull String message) { this(message, null); }

    /**
     * Creates a new instance of the {@link InvalidFieldLayoutException} class,  <br />
     * providing the error message describing the cause of this exception, <br />
     * and the inner exception that is the cause of this exception.
     *
     * @param message An error message describing the exception's cause.
     * @param inner   The inner exception that is the cause of this exception.
     */
    public InvalidFieldLayoutException(@AllowNull String message, @AllowNull Exception inner) { super(message, inner); }
}
