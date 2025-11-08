package com.github.mdcdi1315.basemodslib.client.gui;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;

/**
 * Provides a dialog screen for informing the user about an event.
 */
public final class InformationalDialogScreen
    extends ParentableScreen
{
    private Button ok_button;
    private int[] line_widths;
    private final String[] text_lines;
    private String title_component_string;
    private int text_color, title_component_string_length;

    /**
     * Creates a new instance of the {@link InformationalDialogScreen} class, providing the title of the dialog, the description text, and the parent screen to bind to.
     * @param title The title of the informational dialog.
     * @param text_lines The description text. This must not be null.
     * @param parent The parent screen to get to after this dialog has exited. Can be {@code null}, which in such case the user will be lead to the main screen.
     * @throws ArgumentNullException {@code text_lines} parameter is {@code null}.
     */
    public InformationalDialogScreen(Component title, String text_lines , @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        super(title, parent);
        ArgumentNullException.ThrowIfNull(text_lines, "text_lines");
        this.text_lines = text_lines.split("\n");
        ok_button = null;
        text_color = 0xFFFFFFFF; // White text
    }

    /**
     * Creates a new instance of the {@link InformationalDialogScreen} class, providing the title of the dialog, the description text, and the parent screen to bind to.
     * @param title The title of the informational dialog.
     * @param text_lines An array of strings providing the description text lines. This must not be null, but can be an empty array.
     * @param parent The parent screen to get to after this dialog has exited. Can be {@code null}, which in such case the user will be lead to the main screen.
     * @throws ArgumentNullException {@code text_lines} parameter is {@code null}.
     */
    public InformationalDialogScreen(Component title, String[] text_lines, @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        super(title, parent);
        ArgumentNullException.ThrowIfNull(text_lines, "text_lines");
        this.text_lines = text_lines;
        ok_button = null;
        text_color = 0xFFFFFFFF; // White text
    }

    /**
     * Creates a new instance of the {@link InformationalDialogScreen} class, providing the description text, and the parent screen to bind to.
     * @param text_lines The description text. This must not be null.
     * @param parent The parent screen to get to after this dialog has exited. Can be {@code null}, which in such case the user will be lead to the main screen.
     * @throws ArgumentNullException {@code text_lines} parameter is {@code null}.
     */
    public InformationalDialogScreen(String text_lines, @MaybeNull Screen parent)
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
    public InformationalDialogScreen(String[] text_lines, @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        this(Component.translatable("mdcdi1315_base_mods_lib.info_dialog_screen.default_title"), text_lines, parent);
    }

    /**
     * Sets the ARGB color for the description text.
     * @param color The color to apply for the description text.
     */
    public void SetTextColor(int color) {
        text_color = color;
    }

    /**
     * Gets the ARGB color for the description text.
     * @return The ARGB color currently set for the description text.
     */
    public int GetTextColor() {
        return text_color;
    }

    private void OnOKButtonPressed(Button b)
    {
        if (b == ok_button) {
            onClose();
        }
    }

    public void init()
    {
        if (ok_button == null)
        {
            int ok_button_x = (width / 2) - 35;
            if (ok_button_x < 0) {
                ok_button_x = 30;
            }
            ok_button = Button.builder(Component.translatable("mdcdi1315_base_mods_lib.info_dialog_screen.ok_button_text"), this::OnOKButtonPressed)
                    .bounds(ok_button_x, 0 , 70 , 25)
                    .build();
            addRenderableWidget(ok_button);
        }
        title_component_string_length = font.width(title_component_string = title.getString());
        line_widths = new int[text_lines.length];
        for (int I = 0; I < text_lines.length; I++) {
            line_widths[I] = font.width(text_lines[I]);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
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

        base_y += (font.lineHeight * 2);

        // Render description text lines.

        for (int I = 0; I < text_lines.length; I++)
        {
            string_x_pos = (width / 2) - (line_widths[I] / 2);
            gc.drawString(font, text_lines[I] , (string_x_pos < 0) ? 10 : string_x_pos, base_y, text_color);
            base_y += font.lineHeight;
        }

        ok_button.setY(base_y + (font.lineHeight * 2));

        // Render our button.
        super.render(gc, mouseX, mouseY, partialTick);
    }

    public static void CreateDialog(Component title, String text_lines , @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        Minecraft.getInstance().setScreen(new InformationalDialogScreen(title , text_lines, parent));
    }

    public static void CreateDialog(Component title, Component text_lines , @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(text_lines, "text_lines");
        Minecraft.getInstance().setScreen(new InformationalDialogScreen(title , text_lines.getString(), parent));
    }

    public static void CreateDialog(String text_lines , @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        Minecraft.getInstance().setScreen(new InformationalDialogScreen(text_lines, parent));
    }

    public static void CreateDialog(Component text_lines , @MaybeNull Screen parent)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(text_lines, "text_lines");
        Minecraft.getInstance().setScreen(new InformationalDialogScreen(text_lines.getString(), parent));
    }
}
