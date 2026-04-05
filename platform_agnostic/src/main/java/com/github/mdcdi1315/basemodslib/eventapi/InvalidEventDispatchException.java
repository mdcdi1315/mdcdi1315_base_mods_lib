package com.github.mdcdi1315.basemodslib.eventapi;

import com.github.mdcdi1315.DotNetLayer.System.Exception;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

/**
 * Provides an {@link InvalidOperationException} inheritor
 * that is thrown when a given {@link IEvent} instance is incorrectly dispatched.
 * @since 1.0.25
 */
public class InvalidEventDispatchException
        extends InvalidOperationException
{
    @NotNull
    private final IEvent dispatched_event;

    /**
     * Constructs a new instance of the {@link InvalidEventDispatchException} class, specifying the event instance that
     * is the cause of this exception to be created.
     * @param dispatched_event The {@link IEvent} instance that caused event dispatch to fail fast.
     * @throws ArgumentNullException {@code dispatched_event} is {@code null}.
     */
    public InvalidEventDispatchException(IEvent dispatched_event)
            throws ArgumentNullException
    {
        super();
        if (dispatched_event == null) {
            throw new ArgumentNullException("dispatched_event", "Null dispatched_event parameter in InvalidEventDispatchException ctor.");
        } else {
            this.dispatched_event = dispatched_event;
        }
    }

    /**
     * Constructs a new instance of the {@link InvalidEventDispatchException} class, specifying the event instance that
     * is the cause of this exception to be created, along with an error detail message.
     * @param dispatched_event The {@link IEvent} instance that caused event dispatch to fail fast.
     * @param message An error detail message further describing the exception. Can be {@code null}.
     * @throws ArgumentNullException {@code dispatched_event} is {@code null}.
     */
    public InvalidEventDispatchException(IEvent dispatched_event, @AllowNull String message)
            throws ArgumentNullException
    {
        super(message);
        if (dispatched_event == null) {
            throw new ArgumentNullException("dispatched_event", "Null dispatched_event parameter in InvalidEventDispatchException ctor.");
        } else {
            this.dispatched_event = dispatched_event;
        }
    }

    /**
     * Constructs a new instance of the {@link InvalidEventDispatchException} class, specifying the event instance that
     * is the cause of this exception to be created, along with an error detail message, and the inner exception that is
     * also the cause of this exception to be created.
     * @param dispatched_event The {@link IEvent} instance that caused event dispatch to fail fast.
     * @param message An error detail message further describing the exception. Can be {@code null}.
     * @param inner The inner exception that is the second reason why this exception is created.
     * @throws ArgumentNullException {@code dispatched_event} is {@code null}.
     */
    public InvalidEventDispatchException(IEvent dispatched_event, @AllowNull String message, @AllowNull Exception inner)
            throws ArgumentNullException
    {
        super(message, inner);
        if (dispatched_event == null) {
            throw new ArgumentNullException("dispatched_event", "Null dispatched_event parameter in InvalidEventDispatchException ctor.");
        } else {
            this.dispatched_event = dispatched_event;
        }
    }

    /**
     * Gets the {@link IEvent} that was invalidly dispatched.
     * @return The {@link IEvent} that was dispatched invalidly.
     */
    @NotNull
    public IEvent GetInvalidDispatchedEvent() { return dispatched_event; }

    @Override
    public String getMessage()
    {
        String s = super.getMessage();
        if (StringUtils.IsNullOrEmpty(s)) { s = "The event manager rejected the dispatch of the given event."; }
        StringBuilder builder = new StringBuilder(s);
        builder.append('\n');
        builder.append("Event instance information:\n");
        builder.append("Event class name that was failed dispatch: ");
        builder.append(dispatched_event.getClass().getName());
        builder.append('\n');
        builder.append("Event class instance hash code: ");
        builder.append(dispatched_event.hashCode());
        builder.append('\n');
        builder.append("Event class toString method call result: ");
        try {
            builder.append(dispatched_event.toString());
        } catch (java.lang.Exception ex) {
            addSuppressed(ex);
            builder.append("<EXCEPTION CALLING TOSTRING>");
        }
        builder.append('\n');
        return builder.toString();
    }
}
