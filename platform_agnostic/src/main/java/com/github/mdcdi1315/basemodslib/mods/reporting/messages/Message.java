package com.github.mdcdi1315.basemodslib.mods.reporting.messages;

import com.github.mdcdi1315.DotNetLayer.ByRefParameter;
import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.Dictionary;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.mods.reporting.IMessage;
import com.github.mdcdi1315.basemodslib.mods.reporting.MessageSeverity;
import com.github.mdcdi1315.basemodslib.utils.collections.comparers.CharSequenceEqualityComparer;

import net.minecraft.network.chat.Component;

import java.util.Optional;

/**
 * A convenient base class for writing new message instances. <br />
 * Implements {@link IMessage}.
 */
public abstract class Message
    implements IMessage
{
    private Component message;
    private MessageSeverity severity;
    private String associated_issue_url;
    private final Dictionary<String, Object> attributes;

    /**
     * Initializes a new instance of the {@link Message} class with sensible defaults.
     */
    public Message()
    {
        message = Component.empty();
        severity = MessageSeverity.INFO;
        associated_issue_url = StringUtils.Empty;
        attributes = new Dictionary<>(16, CharSequenceEqualityComparer.SENSITIVE);
    }

    /**
     * Sets the message to be reported.
     * @param message The message to be reported.
     * @throws ArgumentNullException {@code message} is {@code null}.
     */
    protected final void SetMessage(Component message)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(message, "message");
        this.message = message;
    }

    /**
     * Sets the {@link MessageSeverity} that indicates the severity of this message.
     * @param severity The {@link MessageSeverity} for this message.
     * @throws ArgumentNullException {@code severity} is {@code null}.
     */
    protected final void SetSeverity(MessageSeverity severity)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(severity, "severity");
        this.severity = severity;
    }

    /**
     * Sets the URL that further describes this message and what the users can do about it.
     * @param associated_issue_url A valid URL to a website describing in depth this message, or the empty string to clear the URL.
     * @throws ArgumentNullException {@code associated_issue_url} is {@code null}.
     */
    protected final void SetAssociatedIssueURL(String associated_issue_url)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(associated_issue_url, "associated_issue_url");
        this.associated_issue_url = associated_issue_url;
    }

    /**
     * Registers a new attribute to this message.
     * @param name The name of the new attribute.
     * @param value The value of the new attribute.
     * @throws ArgumentNullException {@code name} and/or {@code value} are {@code null}.
     * @throws ArgumentException The specified attribute with the specified name does already exist.
     */
    protected final void RegisterAttribute(String name, Object value)
            throws ArgumentNullException, ArgumentException
    {
        ArgumentNullException.ThrowIfNull(name, "name");
        ArgumentNullException.ThrowIfNull(value, "value");
        attributes.Add(name, value);
    }

    /**
     * Ensures that the attributes dictionary occupies as less memory as possible.
     */
    protected final void FinalizeAttributes() { attributes.TrimExcess(); }

    @Override
    public final Optional<Object> GetAttribute(String name)
            throws ArgumentNullException
    {
        ByRefParameter<Object> value = new ByRefParameter<>();
        if (attributes.TryGetValue(name, value)) {
            return Optional.of(value.Value);
        } else {
            return Optional.empty();
        }
    }

    @Pure
    @NotNull
    @Override
    public final Component GetMessage() { return message; }

    @Pure
    @NotNull
    @Override
    public final MessageSeverity GetSeverity() { return severity; }

    @Pure
    @NotNull
    @Override
    public final String GetAssociatedIssueURL() { return associated_issue_url; }

    @NotNull
    @Override
    public final IEnumerable<String> GetAttributeKeys() { return attributes.getKeys(); }
}
