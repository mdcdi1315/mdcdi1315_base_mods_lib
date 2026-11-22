package com.github.mdcdi1315.basemodslib.client.gui;

import com.github.mdcdi1315.DotNetLayer.System.TimeSpan;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.color.Colors;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.utils.color.ColorHelpers;
import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.components.AbstractStringWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.client.sounds.SoundManager;

import org.joml.Matrix3x2f;

/**
 * Provides a single-line text widget that is animated over a period of time while it is hovered. <br />
 * It is a tickable widget, so it requires calling it's {@link #Tick()} method to the screen's tick method.
 * @since 1.0.40-beta7
 */
@SuppressWarnings("NullableProblems")
@APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.40-beta7")
public class AnimatedTextWidget
    extends AbstractStringWidget
    implements ITickableGuiElement
{
    private int normal_color;
    private int hovered_color;
    private float hovered_scale;
    private int transition_ticks;
    private TextAlignment text_alignment;

    private int color_current;
    private float scale_current;
    private int current_transition_ticks;

    /**
     * Initializes a new instance of the {@link AnimatedTextWidget} class.
     * @param font The font referenced by Minecraft.
     */
    public AnimatedTextWidget(Font font)
    {
        super(0, 0, 0, font.lineHeight, Component.empty(), font);
        normal_color = Colors.White;
        text_alignment = TextAlignment.LEFT;
        scale_current = hovered_scale = 1.0F;
        SetTransitionTime(1000);
        color_current = hovered_color = Colors.Green;
    }

    /**
     * Sets the text to be displayed.
     * @param text The text that is subject to be displayed.
     * @throws ArgumentNullException {@code text} is {@code null}.
     */
    public final void setMessage(Component text)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(text, "text");
        super.setMessage(text);
        var color = text.getStyle().getColor();
        if (color != null) {
            normal_color = ColorHelpers.CreateARGB(255, color.getValue());
        }
    }

    /**
     * Sets the time, in milliseconds, of the animation.
     * @param time_milliseconds The duration of the animation in milliseconds.
     */
    @Pure
    public final void SetTransitionTime(int time_milliseconds)
    {
        time_milliseconds = Extensions.Max(0, time_milliseconds);
        transition_ticks = Extensions.Ceiling(((float)time_milliseconds / TimeSpan.MillisecondsPerSecond) * 20f);
        current_transition_ticks = 0;
    }

    /**
     * Sets a value that indicates the placement of the text inside the widget.
     * @param text_align The text alignment.
     * @throws ArgumentNullException {@code text_align} is {@code null}.
     */
    public final void SetTextAlign(TextAlignment text_align)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(text_align, "text_align");
        text_alignment = text_align;
    }

    /**
     * Gets the text that is to be displayed.
     * @return The text.
     */
    @Pure
    @NotNull
    public final Component GetText() { return message; }

    /**
     * Gets the color when the mouse is hovering over the text.
     * @return The color.
     */
    @Pure
    public final int GetHoverColor() { return hovered_color; }

    /**
     * Gets the scale of the text when the mouse is hovering over the text.
     * @return The text scale.
     */
    @Pure
    public final float GetHoverScale() { return hovered_scale; }

    /**
     * Gets a value that indicates the placement of the text inside the widget.
     * @return The placement of the text.
     */
    @Pure
    @NotNull
    public final TextAlignment GetTextAlign() { return text_alignment; }

    /**
     * Sets the scale of the text when the mouse is hovering over the text.
     * @param hover_scale The scale of the text while the mouse is hovering over the text.
     */
    @Pure
    public final void SetHoverScale(float hover_scale) { this.hovered_scale = hover_scale; }

    /**
     * Sets the text to be displayed.
     * @param text The text that is subject to be displayed.
     * @throws ArgumentNullException {@code text} is {@code null}.
     */
    public final void SetText(Component text) throws ArgumentNullException { setMessage(text); }

    /**
     * Sets the color when the mouse is hovering over the text.
     * @param hover_color The ARGB color.
     */
    @Pure
    public final void SetHoverColor(int hover_color) { this.hovered_color = 255 << 24 | hover_color; }

    /**
     * Gets the time, in milliseconds, of the animation.
     * @return The duration of the animation in milliseconds.
     */
    @Pure
    public final int GetTransitionTime() { return Extensions.Ceiling((transition_ticks / 20f) * TimeSpan.MillisecondsPerSecond); }

    /**
     * Resets the animation time back to zero. <br />
     * Useful when the screen has been re-initialized.
     */
    @Pure
    public void ResetTickTime()
    {
        scale_current = 1f;
        color_current = normal_color;
        current_transition_ticks = 0;
    }

    @Override
    public void playDownSound(SoundManager sm) { }

    @Override
    public void visitLines(ActiveTextCollector atc)
    {
        var parameters = atc.defaultParameters();
        int y_scale_offset = Extensions.Ceiling(getFont().lineHeight * scale_current);

        parameters = parameters
                .withPose(parameters.pose().translate(getX(), getY() + y_scale_offset, new Matrix3x2f()))
                .withScale(scale_current);

        atc.accept(text_alignment, 0, -y_scale_offset, parameters, message.copy().withColor(color_current));
    }

    @Override
    public void Tick()
    {
        current_transition_ticks += isHovered ? ((current_transition_ticks < transition_ticks) ? 1 : 0) : ((current_transition_ticks > 0) ? -1 : 0);
        float current_transition_time = current_transition_ticks / (float)transition_ticks;
        scale_current = Extensions.Lerp(current_transition_time, 1f, hovered_scale);
        color_current = ColorHelpers.Lerp(current_transition_time, normal_color, hovered_color);
    }
}
