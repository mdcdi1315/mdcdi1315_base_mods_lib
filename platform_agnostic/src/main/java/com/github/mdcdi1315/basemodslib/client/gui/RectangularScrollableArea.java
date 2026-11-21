package com.github.mdcdi1315.basemodslib.client.gui;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.events.ContainerEventHandler;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.input.MouseButtonEvent;

import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Provides an easy-to-use scrollable area widget, bounded in a rectangle.
 */
@SuppressWarnings("NullableProblems")
public class RectangularScrollableArea
    extends AbstractScrollArea
    implements ContainerEventHandler
{
    private static final int SCROLLBAR_MIN_HEIGHT = 32;

    public static final int DEFAULT_SPACING = 5;
    public static final int DEFAULT_X_PADDING = 0;
    public static final Identifier SCROLLER_SPRITE = RegistryUtils.ConstructResourceLocation(Identifier.DEFAULT_NAMESPACE, "widget/scroller");
    public static final Identifier SCROLLER_DISABLED_SPRITE = RegistryUtils.ConstructResourceLocation(BaseModsLib.MOD_ID, "scroller_disabled");
    public static final Identifier SCROLLER_BACKGROUND_SPRITE = RegistryUtils.ConstructResourceLocation(Identifier.DEFAULT_NAMESPACE, "widget/scroller_background");

    private int spacing;
    private int x_padding;
    private boolean dragging;
    private int total_height;
    private int background_color;
    private GuiEventListener focused;
    private final List<AbstractWidget> widgets;
    private int rendering_start_index, rendering_end_index;

    /**
     * Creates a new rectangular, scrollable area widget.
     * @param width The width of the area.
     * @param height The height of the area.
     */
    public RectangularScrollableArea(int width, int height) { this(width, height, null); }

    /**
     * Creates a new rectangular, scrollable area widget.
     * @param width The width of the area.
     * @param height The height of the area.
     * @param settings Settings that allow to customize the scrollbar.
     */
    public RectangularScrollableArea(int width, int height, @AllowNull ScrollbarSettings settings)
    {
        super(
                0, 0,
                width, height,
                Component.literal("Rectangular Scrollable Area"),
                SelectedSettingsOrDefault(settings)
        );

        focused = null;
        dragging = false;
        rendering_end_index = -1;
        rendering_start_index = 0;
        spacing = DEFAULT_SPACING;
        widgets = new ArrayList<>();
        x_padding = DEFAULT_X_PADDING;
        background_color = 0x78000000;
    }

    /**
     * Initializes the scrollable area and prepares it's containing widgets for rendering.
     * @apiNote It is allowed to override this method for providing custom additional
     * code to execute. Note that you must still call this method using the {@code super} keyword.
     */
    public void Initialize()
    {
        total_height = 0;
        for (var w : widgets) { total_height += w.getHeight() + spacing; }

        ReorderWidgets();
    }

    /**
     * Adds a new widget to be contained in this scrollable area.
     * @param widget The widget to add.
     * @throws ArgumentNullException {@code widget} is {@code null}.
     * @apiNote Do not modify X and Y positions to the widget you just passed in;
     * the positioning is done by the object itself during {@link #Initialize()} and
     * when the scroll bar is moved.
     */
    public final void RegisterWidget(AbstractWidget widget)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(widget, "widget");
        widgets.add(widget);
    }

    /**
     * Gets the vertical spacing, in pixels, between each widget.
     * @return The vertical spacing currently specified.
     * @apiNote The default value is {@link #DEFAULT_SPACING}.
     */
    @Pure
    public final int GetSpacing() { return spacing; }

    /**
     * Gets the horizontal spacing, in pixels, between the left edge of the area and the widgets.
     * @return The horizontal spacing currently specified.
     * @apiNote The default value is {@link #DEFAULT_X_PADDING}.
     */
    @Pure
    public final int GetXPadding() { return x_padding; }

    /**
     * Gets the background color that this area will have.
     * @return The background color.
     */
    @Pure
    public final int GetBackgroundColor() { return background_color; }

    /**
     * Sets the background color that this area will have.
     * @param color The new background color.
     */
    @Pure
    public final void SetBackgroundColor(int color) { background_color = color; }

    /**
     * Sets the vertical spacing, in pixels, between each widget.
     * @param spacing The new vertical spacing, in pixels.
     * @apiNote The default value is {@link #DEFAULT_SPACING}.
     */
    @Pure
    public final void SetSpacing(int spacing) { this.spacing = Extensions.Max(0, spacing); }

    /**
     * Sets the horizontal spacing, in pixels, between the left edge of the area and the widgets.
     * @param x_padding The new horizontal spacing.
     * @apiNote The default value is {@link #DEFAULT_X_PADDING}.
     */
    @Pure
    public final void SetXPadding(int x_padding) { this.x_padding = Extensions.Max(0, x_padding); }

    private void ReorderWidgets()
    {
        int w_height;
        int y = getY();
        int end_index = -1;
        int start_index = 0;
        int x = getX() + x_padding;
        int cursor = y - (int)scrollAmount();

        for (var widget : widgets)
        {
            w_height = widget.getHeight() + spacing;
            widget.setX(x);
            widget.setY(cursor);
            if (cursor < -w_height) {
                end_index++;
                start_index++;
            } else if ((cursor-y) <= height) {
                end_index++;
            } else {
                break;
            }
            cursor += w_height;
        }

        rendering_end_index = end_index;
        rendering_start_index = start_index;
    }

    @Pure
    @NotNull
    @SuppressWarnings("ReplaceNullCheck")
    private static ScrollbarSettings SelectedSettingsOrDefault(@AllowNull ScrollbarSettings settings)
    {
        if (settings == null) {
            return new ScrollbarSettings(
                    SCROLLER_SPRITE,
                    SCROLLER_DISABLED_SPRITE,
                    SCROLLER_BACKGROUND_SPRITE,
                    SCROLLBAR_WIDTH,
                    SCROLLBAR_MIN_HEIGHT,
                    3,
                    true
            );
        } else {
            return settings;
        }
    }

    // #region Overrides

    @Override
    public void playDownSound(SoundManager sm) { }

    @Override
    public boolean isDragging() { return dragging; }

    @Override
    public void setDragging(boolean b) { dragging = b; }

    @Override
    protected int contentHeight() { return total_height; }

    @Override
    public GuiEventListener getFocused() { return focused; }

    @NotNull
    @Override
    public List<? extends GuiEventListener> children() { return widgets; }

    @NotNull
    @Override
    protected MutableComponent createNarrationMessage() { return message.copy(); }

    @Override
    @MaybeNull
    public ComponentPath nextFocusPath(FocusNavigationEvent fne) { return ContainerEventHandler.super.nextFocusPath(fne); }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput neo)
    {
        if (rendering_end_index > -1)
        {
            widgets.get(rendering_start_index).updateNarration(neo);
        }
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor gc, int mouse_x, int mouse_y, float partial_ticks)
    {
        int x = this.getX(), y = this.getY();

        gc.fillGradient(
                x,
                y,
                scrollBarX(),
                getBottom(),
                background_color,
                background_color
        );

        extractScrollbar(gc, mouse_x, mouse_y);

        gc.enableScissor(x, y, scrollBarX(), getBottom());

        for (int I = rendering_start_index; I <= rendering_end_index; I++)
        {
            widgets.get(I).extractRenderState(gc, mouse_x, mouse_y, partial_ticks);
        }

        gc.disableScissor();
    }

    @Override
    public void mouseMoved(double x, double y)
    {
        for (int I = rendering_start_index; I <= rendering_end_index; I++)
        {
            widgets.get(I).mouseMoved(x, y);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if (this.isActive()) {
            boolean scrolling = this.updateScrolling(event);
            super.mouseClicked(event, doubleClick);
            return ContainerEventHandler.super.mouseClicked(event, doubleClick) || scrolling;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY)
    {
        return ContainerEventHandler.super.mouseScrolled(mx, my, scrollX, scrollY) ||
                super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        super.mouseReleased(event);
        return ContainerEventHandler.super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy)
    {
        super.mouseDragged(event, dx, dy);
        return ContainerEventHandler.super.mouseDragged(event, dx, dy);
    }

    @Override
    public void setScrollAmount(double scrollAmount)
    {
        super.setScrollAmount(scrollAmount);
        ReorderWidgets();
    }

    @NotNull
    @Override
    public Optional<GuiEventListener> getChildAt(double x, double y)
    {
        GuiEventListener child;
        for (int I = rendering_start_index; I <= rendering_end_index; I++)
        {
            if ((child = widgets.get(I)).isMouseOver(x, y)) { return Optional.of(child); }
        }
        return Optional.empty();
    }

    @Override
    public void setFocused(GuiEventListener listener)
    {
        if (this.focused != listener)
        {
            if (this.focused != null) { this.focused.setFocused(false); }

            if ((this.focused = listener) != null) { listener.setFocused(true); }
        }
    }

    // #endregion
}
