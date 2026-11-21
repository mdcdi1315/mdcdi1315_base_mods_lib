package com.github.mdcdi1315.basemodslib.mods.reporting.messages;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.mods.reporting.MessageSeverity;

import net.minecraft.network.chat.Component;

/**
 * Provides a message whose severity is {@link MessageSeverity#INFO}.
 */
public class InformationalMessage
    extends Message
{
    /**
     * Initializes a new instance of the {@link InformationalMessage} class.
     * @param message The message to be reported.
     * @throws ArgumentNullException {@code message} is {@code null}.
     */
    public InformationalMessage(Component message) throws ArgumentNullException { this(message, StringUtils.Empty); }

    /**
     * Initializes a new instance of the {@link InformationalMessage} class.
     * @param message The message to be reported.
     * @param associated_issue_URL The associated issue URL to be used.
     * @throws ArgumentNullException {@code message} and/or {@code associated_issue_URL} are {@code null}.
     */
    public InformationalMessage(Component message, String associated_issue_URL)
            throws ArgumentNullException
    {
        SetMessage(message);
        SetSeverity(MessageSeverity.INFO);
        SetAssociatedIssueURL(associated_issue_URL);
    }
}
