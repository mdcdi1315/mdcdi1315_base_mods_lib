package com.github.mdcdi1315.basemodslib.client.gui;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;

/**
 * Provides a dialog screen for informing the user about something.
 */
public final class InformationalDialogScreen
    extends AbstractDialogScreen
{
    private final Button ok_button;

    @APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.40-beta7")
    public InformationalDialogScreen(Component title, Component text, @AllowNull Screen parent)
            throws ArgumentNullException
    {
        super(title, parent);
        ArgumentNullException.ThrowIfNull(text, "text");
        SetText(text);
        ok_button = Button.builder(Component.translatable("mdcdi1315_base_mods_lib.info_dialog_screen.ok_button_text"), this::OnOKButtonPressed)
                .size(70 , 25)
                .build();
    }

    /**
     * Creates a new instance of the {@link InformationalDialogScreen} class, providing the title of the dialog, the description text, and the parent screen to bind to.
     * @param title The title of the informational dialog.
     * @param text_lines The description text. This must not be null.
     * @param parent The parent screen to get to after this dialog has exited. Can be {@code null}, which in such case the user will be lead to the main screen.
     * @throws ArgumentNullException {@code text_lines} parameter is {@code null}.
     */
    public InformationalDialogScreen(Component title, String text_lines, @AllowNull Screen parent)
            throws ArgumentNullException
    {
        this(title, CreateComponentFromText(text_lines), parent);
    }

    /**
     * Creates a new instance of the {@link InformationalDialogScreen} class, providing the title of the dialog, the description text, and the parent screen to bind to.
     * @param title The title of the informational dialog.
     * @param text_lines An array of strings providing the description text lines. This must not be null, but can be an empty array.
     * @param parent The parent screen to get to after this dialog has exited. Can be {@code null}, which in such case the user will be lead to the main screen.
     * @throws ArgumentNullException {@code text_lines} parameter is {@code null}.
     */
    public InformationalDialogScreen(Component title, String[] text_lines, @AllowNull Screen parent)
            throws ArgumentNullException
    {
        this(title, CreateComponentFromText(text_lines), parent);
    }

    /**
     * Creates a new instance of the {@link InformationalDialogScreen} class, providing the description text, and the parent screen to bind to.
     * @param text_lines The description text. This must not be null.
     * @param parent The parent screen to get to after this dialog has exited. Can be {@code null}, which in such case the user will be lead to the main screen.
     * @throws ArgumentNullException {@code text_lines} parameter is {@code null}.
     */
    public InformationalDialogScreen(String text_lines, @AllowNull Screen parent)
        throws ArgumentNullException
    {
        this(Component.translatable("mdcdi1315_base_mods_lib.info_dialog_screen.default_title"), text_lines, parent);
    }

    /**
     * Creates a new instance of the {@link InformationalDialogScreen} class, providing the description text, and the parent screen to bind to.
     * @param text_lines An array of strings providing the description text lines. This must not be null, but can be an empty array.
     * @param parent The parent screen to get to after this dialog has exited. Can be {@code null}, which in such case the user will be lead to the main screen.
     * @throws ArgumentNullException {@code text_lines} parameter is {@code null}.
     */
    public InformationalDialogScreen(String[] text_lines, @AllowNull Screen parent)
            throws ArgumentNullException
    {
        this(Component.translatable("mdcdi1315_base_mods_lib.info_dialog_screen.default_title"), text_lines, parent);
    }

    private static Component CreateComponentFromText(String text)
    {
        ArgumentNullException.ThrowIfNull(text, "text");
        return Component.literal(text);
    }

    private static Component CreateComponentFromText(String[] text)
    {
        ArgumentNullException.ThrowIfNull(text, "text");
        var c = Component.empty();
        for (var line : text) { c.append(line).append("\n"); }
        return c;
    }

    private void OnOKButtonPressed(Button b)
    {
        if (b == ok_button) {
            onClose();
        }
    }

    @Override
    protected void RegisterButtonAreaControls(Action1<AbstractWidget> registrar)
    {
        registrar.action(ok_button);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Deprecated(forRemoval = true, since = "1.0.40-beta7")
    @APIStatus(Status = APIStatus.Status.DEPRECATED, Since = "1.0.40-beta7")
    public static void CreateDialog(Component title, String text_lines , @AllowNull Screen parent)
            throws ArgumentNullException
    {
        Minecraft.getInstance().setScreen(new InformationalDialogScreen(title , text_lines, parent));
    }

    @Deprecated(forRemoval = true, since = "1.0.40-beta7")
    @APIStatus(Status = APIStatus.Status.DEPRECATED, Since = "1.0.40-beta7")
    public static void CreateDialog(Component title, Component text_lines , @AllowNull Screen parent)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(text_lines, "text_lines");
        Minecraft.getInstance().setScreen(new InformationalDialogScreen(title , text_lines.getString(), parent));
    }

    @Deprecated(forRemoval = true, since = "1.0.40-beta7")
    @APIStatus(Status = APIStatus.Status.DEPRECATED, Since = "1.0.40-beta7")
    public static void CreateDialog(String text_lines , @AllowNull Screen parent)
            throws ArgumentNullException
    {
        Minecraft.getInstance().setScreen(new InformationalDialogScreen(text_lines, parent));
    }

    @Deprecated(forRemoval = true, since = "1.0.40-beta7")
    @APIStatus(Status = APIStatus.Status.DEPRECATED, Since = "1.0.40-beta7")
    public static void CreateDialog(Component text_lines , @AllowNull Screen parent)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(text_lines, "text_lines");
        Minecraft.getInstance().setScreen(new InformationalDialogScreen(text_lines.getString(), parent));
    }
}
