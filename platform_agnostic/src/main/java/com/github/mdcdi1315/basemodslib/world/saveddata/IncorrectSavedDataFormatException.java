package com.github.mdcdi1315.basemodslib.world.saveddata;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.ApplicationException;

/**
 * The exception that is thrown when the read saved data format details are not correct, or the saved data could not be read.
 */
public final class IncorrectSavedDataFormatException
    extends ApplicationException
{
    public IncorrectSavedDataFormatException() {}

    public IncorrectSavedDataFormatException(String message) { super(message); }

    public IncorrectSavedDataFormatException(String message, Exception ex) { super(message , ex); }
}
