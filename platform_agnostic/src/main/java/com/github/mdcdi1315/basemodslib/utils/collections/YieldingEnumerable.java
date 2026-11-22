package com.github.mdcdi1315.basemodslib.utils.collections;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.ExceptionTranslator;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.StackTraceHidden;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerator;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ForkJoinPool;

/**
 * Provides an {@link IEnumerable} implementation that returns
 * elements as they are submitted by an executing action.
 * @apiNote Note that the action is executed in the {@linkplain ForkJoinPool Fork-Join Pool},
 * so make sure that your method is written in a thread-safe manner.
 * @param <T> The type of the elements to return.
 * @param <TState> Additional state data required for executing the action.
 * @since 1.0.42
 */
@APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.42")
public final class YieldingEnumerable<T, TState>
    implements IEnumerable<T>
{
    private final TState state;
    private final Action1<IYieldActionData<T, TState>> action;

    /**
     * Provides required methods for implementing the yielding logic. <br />
     * An implementation of this interface is provided once the action specified
     * in the yielding enumerable starts execution.
     * @param <T> The type of the elements.
     * @param <TState> The type of the state value stored in the enumerable
     *                and returned via the {@link #GetState()} method.
     */
    public interface IYieldActionData<T, TState>
    {
        /**
         * Gets the state value.
         * @return The state value.
         */
        @MaybeNull
        TState GetState();

        /**
         * Yields an element.
         * @param value The element to yield.
         */
        void Yield(@AllowNull T value);

        /**
         * Breaks the action, flagging to the enumerable that no more elements are to be returned.
         */
        void Break();
    }

    /**
     * Initializes a new instance of the {@link YieldingEnumerable} class.
     * @param state The state data. This value will be returned when calling {@link IYieldActionData#GetState()}. Can be {@code null}.
     * @param action The action to execute.
     * @throws ArgumentNullException {@code action} is {@code null}.
     */
    public YieldingEnumerable(@AllowNull TState state, Action1<IYieldActionData<T, TState>> action)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(action, "action");
        this.state = state;
        this.action = action;
    }

    private static final class Data<T, TS>
        implements IYieldActionData<T, TS>
    {
        private T value;
        private final TS state;
        private Exception exception;
        private volatile boolean done, has_element;
        private final Semaphore yield_semaphore, move_next_semaphore;

        public Data(TS state)
        {
            done = false;
            value = null;
            this.state = state;
            this.has_element = false;
            this.yield_semaphore = new Semaphore(0);
            this.move_next_semaphore = new Semaphore(0);
        }

        @Override
        public TS GetState() { return state; }

        @Override
        public void Yield(T value)
        {
            if (done) { return; }
            try { yield_semaphore.acquire(); } catch (InterruptedException e) { return; }
            this.value = value;
            has_element = true;
            move_next_semaphore.release();
        }

        @Override
        public void Break()
        {
            done = true;
            yield_semaphore.release();
            move_next_semaphore.release();
        }

        public T GetValue() { return value; }

        public boolean HasElement() { return has_element; }

        public void SetException(Exception exception) { this.exception = exception; }

        @StackTraceHidden
        private void CheckException()
        {
            if (exception instanceof com.github.mdcdi1315.DotNetLayer.System.Exception e) {
                throw e;
            } else if (exception != null) {
                throw new RuntimeException(exception);
            }
        }

        @StackTraceHidden
        public void MoveToNextState()
        {
            CheckException();
            has_element = false;
            if (done) { return; }
            yield_semaphore.release();
            try { move_next_semaphore.acquire(); } catch (InterruptedException _) { }
            CheckException();
        }
    }

    private static final class Enumerator<T, TState>
        implements IEnumerator<T>
    {
        private T temp_value;
        private final Data<T, TState> execution_data;

        private record WrappedAction<T, TS>(Action1<IYieldActionData<T, TS>> action, Data<T, TS> data)
            implements Runnable
        {
            @Override
            public void run()
            {
                try {
                    action.action(data);
                } catch (Exception t) {
                    var e = ExceptionTranslator.TranslateException(t);
                    if (e == null) { data.SetException(new RuntimeException(t)); } else { data.SetException(e); }
                } finally {
                    data.Break();
                }
            }
        }

        @SuppressWarnings("resource")
        public Enumerator(TState state, Action1<IYieldActionData<T, TState>> action)
        {
            temp_value = null;
            ForkJoinPool.commonPool().execute(new WrappedAction<>(action, execution_data = new Data<>(state)));
        }

        @Override
        public boolean MoveNext()
        {
            execution_data.MoveToNextState();
            temp_value = execution_data.GetValue();
            return execution_data.HasElement();
        }

        @Override
        public T getCurrent() { return temp_value; }

        @Override
        public void Dispose() { execution_data.Break(); }

        @Override
        public void Reset() throws InvalidOperationException { throw new InvalidOperationException("Cannot reset a yielding enumerator"); }
    }

    /**
     * Provides the method that yields the elements to return.
     * @return The method.
     */
    @Pure
    @NotNull
    public Action1<IYieldActionData<T, TState>> YieldingAction() { return action; }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public IEnumerator<T> GetEnumerator() { return new Enumerator<>(state, action); }
}
