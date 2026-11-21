package com.github.mdcdi1315.basemodslib.mods.reporting.messages;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.mods.reporting.MessageSeverity;

import net.minecraft.network.chat.Component;

/**
 * Provides a message whose severity is {@link MessageSeverity#RECOMMENDATION}.
 */
public class RecommendationalMessage
    extends Message
{
    /**
     * Initializes a new instance of the {@link RecommendationalMessage} class.
     * @param message The message to be reported.
     * @throws ArgumentNullException {@code message} is {@code null}.
     */
    public RecommendationalMessage(Component message) throws ArgumentNullException { this(message, StringUtils.Empty); }

    /**
     * Initializes a new instance of the {@link RecommendationalMessage} class.
     * @param message The message to be reported.
     * @param associated_issue_URL The associated issue URL to be used.
     * @throws ArgumentNullException {@code message} and/or {@code associated_issue_URL} are {@code null}.
     */
    public RecommendationalMessage(Component message, String associated_issue_URL)
            throws ArgumentNullException
    {
        SetMessage(message);
        SetSeverity(MessageSeverity.RECOMMENDATION);
        SetAssociatedIssueURL(associated_issue_URL);
    }
}
