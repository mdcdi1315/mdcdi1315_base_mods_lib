package com.github.mdcdi1315.basemodslib.utils;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Wraps a spliterator instance that, given by a mapping function, it maps all the elements to the {@link TR} type before returning them.
 * @param <TA> The original type of the original spliterator elements.
 * @param <TR> The mapped type returned through methods of this class.
 * @since 1.0.20
 */
public class DirectlyMappedSpliterator<TA, TR>
    implements Spliterator<TR>
{
    private final Func2<TA, TR> mapper;
    private final Spliterator<TA> spliterator;

    /**
     * Creates a new instance of the {@link DirectlyMappedSpliterator} class.
     * @param spliterator The spliterator to map all the elements that will return as of type {@link TR}.
     * @param mapper The mapping function to use for mapping a single {@link TA} element to a {@link TR} element.
     */
    public DirectlyMappedSpliterator(Spliterator<TA> spliterator, Func2<TA, TR> mapper)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(this.mapper = mapper, "mapper");
        ArgumentNullException.ThrowIfNull(this.spliterator = spliterator, "spliterator");
    }

    private static final class ElementConsumer<T>
            implements Consumer<T>
    {
        public T value;

        @Override
        public void accept(T t) { value = t; }
    }

    private record ElementConsumer_TryAdvance<TA, TR>(Consumer<? super TR> action, Func2<TA, TR> mapper)
        implements Consumer<TA>
    {
        @Override
        public void accept(TA input) { action.accept(mapper.function(input)); }
    }

    @Override
    public boolean tryAdvance(Consumer<? super TR> action) {
        return spliterator.tryAdvance(new ElementConsumer_TryAdvance<>(action, mapper));
    }

    @Override
    public void forEachRemaining(Consumer<? super TR> action)
    {
        ElementConsumer<TA> c = new ElementConsumer<>();
        while (spliterator.tryAdvance(c)) { action.accept(mapper.function(c.value)); }
    }

    @Override
    public Spliterator<TR> trySplit()
    {
        Spliterator<TA> sp_new = spliterator.trySplit();
        return (sp_new == null) ? null : new DirectlyMappedSpliterator<>(sp_new, mapper);
    }

    @Override
    public long estimateSize() { return spliterator.estimateSize(); }

    @Override
    public int characteristics() { return spliterator.characteristics(); }

    @Override
    public long getExactSizeIfKnown() { return spliterator.getExactSizeIfKnown(); }

    @Override
    public boolean hasCharacteristics(int characteristics) { return spliterator.hasCharacteristics(characteristics); }
}
