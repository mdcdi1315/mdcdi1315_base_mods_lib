package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Defines a tag in a {@link IModLoaderRegistry} object.
 * @param <T> The type of the tag values.
 */
public interface ITag<T>
    extends Iterable<T>
{
    /**
     * Gets the key backing this tag object.
     * @return The tag key backing this tag object.
     */
    TagKey<T> getKey();

    /**
     * Gets the tag's values as a stream instead of getting an {@link java.util.Iterator} for them.
     * @return A stream containing the tag's values.
     */
    Stream<T> stream();

    /**
     * Gets the number of elements contained in this tag.
     * @return A positive integer that is the number of tag elements. 0 if it does not contain any elements.
     */
    int getSize();

    /**
     * Gets a value whether the specified element does exist in the tag.
     * @param element The element to test.
     * @return A value whether element is part of this tag entries.
     */
    boolean Contains(T element);

    /**
     * Picks a random element from the current tag entries and returns it. <br />
     * If the tag is empty the {@link Optional#isEmpty()} method will return true.
     * @param source The random number generator to use.
     * @return The random element.
     * @throws ArgumentNullException <em>source</em> was null.
     */
    Optional<T> GetRandomElement(RandomSource source) throws ArgumentNullException;

    /**
     * Gets a value whether this tag is bound to a {@link IModLoaderRegistry} object.
     * @return A value whether this tag is bound or not.
     */
    boolean IsBound();

    /**
     * Gets a value whether this tag does not have any associated entries.
     * @return A value whether this tag does not have any values.
     */
    default boolean IsEmpty() {
        return getSize() == 0;
    }
}
