package com.github.mdcdi1315.DotNetLayer.System.Collections.Generic;

/**
 * Used internally to control behavior of insertion into a {@link Dictionary} or <see cref="HashSet{T}"/>.
 */
enum InsertionBehavior
{
    /**
     * The default insertion behavior.
     */
    None,

    /**
     * Specifies that an existing entry with the same key should be overwritten if encountered.
     */
    OverwriteExisting,

    /**
     * Specifies that if an existing entry with the same key is encountered, an exception should be thrown.
     */
    ThrowOnExisting
}