package com.github.mdcdi1315.basemodslib.client.gui;

import com.github.mdcdi1315.DotNetLayer.System.AppContext;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.RuntimeFeature;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.VersionInfo;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.mods.ModInstanceCollection;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.MultiLineTextWidget;

import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;

public final class AboutBMLScreen
    extends ParentableScreen
{
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            BaseModsLib.MOD_ID,
            "textures/gui/bml_about_screen_bg.png"
    );

    private int tick_width;
    private int title_width;
    private final Button button;
    private final LinearLayout layout;
    private boolean tick_width_increment;

    public AboutBMLScreen(Screen parent)
    {
        super(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.title"), parent);
        button = Button.builder(
                Component.literal("Return back"),
                this::OnClose
        ).build();
        tick_width = 0;
        tick_width_increment = true;
        layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL).spacing(10);
        InitLayout();
    }

    private void InitLayout()
    {
        var first_column = layout.addChild(LinearLayout.vertical().spacing(5));
        first_column.addChild(new MultiLineTextWidget(
                Component.literal(
                        String.format(
"""
================================================
                BML Information
================================================

BML Version: %s
BML Build Time: %s
BML Release Series: %s
Minecraft Version: %s
Mod Loader: %s
Mod Loader Version: %s
Java Runtime: %s
Is Development Environment: %s
""",
                                VersionInfo.GetPropertyOrEmpty(VersionInfo.PROPERTY_VERSION),
                                VersionInfo.GetPropertyOrEmpty(VersionInfo.PROPERTY_BUILD_TIME),
                                VersionInfo.GetPropertyOrEmpty(VersionInfo.PROPERTY_RELEASE_TYPE),
                                BaseModsLib.GetMinecraftVersion(),
                                BaseModsLib.GetModLoaderBranding(),
                                BaseModsLib.GetModLoaderVersion(),
                                BaseModsLib.GetJavaVersion(),
                                BaseModsLib.IsDevelopmentEnvironment() ? "Yes" : "No"
                        )
                ),
                minecraft.font
        ));
        first_column.addChild(
                new MultiLineTextWidget(
                        Component.literal(
                                String.format(
"""
================================================
            Mod Instance Information
================================================

Loaded Server Mod Instances: %d
Loaded Client Mod Instances: %d

================================================
            Server Mod Instances
================================================
%s
================================================
            Client Mod Instances
================================================
%s
""",
                                        BaseModsLib.GetModInstanceCollection().getCount(),
                                        BaseModsLibClient.GetModInstances().getCount(),
                                        GetModInstanceInfo(BaseModsLib.GetModInstanceCollection()),
                                        GetModInstanceInfo(BaseModsLibClient.GetModInstances())
                                )
                        ),
                        minecraft.font
                )
        );

        var second_column = layout.addChild(LinearLayout.vertical().spacing(5));
        second_column.addChild(
                new MultiLineTextWidget(
                        Component.literal(
                                String.format(
"""
================================================
                  .NET Layer
================================================

Target Framework Name: %s
App Context Base Directory: %s
""",
                                        AppContext.GetTargetFrameworkName(),
                                        AppContext.GetBaseDirectory()
                                )
                        ),
                        minecraft.font
                )
        );

        var third_column = layout.addChild(LinearLayout.vertical().spacing(5));
        third_column.addChild(
                new StringWidget(Component.literal("Supported .NET Layer Features: "), minecraft.font)
        );
        for (var s : new String[] {
                RuntimeFeature.PortablePdb,
                RuntimeFeature.DefaultImplementationsOfInterfaces,
                RuntimeFeature.UnmanagedSignatureCallingConvention,
                RuntimeFeature.CovariantReturnsOfClasses,
                RuntimeFeature.ByRefFields,
                RuntimeFeature.ByRefLikeGenerics,
                RuntimeFeature.VirtualStaticsInInterfaces,
                RuntimeFeature.NumericIntPtr
        })
        {
            third_column.addChild(
                    new StringWidget(Component.literal(
                            String.format(
                                    "Feature %s: %s",
                                    s,
                                    RuntimeFeature.IsSupported(s) ? "Yes" : "No"
                            )
                    ), minecraft.font)
            );
        }
    }

    private static String GetModInstanceInfo(ModInstanceCollection<?> collection)
    {
        if (collection.getCount() == 0) {
            return "<No mod instances were found>";
        } else {
            StringBuilder b = new StringBuilder();
            try (var en = collection.GetEnumerator())
            {
                while (en.MoveNext())
                {
                    var item = en.getCurrent();
                    b.append("ID: ");
                    b.append(item.GetModId());
                    b.append("\nClass: ");
                    b.append(item.getClass());
                    b.append("\nHash Code: ");
                    b.append(item.hashCode());
                    b.append('\n');
                }
            }
            return b.toString();
        }
    }

    private void OnClose(Button b) { this.onClose(); }

    @Override
    public void init()
    {
        int g;
        title_width = minecraft.font.width(title);
        g = (width - button.getWidth()) / 2;
        if (g < 0) { g = button.getWidth(); }
        button.setX(g);
        g = height - 16 - button.getHeight();
        if (g < 0) { g = button.getHeight(); }
        button.setY(g);
        addRenderableWidget(button);
        tick_width = (width - title_width) / 2;
        tick_width_increment = System.currentTimeMillis() % 8 < 4;

        var sl = new ScrollableLayout(minecraft, layout, 0);
        sl.setX(30);
        sl.setY(35);
        sl.setMinWidth(width - 80);
        g = button.getY() - button.getHeight() - 50;
        sl.setMaxHeight(g);
        sl.setMinHeight(g);
        sl.visitWidgets(this::addRenderableWidget);
        sl.arrangeElements();
    }

    @Override
    public void tick()
    {
        int bound = width - title_width;
        if (tick_width_increment) { tick_width++; } else { tick_width--; }
        if (tick_width < 0) { tick_width = 0; tick_width_increment = true; }
        if (tick_width > bound) { tick_width = bound; tick_width_increment = false; }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        graphics.text(minecraft.font, title, tick_width, 10, 0xFF000000);

        graphics.fillGradient(
                30,
                35,
                width - 30,
                button.getY() - button.getHeight() - 15,
                0x78000000,
                0x78000000
        );

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gc, int mouseX, int mouseY, float a)
    {
        if (minecraft.level == null) {
            gc.blit(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    0,
                    0,
                    0f,
                    0f,
                    width,
                    height,
                    1920,
                    1081,
                    1920,
                    1081
            );
        } else {
            this.extractBlurredBackground(gc);
        }
    }
}
