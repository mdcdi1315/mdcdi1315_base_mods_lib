package com.github.mdcdi1315.basemodslib.mods.reporting;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Dictionary;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.EmptyEnumerable;
import com.github.mdcdi1315.basemodslib.utils.collections.SingleLinkedListBasedRegister;

/**
 * A bag holding all the diagnostic messages reported by the mods and the library.
 */
public final class MessageReporter
{
    private boolean frozen;
    private final Dictionary<MessageSeverity, SingleLinkedListBasedRegister<IMessage>> messages_by_severity;

    /**
     * Initializes a new instance of the {@link MessageReporter} class.
     */
    public MessageReporter()
    {
        frozen = false;
        messages_by_severity = new Dictionary<>(5);
    }

    /**
     * Queries whether this reporter has any diagnostics reported through the {@link #Report(IMessage)} method.
     * @return A value whether diagnostic messages have been reported.
     */
    public boolean HasReportedMessages() { return messages_by_severity.getCount() > 0; }

    /**
     * Freezes this message reporter, that means, that no new diagnostic messages can be added after the reporter
     * has been successfully frozen.
     */
    public void Freeze()
    {
        synchronized (messages_by_severity)
        {
            if (frozen) { return; }
            frozen = true;
            messages_by_severity.TrimExcess();
        }
    }

    /**
     * Reports a new message.
     * @param message The message to report.
     * @throws ArgumentNullException {@code message} is {@code null}.
     * @throws FrozenMessageReporterException This reporter instance has been frozen and does not accept new messages.
     */
    public void Report(IMessage message)
            throws ArgumentNullException, FrozenMessageReporterException
    {
        ArgumentNullException.ThrowIfNull(message, "message");
        if (frozen) { throw new FrozenMessageReporterException(); }
        var sev = message.GetSeverity();
        ByRefParameter<SingleLinkedListBasedRegister<IMessage>> message_list = new ByRefParameter<>();
        synchronized (messages_by_severity)
        {
            if (!messages_by_severity.TryGetValue(sev, message_list))
            {
                message_list.Value = new SingleLinkedListBasedRegister<>();
                messages_by_severity.Add(sev, message_list.Value);
            }
            message_list.Value.Register(message);
        }
    }

    /**
     * Gets all the reported messages for a given severity.
     * @param severity The message severity to get the reported messages for.
     * @return A list of all the reported messages.
     * @throws ArgumentNullException {@code severity} is {@code null}.
     */
    @NotNull
    public IEnumerable<IMessage> GetReportedMessages(MessageSeverity severity)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(severity, "severity");
        ByRefParameter<SingleLinkedListBasedRegister<IMessage>> message_list = new ByRefParameter<>();
        if (messages_by_severity.TryGetValue(severity, message_list)) {
            return message_list.Value;
        } else {
            return new EmptyEnumerable<>();
        }
    }

}
