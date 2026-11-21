package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Generic.IEnumerable;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.mods.reporting.IMessage;
import com.github.mdcdi1315.basemodslib.mods.reporting.MessageSeverity;
import com.github.mdcdi1315.basemodslib.utils.collections.CollectionManipulations;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;

import net.minecraft.network.chat.Component;

import net.minecraft.util.Util;

import java.util.Optional;

public final class MessageReporterMessagesScreen
    extends BaseAboutScreen
{
    public MessageReporterMessagesScreen(Screen parent)
    {
        super(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.title"), parent);
        SetWorkAreaSpacing(10);
        InitLayout();
    }

    private void InitLayout()
    {
        var message_reporter = BaseModsLib.GetMessageReporter();

        IEnumerable<IMessage> enumerable = message_reporter.GetReportedMessages(MessageSeverity.ERROR);

        if (!CollectionManipulations.IsEmpty(enumerable))
        {
            AddWorkAreaWidget(
                    new StringWidget(
                            Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.header_error"),
                            font
                    )
            );

            try (var en = enumerable.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    ConstructMessageToWidgets(en.getCurrent(), this::AddWorkAreaWidget);
                }
            }
        }

        enumerable = message_reporter.GetReportedMessages(MessageSeverity.WARNING);

        if (!CollectionManipulations.IsEmpty(enumerable))
        {
            AddWorkAreaWidget(
                    new StringWidget(
                            Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.header_warn"),
                            font
                    )
            );

            try (var en = enumerable.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    ConstructMessageToWidgets(en.getCurrent(), this::AddWorkAreaWidget);
                }
            }
        }

        enumerable = message_reporter.GetReportedMessages(MessageSeverity.INFO);

        if (!CollectionManipulations.IsEmpty(enumerable))
        {
            AddWorkAreaWidget(
                    new StringWidget(
                            Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.header_info"),
                            font
                    )
            );

            try (var en = enumerable.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    ConstructMessageToWidgets(en.getCurrent(), this::AddWorkAreaWidget);
                }
            }
        }

        enumerable = message_reporter.GetReportedMessages(MessageSeverity.RECOMMENDATION);

        if (!CollectionManipulations.IsEmpty(enumerable))
        {
            AddWorkAreaWidget(
                    new StringWidget(
                            Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.header_recommendation"),
                            font
                    )
            );

            try (var en = enumerable.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    ConstructMessageToWidgets(en.getCurrent(), this::AddWorkAreaWidget);
                }
            }
        }
    }

    private void ConstructMessageToWidgets(IMessage message, Action1<AbstractWidget> action)
    {
        action.action(
                new StringWidget(Component.literal("-----------------------------------------------------------------------"), font)
        );
        action.action(
                new StringWidget(
                        Component.translatable(
                                "mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.diag_message.severity",
                                message.GetSeverity().severity_key().toUpperCase()
                        ),
                        font
                )
        );
        action.action(
                new MultiLineTextWidget(
                        message.GetMessage(),
                        font
                )
        );
        String s = message.GetAssociatedIssueURL();
        if (!s.isEmpty())
        {
            var c = Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.diag_message.see_url");
            action.action(
                    Button.builder(
                            c,
                            new ButtonOpenUrl(this, s)
                    )
                            .width(this.BoundToMaxOfWorkArea(font.width(c)) + 20)
                            .build()
            );
        }
        var c = message.GetAttributeKeys();
        if (!CollectionManipulations.IsEmpty(c))
        {
            action.action(
                    new StringWidget(
                            Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.diag_message.attributes_header"),
                            font
                    )
            );

            try (var en = c.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    var attr = en.getCurrent();
                    action.action(
                            new StringWidget(
                                    Component.translatable(
                                            "mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.diag_message.attribute_and_value",
                                            attr, StringifyAttributeValue(message.GetAttribute(attr))),
                                    font
                            )
                    );
                }
            }
        }
        action.action(
                new StringWidget(Component.literal("-----------------------------------------------------------------------"), font)
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static String StringifyAttributeValue(Optional<Object> value)
    {
        if (value.isEmpty()) {
            return "<EMPTY>";
        } else {
            try {
                return value.get().toString();
            } catch (Exception ex) {
                return "<EXCEPTION>";
            }
        }
    }

    @SuppressWarnings("NullableProblems")
    private record ButtonOpenUrl(Screen parent, String url)
        implements Button.OnPress, BooleanConsumer
    {
        @Override
        public void onPress(Button button)
        {
            Minecraft.getInstance().setScreen(new ConfirmLinkScreen(this, url, true));
        }

        @Override
        public void accept(boolean result)
        {
            if (result) { Util.getPlatform().openUri(url); }

            Minecraft.getInstance().setScreen(parent);
        }
    }
}
