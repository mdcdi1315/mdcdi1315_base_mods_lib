package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.color.Colors;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;

import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public final class MessageReporterToast
    implements Toast
{
    public static final int REPORT_TIME = 12500;
    private static final Identifier BACKGROUND_SPRITE = RegistryUtils.ConstructResourceLocation(Identifier.DEFAULT_NAMESPACE, "toast/system");

    private int width;
    private boolean computed;
    private final Component title;
    private final Component message;
    private Toast.Visibility wanted_visibility;
    private List<FormattedCharSequence> message_lines;

    public MessageReporterToast(Component title, Component message)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(title, "title");
        ArgumentNullException.ThrowIfNull(message, "message");
        this.title = title;
        this.computed = false;
        this.message = message;
        this.message_lines = List.of();
        this.wanted_visibility = Visibility.SHOW;
    }

    @Override
    public int width() { return this.width; }

    @Override
    public Visibility getWantedVisibility() { return wanted_visibility; }

    @Override
    public int height() { return 20 + Extensions.Max(this.message_lines.size(), 1) * 12; }

    @Override
    public void update(ToastManager tm, long fully_visible_for_ms)
    {
        if (!computed)
        {
            var font = tm.getMinecraft().font;
            this.width = Extensions.Max(180, (this.message_lines = font.split(message, 180)).stream().mapToInt(font::width).max().orElse(180));
            computed = true;
        }

        wanted_visibility = (fully_visible_for_ms < REPORT_TIME * tm.getNotificationDisplayTimeMultiplier()) ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gc, Font font, long fully_visible_for_ms)
    {
        gc.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        if (this.message_lines.isEmpty()) {
            gc.text(font, this.title, 18, 12, Colors.Yellow, false);
        } else {
            gc.text(font, this.title, 18, 7, Colors.Yellow, false);

            for (int i = 0; i < this.message_lines.size(); i++)
            {
                gc.text(font, this.message_lines.get(i), 18, 18 + i * 12, Colors.White, false);
            }
        }
    }
}
