package com.github.mdcdi1315.basemodslib.world.saveddata;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLibraryException;

/**
 * The exception that is thrown when the read saved data format details are not correct, or the saved data could not be read.
 */
public final class IncorrectSavedDataFormatException
    extends BaseModsLibraryException
{
    /**
     * Initializes a new instance of the {@link IncorrectSavedDataFormatException} class without any additional information on why this exception object was created.
     */
    public IncorrectSavedDataFormatException() { super(); }

    /**
     * Initializes a new instance of the {@link IncorrectSavedDataFormatException} class. <br />
     * The error message parameter provided describes why this exception object was created.
     * @param message An error message describing the faulty condition that was determined.
     */
    public IncorrectSavedDataFormatException(@MaybeNull String message) { super(message); }

    /**
     * Initializes a new instance of the {@link IncorrectSavedDataFormatException} class. <br />
     * The error message parameter provided describes why this exception object was created,
     * and the inner exception parameter provides the cause of why this exception object was created.
     * @param message An error message describing the faulty condition that was determined.
     * @param ex The exception causing this exception object to be created.
     */
    public IncorrectSavedDataFormatException(@MaybeNull String message, @MaybeNull Exception ex) { super(message , ex); }
}
