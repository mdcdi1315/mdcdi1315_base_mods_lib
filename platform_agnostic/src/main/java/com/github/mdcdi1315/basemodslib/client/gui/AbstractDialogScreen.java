package com.github.mdcdi1315.basemodslib.client.gui;

import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

/**
 * Provides a base class for creating dialog screens.
 * @since 1.0.40-beta7
 */
@APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.40-beta7")
public abstract class AbstractDialogScreen
    extends SelectiveParentableScreen
{
    public static final int DEFAULT_BUTTON_SPACING = 15;
    public static final int DEFAULT_TITLE_Y_POSITION = 60;
    public static final int DEFAULT_TITLE_COLOR = 0xFF00FF00; // Green text

    private int title_color;
    private int title_y_pos;
    private int button_spacing;
    private boolean buttons_not_added;
    private final MultiLineTextWidget text;
    private final LinearLayout button_layout;
    private final LayoutSettings button_layout_settings;

    private int title_component_width;

    /**
     * Initializes a new instance of the {@link AbstractDialogScreen} class.
     * @param title The title of the dialog.
     * @param parent The parent screen, if any. Otherwise, {@code null}.
     */
    public AbstractDialogScreen(Component title, @AllowNull Screen parent)
    {
        super(title, parent);
        buttons_not_added = true;
        title_component_width = 0;
        title_color = DEFAULT_TITLE_COLOR;
        title_y_pos = DEFAULT_TITLE_Y_POSITION;
        button_spacing = DEFAULT_BUTTON_SPACING;
        text = new MultiLineTextWidget(Component.empty(), font);
        button_layout = LinearLayout.horizontal().spacing(button_spacing);
        button_layout_settings = LayoutSettings.defaults().alignHorizontallyCenter();
        text.setCentered(true);
    }

    private void RegisterToLayout(AbstractWidget widget) { button_layout.addChild(widget, button_layout_settings); }

    /**
     * Provides the way for registering button controls for the dialog.
     * @param registrar The method that registers controls.
     */
    protected abstract void RegisterButtonAreaControls(Action1<AbstractWidget> registrar);

    /**
     * Changes the informational text displayed to the user.
     * @param text The informational text.
     * @throws ArgumentNullException {@code text} is {@code null}.
     */
    protected final void SetText(Component text)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(text, "text");
        this.text.setMessage(text);
    }

    /**
     * Gets the ARGB color of the title's text.
     * @return The ARGB color of the title.
     */
    @Pure
    public final int GetTitleColor() { return title_color; }

    /**
     * Gets the Y position where the title text is placed to.
     * @return The Y position of the title text.
     */
    @Pure
    public final int GetTitleYPosition() { return title_y_pos; }

    /**
     * Gets the value, in pixels, between each button control registered through the {@link #RegisterButtonAreaControls(Action1)} method.
     * @return The value in pixels, between each button control.
     */
    @Pure
    public final int GetButtonSpacing() { return button_spacing; }

    /**
     * Sets the ARGB color of the title's text.
     * @param color The new ARGB color of the title's text.
     * @apiNote The default value is {@link #DEFAULT_TITLE_COLOR}.
     */
    @Pure
    public final void SetTitleColor(int color) { title_color = color; }

    /**
     * Gets the informational text displayed to the user.
     * @return The informational text currently displayed.
     */
    @NotNull
    public final Component GetText() { return this.text.getMessage(); }

    /**
     * Sets the Y position where the title text is placed to.
     * @param y The new Y position where the title text is to be placed to.
     * @apiNote The default value is {@link #DEFAULT_TITLE_Y_POSITION}.
     */
    @Pure
    public final void SetTitleYPosition(int y) { title_y_pos = Extensions.Max(0, y); }

    /**
     * Sets the value, in pixels, between each button control registered through the {@link #RegisterButtonAreaControls(Action1)} method.
     * @param spacing The new value in pixels, between each button control.
     * @apiNote The default value is {@link #DEFAULT_BUTTON_SPACING}.
     */
    @Pure
    public final void SetButtonSpacing(int spacing) { button_spacing = Extensions.Max(0, spacing); }

    /**
     * Sets the ARGB color for the description text.
     * @param color The color to apply for the description text.
     */
    public final void SetDescriptionTextColor(int color) { SetText(GetText().copy().withColor(color)); }

    /**
     * Gets the ARGB color for the description text.
     * @return The ARGB color currently set for the description text.
     */
    public final int GetDescriptionTextColor()
    {
        TextColor tc = GetText().getStyle().getColor();
        return (tc == null) ? 0 : tc.getValue();
    }

    // #region Overrides

    @Override
    protected void init()
    {
        super.init();

        button_layout.spacing(button_spacing);

        if (buttons_not_added)
        {
            buttons_not_added = false;
            RegisterButtonAreaControls(this::RegisterToLayout);
        }

        title_component_width = font.width(title);

        int y = title_y_pos + font.lineHeight * 2;

        text.setY(y);
        text.setX((width - text.getWidth()) / 2);
        addRenderableWidget(text);

        button_layout.arrangeElements();
        button_layout.setX((width - button_layout.getWidth()) / 2);
        button_layout.setY(y + text.getHeight() + font.lineHeight + 10);
        button_layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gc, int mouseX, int mouseY, float partial_ticks)
    {
        gc.text(font, title, (width - title_component_width) / 2, title_y_pos, title_color);

        super.extractRenderState(gc, mouseX, mouseY, partial_ticks);
    }

    // #endregion
}
