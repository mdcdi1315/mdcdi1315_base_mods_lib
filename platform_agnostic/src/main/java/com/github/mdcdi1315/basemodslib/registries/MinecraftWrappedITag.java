package com.github.mdcdi1315.basemodslib.registries;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public final class MinecraftWrappedITag<T>
    implements ITag<T>
{
    private HolderSet.Named<T> hs;

    public MinecraftWrappedITag(HolderSet.Named<T> holderset)
    {
        ArgumentNullException.ThrowIfNull(holderset , "holderset");
        hs = holderset;
    }

    private static class IteratorOfTInternal<T>
        implements Iterator<T>
    {
        private Iterator<Holder<T>> iterator;
        private T nextvalue;

        public IteratorOfTInternal(Iterator<Holder<T>> it)
        {
            iterator = it;
        }

        @Override
        public boolean hasNext() {
            Holder<T> holder;
            while (iterator.hasNext())
            {
                if ((holder = iterator.next()).isBound())
                {
                    nextvalue = holder.value();
                    return true;
                }
            }
            return false;
        }

        @Override
        public T next() {
            return nextvalue;
        }
    }

    @Override
    public TagKey<T> getKey() {
        return hs.key();
    }

    @Override
    public Stream<T> stream() {
        return hs.stream().map(Holder::value);
    }

    @Override
    public int getSize() {
        return hs.size();
    }

    @Override
    public boolean Contains(T element) {
        return hs.contains(Holder.direct(element));
    }

    @Override
    public Optional<T> GetRandomElement(RandomSource source) throws ArgumentNullException {
        var opt = hs.getRandomElement(source);
        return opt.map(Holder::value);
    }

    @Override
    public boolean IsBound() {
        return true;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new IteratorOfTInternal<>(hs.iterator());
    }
}