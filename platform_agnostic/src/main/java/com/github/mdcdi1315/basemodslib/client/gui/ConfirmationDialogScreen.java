package com.github.mdcdi1315.basemodslib.client.gui;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;

import org.lwjgl.glfw.GLFW;

/**
 * Provides a dialog screen for taking from the user a response from the submitted question.
 */
@APIStatus(Status = APIStatus.Status.STABLE)
public final class ConfirmationDialogScreen
    extends AbstractDialogScreen
{
    private final Button yes, no;
    private final Action1<DialogResult> on_complete;

    @APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.40-beta7")
    public ConfirmationDialogScreen(Component title, Component confirmation_text, Action1<DialogResult> dialog_completed_action, @AllowNull Screen parent)
            throws ArgumentNullException
    {
        super(title, parent);
        ArgumentNullException.ThrowIfNull(confirmation_text, "confirmation_text");
        ArgumentNullException.ThrowIfNull(dialog_completed_action, "dialog_completed_action");
        SetText(confirmation_text);
        on_complete = dialog_completed_action;
        no = Button.builder(Component.translatable("mdcdi1315_base_mods_lib.confirm_dialog_screen.no_button_text"), this::OnButtonPressedHandler)
                .size(70 , 25)
                .build();
        yes = Button.builder(Component.translatable("mdcdi1315_base_mods_lib.confirm_dialog_screen.yes_button_text") , this::OnButtonPressedHandler)
                .size(70 ,25)
                .build();
    }

    @APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.40-beta7")
    public ConfirmationDialogScreen(Component confirmation_text, Action1<DialogResult> dialog_completed_action, @AllowNull Screen parent)
            throws ArgumentNullException
    {
        this(Component.translatable("mdcdi1315_base_mods_lib.confirm_dialog_screen.default_title"), confirmation_text, dialog_completed_action, parent);
    }

    public ConfirmationDialogScreen(Component title, String confirmation_text, Action1<DialogResult> dialog_completed_action, @AllowNull Screen parent)
            throws ArgumentNullException
    {
        this(title, CreateComponentFromText(confirmation_text), dialog_completed_action, parent);
    }

    public ConfirmationDialogScreen(Component title, String[] confirmation_text_lines, Action1<DialogResult> dialog_completed_action, @AllowNull Screen parent)
            throws ArgumentNullException
    {
        this(title, CreateComponentFromText(confirmation_text_lines), dialog_completed_action, parent);
    }

    public ConfirmationDialogScreen(String confirmation_text, Action1<DialogResult> dialog_completed_action, @AllowNull Screen parent)
            throws ArgumentNullException
    {
        this(Component.translatable("mdcdi1315_base_mods_lib.confirm_dialog_screen.default_title"), confirmation_text, dialog_completed_action, parent);
    }

    public ConfirmationDialogScreen(String[] confirmation_text_lines, Action1<DialogResult> dialog_completed_action, @AllowNull Screen parent)
            throws ArgumentNullException
    {
        this(Component.translatable("mdcdi1315_base_mods_lib.confirm_dialog_screen.default_title"), confirmation_text_lines, dialog_completed_action, parent);
    }

    public enum DialogResult { NO, YES }

    private void OnButtonPressedHandler(Button b)
    {
        if (b == no) {
            on_complete.action(DialogResult.NO);
            onClose();
        } else if (b == yes) {
            on_complete.action(DialogResult.YES);
            onClose();
        }
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

    // #region Overrides

    @Override
    protected void RegisterButtonAreaControls(Action1<AbstractWidget> registrar)
    {
        registrar.action(no);
        registrar.action(yes);
    }

    @Override
    public boolean shouldCloseOnEsc()
    {
        // We handle this event specially, see keyPressed method
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            on_complete.action(DialogResult.NO); // To indicate that no selection was performed.
            onClose();
            return true;
        } else {
            // For all other cases route to the default handler
            return super.keyPressed(event);
        }
    }

    // #endregion

    @Deprecated(forRemoval = true, since = "1.0.40-beta7")
    @APIStatus(Status = APIStatus.Status.DEPRECATED, Since = "1.0.40-beta7")
    public static void CreateDialog(Component title, String confirmation_text, @AllowNull Screen parent, Action1<DialogResult> on_completed)
        throws ArgumentNullException
    {
        Minecraft.getInstance().setScreen(new ConfirmationDialogScreen(title, confirmation_text, on_completed, parent));
    }

    @Deprecated(forRemoval = true, since = "1.0.40-beta7")
    @APIStatus(Status = APIStatus.Status.DEPRECATED, Since = "1.0.40-beta7")
    public static void CreateDialogSuppressSettingParent(Component title, String confirmation_text, Action1<DialogResult> on_completed)
            throws ArgumentNullException
    {
        var cds = new ConfirmationDialogScreen(title, confirmation_text, on_completed, null);
        cds.SuppressSettingParentScreenOnClose();
        Minecraft.getInstance().setScreen(cds);
    }

    @Deprecated(forRemoval = true, since = "1.0.40-beta7")
    @APIStatus(Status = APIStatus.Status.DEPRECATED, Since = "1.0.40-beta7")
    public static void CreateDialogSuppressSettingParent(String confirmation_text, Action1<DialogResult> on_completed)
            throws ArgumentNullException
    {
        var cds = new ConfirmationDialogScreen(confirmation_text, on_completed, null);
        cds.SuppressSettingParentScreenOnClose();
        Minecraft.getInstance().setScreen(cds);
    }

    @Deprecated(forRemoval = true, since = "1.0.40-beta7")
    @APIStatus(Status = APIStatus.Status.DEPRECATED, Since = "1.0.40-beta7")
    public static void CreateDialog(String confirmation_text, @AllowNull Screen parent, Action1<DialogResult> on_completed)
            throws ArgumentNullException
    {
        Minecraft.getInstance().setScreen(new ConfirmationDialogScreen(confirmation_text, on_completed, parent));
    }
}
