package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.ApplicationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

/**
 * Defines the base class where all other exceptions originating from the Base Mods Library are thrown. <br />
 * It can be also thrown and on its own way, but throwing this would be very, very verbose.
 */
public class BaseModsLibraryException
    extends ApplicationException
{
    /**
     * Initializes a new instance of the {@link BaseModsLibraryException} class without any additional information on why this exception object was created.
     */
    public BaseModsLibraryException() { super(); }

    /**
     * Initializes a new instance of the {@link BaseModsLibraryException} class. <br />
     * The error message parameter provided describes why this exception object was created.
     * @param message An error message describing the faulty condition that was determined.
     */
    public BaseModsLibraryException(@MaybeNull String message) { super(message); }

    /**
     * Initializes a new instance of the {@link BaseModsLibraryException} class. <br />
     * The error message parameter provided describes why this exception object was created,
     * and the inner exception parameter provides the cause of why this exception object was created.
     * @param message An error message describing the faulty condition that was determined.
     * @param inner The exception causing this exception object to be created.
     */
    public BaseModsLibraryException(@MaybeNull String message, @MaybeNull Exception inner) { super(message, inner); }
}
