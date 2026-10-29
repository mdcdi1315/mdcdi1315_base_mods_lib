package com.github.mdcdi1315.DotNetLayer.System.Collections;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

/**
 * Enumerates the elements of a nongeneric dictionary.
 */
public interface IDictionaryEnumerator
    extends IEnumerator
{
    /**
     * Gets both the key and the value of the current dictionary entry.
     * @return A {@link DictionaryEntry} containing both the key and the value of the current dictionary entry.
     * @throws InvalidOperationException The {@link IDictionaryEnumerator} is positioned before the first entry of the dictionary or after the last entry.
     */
    @NotNull
    default DictionaryEntry GetEntry() throws InvalidOperationException { return (DictionaryEntry) getCurrent(); }

    /**
     * Gets the key of the current dictionary entry.
     * @return The key of the current element of the enumeration.
     * @throws InvalidOperationException The {@link IDictionaryEnumerator} is positioned before the first entry of the dictionary or after the last entry.
     */
    default Object GetKey() throws InvalidOperationException { return GetEntry().GetKey(); }

    /**
     * Gets the value of the current dictionary entry.
     * @return The value of the current element of the enumeration.
     * @throws InvalidOperationException The {@link IDictionaryEnumerator} is positioned before the first entry of the dictionary or after the last entry.
     */
    default Object GetValue() throws InvalidOperationException { return GetEntry().GetValue(); }
}
