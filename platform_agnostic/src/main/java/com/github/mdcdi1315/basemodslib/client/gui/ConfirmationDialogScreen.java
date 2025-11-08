package com.github.mdcdi1315.basemodslib.client.gui;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;

import org.lwjgl.glfw.GLFW;

/**
 * Provides a dialog screen for taking from the user a response from the submitted question.
 */
public final class ConfirmationDialogScreen
    extends SelectiveParentableScreen
{
    private Button yes, no;
    private int[] line_widths;
    private final String[] lines;
    private final String title_component_string;
    private final Action1<DialogResult> on_complete;
    private int title_component_string_length, text_color;

    public ConfirmationDialogScreen(Component title, String confirmation_text, Action1<DialogResult> dialog_completed_action, @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        super(title, parent);
        ArgumentNullException.ThrowIfNull(dialog_completed_action, "dialog_completed_action");
        ArgumentNullException.ThrowIfNull(confirmation_text, "confirmation_text");
        lines = confirmation_text.split("\n");
        title_component_string = title.getString();
        on_complete = dialog_completed_action;
        title_component_string_length = 0;
        text_color = 0xFFFFFFFF; // White text
        no = yes = null;
    }

    public ConfirmationDialogScreen(Component title, String[] confirmation_text_lines, Action1<DialogResult> dialog_completed_action, @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        super(title, parent);
        ArgumentNullException.ThrowIfNull(dialog_completed_action, "dialog_completed_action");
        ArgumentNullException.ThrowIfNull(confirmation_text_lines, "confirmation_text_lines");
        lines = confirmation_text_lines;
        title_component_string = title.getString();
        on_complete = dialog_completed_action;
        title_component_string_length = 0;
        text_color = 0xFFFFFFFF; // White text
        no = yes = null;
    }

    public ConfirmationDialogScreen(String confirmation_text, Action1<DialogResult> dialog_completed_action, @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        this(Component.translatable("mdcdi1315_base_mods_lib.confirm_dialog_screen.default_title"), confirmation_text, dialog_completed_action, parent);
    }

    public ConfirmationDialogScreen(String[] confirmation_text_lines, Action1<DialogResult> dialog_completed_action, @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        this(Component.translatable("mdcdi1315_base_mods_lib.confirm_dialog_screen.default_title"), confirmation_text_lines, dialog_completed_action, parent);
    }

    public enum DialogResult { NO, YES }

    public void SetDescriptionTextColor(int color) {
        text_color = color;
    }

    public int GetDescriptionTextColor() {
        return text_color;
    }

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

    public void init()
    {
        if (no == null) // Only do this initialization if needed
        {
            int no_button_x = (width / 2) - 80;
            if (no_button_x < 0) {
                no_button_x = 30;
            }
            no = Button.builder(Component.translatable("mdcdi1315_base_mods_lib.confirm_dialog_screen.no_button_text"), this::OnButtonPressedHandler)
                    .bounds(no_button_x , 0, 70 , 25)
                    .build();
            yes = Button.builder(Component.translatable("mdcdi1315_base_mods_lib.confirm_dialog_screen.yes_button_text") , this::OnButtonPressedHandler)
                    .bounds(no_button_x + 80, 0, 70 ,25)
                    .build();
            addRenderableWidget(no);
            addRenderableWidget(yes);
        }
        title_component_string_length = font.width(title_component_string);
        line_widths = new int[lines.length];
        for (int I = 0; I < lines.length; I++) {
            line_widths[I] = font.width(lines[I]);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        // We handle this event specially, see keyPressed method
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            on_complete.action(DialogResult.NO); // To indicate that no selection was performed.
            onClose();
            return true;
        } else {
            // For all other cases route to the default handler
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void render(GuiGraphics gc, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(gc);

        // Render title

        int string_x_pos = (width / 2) - (title_component_string_length / 2), base_y = 60;
        // If somehow our width overflown because the string is too large (or our screen became too small), set position to a reasonable value instead.
        if (string_x_pos < 0) { string_x_pos = 10; }

        gc.drawString(font, title_component_string, string_x_pos, base_y , 0xFF00FF00); // Green text

        base_y += (font.lineHeight * 2); // we want to skip to two lines

        // Render description text lines.

        for (int I = 0; I < lines.length; I++)
        {
            string_x_pos = (width / 2) - (line_widths[I] / 2);
            gc.drawString(font, lines[I] , (string_x_pos < 0) ? 10 : string_x_pos, base_y, text_color);
            base_y += font.lineHeight;
        }

        base_y += font.lineHeight + 10;

        no.setY(base_y);
        yes.setY(base_y);

        // Render our buttons
        super.render(gc, mouseX, mouseY, partialTick);
    }

    public static void CreateDialog(Component title, String confirmation_text, @MaybeNull Screen parent, Action1<DialogResult> on_completed)
        throws ArgumentNullException
    {
        Minecraft.getInstance().setScreen(new ConfirmationDialogScreen(title, confirmation_text, on_completed, parent));
    }

    public static void CreateDialogSuppressSettingParent(Component title, String confirmation_text , Action1<DialogResult> on_completed)
            throws ArgumentNullException
    {
        var cds = new ConfirmationDialogScreen(title, confirmation_text, on_completed, null);
        cds.SuppressSettingParentScreenOnClose();
        Minecraft.getInstance().setScreen(cds);
    }

    public static void CreateDialogSuppressSettingParent(String confirmation_text , Action1<DialogResult> on_completed)
            throws ArgumentNullException
    {
        var cds = new ConfirmationDialogScreen(confirmation_text, on_completed, null);
        cds.SuppressSettingParentScreenOnClose();
        Minecraft.getInstance().setScreen(cds);
    }

    public static void CreateDialog(String confirmation_text, @MaybeNull Screen parent, Action1<DialogResult> on_completed)
            throws ArgumentNullException
    {
        Minecraft.getInstance().setScreen(new ConfirmationDialogScreen(confirmation_text, on_completed, parent));
    }
}
