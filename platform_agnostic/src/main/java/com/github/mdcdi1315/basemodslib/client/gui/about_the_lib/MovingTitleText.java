package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.client.gui.ITickableGuiElement;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;

import net.minecraft.network.chat.Component;

public final class MovingTitleText
    implements Renderable, ITickableGuiElement
{
    private int color;
    private Font font;
    private int base_y;
    private int run_width;
    private Component text;
    private int tick_width;
    private int text_length;
    private boolean tick_width_increment;

    public MovingTitleText()
    {
        this.base_y = 0;
        this.font = null;
        this.run_width = 0;
        this.tick_width = 0;
        this.text_length = 0;
        this.color = 0xFF000000;
        this.text = Component.empty();
        this.tick_width_increment = false;
    }

    @Pure
    public int GetColor() { return this.color; }

    @Pure
    public void SetColor(int color) { this.color = color; }

    public void SetText(Component text)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(text, "text");
        this.text = text;
    }

    @Pure
    @NotNull
    public Component GetText() { return this.text; }

    public void Init(int width, int render_y, Font font)
    {
        this.font = font;
        this.run_width = width;
        this.base_y = render_y;
        this.text_length = this.font.width(text);
        this.tick_width = (run_width - text_length) / 2;
        this.tick_width_increment = System.currentTimeMillis() % 12 < 7;
    }

    @Pure
    @Override
    public void Tick()
    {
        int bound = run_width - text_length;
        if (tick_width_increment) { tick_width++; } else { tick_width--; }
        if (tick_width < 0) { tick_width = 0; tick_width_increment = true; }
        if (tick_width > bound) { tick_width = bound; tick_width_increment = false; }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gc, int mouse_x, int mouse_y, float partial_ticks)
    {
        gc.text(font, text, tick_width, base_y, color, false);
    }
}
