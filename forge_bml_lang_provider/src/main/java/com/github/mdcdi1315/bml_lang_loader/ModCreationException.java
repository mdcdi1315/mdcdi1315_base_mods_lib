package com.github.mdcdi1315.bml_lang_loader;

/**
 * A simple exception type for handling mod creation exceptions,
 * rather than throwing a non-understandable runtime exception.
 */
public final class ModCreationException
        extends RuntimeException
{
    public ModCreationException() { super(); }

    public ModCreationException(String message) {
        super(message);
    }

    public ModCreationException(String message, Throwable t) { super(message , t); }
}
