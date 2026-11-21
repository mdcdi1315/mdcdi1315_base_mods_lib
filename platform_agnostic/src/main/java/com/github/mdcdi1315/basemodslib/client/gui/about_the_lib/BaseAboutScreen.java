package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.registries.RegistryUtils;
import com.github.mdcdi1315.basemodslib.client.gui.ParentableScreen;
import com.github.mdcdi1315.basemodslib.client.gui.RectangularScrollableArea;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;

import net.minecraft.network.chat.Component;

import net.minecraft.resources.Identifier;

public abstract class BaseAboutScreen
    extends ParentableScreen
{
    public static final int WORK_AREA_X_POINT = 30;
    public static final int WORK_AREA_Y_POINT = 35;

    private static final int BKGD_TEXTURE_WIDTH = 1920;
    private static final int BKGD_TEXTURE_HEIGHT = 1081;

    private static final Identifier BKGD_TEXTURE = RegistryUtils.ConstructResourceLocation(
            BaseModsLib.MOD_ID,
            "textures/gui/bml_about_screen_bg.png"
    );

    private final Button exit_button;
    private final MovingTitleText title_text_ctl;
    private final RectangularScrollableArea main_area;

    public BaseAboutScreen(Component title, Screen parent)
    {
        super(title, parent);

        title_text_ctl = new MovingTitleText();
        title_text_ctl.SetText(this.title);

        main_area = new RectangularScrollableArea(0, 0);
        main_area.setPosition(WORK_AREA_X_POINT, WORK_AREA_Y_POINT);
        main_area.SetXPadding(1);

        exit_button = Button.builder(
                Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.exit_button_text"),
                this::OnClose_ExitButton
        ).build();
    }

    private static EqualSpacingLayout CreateChildLayout(LinearLayout parent, int layout_width, int layout_height)
    {
        return parent.addChild(new EqualSpacingLayout(
                layout_width,
                layout_height,
                EqualSpacingLayout.Orientation.HORIZONTAL
        ));
    }

    private void OnClose_ExitButton(Button b) { this.onClose(); }

    public final int ComputeWorkAreaWidth() { return width - WORK_AREA_X_POINT * 2; }

    public final int ComputeWorkAreaHeight() { return height - WORK_AREA_Y_POINT * 3; }

    public final int BoundToMaxOfWorkArea(int desired_width) { return Extensions.Min(main_area.getWidth(), desired_width); }

    @Pure
    public final void SetTitleTextColor(int color) { title_text_ctl.SetColor(color); }

    @Pure
    protected final void SetWorkAreaSpacing(int spacing) { main_area.SetSpacing(spacing); }

    protected final <T extends AbstractWidget> T AddWorkAreaWidget(T w) { main_area.RegisterWidget(w); return w; }

    protected final void HardReinitializeWorkArea()
    {
        main_area.setWidth(ComputeWorkAreaWidth());
        main_area.setHeight(ComputeWorkAreaHeight());
        main_area.Initialize();
    }

    @Override
    protected void init()
    {
        super.init();

        HardReinitializeWorkArea();

        addRenderableWidget(main_area);

        LinearLayout button_layout = new LinearLayout(
                main_area.getX(),
                main_area.getBottom() + 6,
                LinearLayout.Orientation.VERTICAL
        );

        button_layout.spacing(5);

        int layout_width = main_area.getWidth();
        int layout_height = (height - main_area.getBottom() - 6) / 3;

        OnAddControlsOnButtonLayoutRow0(CreateChildLayout(button_layout, layout_width, layout_height));

        OnAddControlsOnButtonLayoutRow1(CreateChildLayout(button_layout, layout_width, layout_height));

        OnAddControlsOnButtonLayoutRow2(CreateChildLayout(button_layout, layout_width, layout_height));

        button_layout.visitWidgets(this::addRenderableWidget);
        button_layout.arrangeElements();

        addRenderableWidget(exit_button);

        title_text_ctl.Init(width, 10, minecraft.font);
        addRenderableOnly(title_text_ctl);
    }

    protected void OnAddControlsOnButtonLayoutRow0(EqualSpacingLayout layout)
    {
        layout.addChild(exit_button);
    }

    protected void OnAddControlsOnButtonLayoutRow1(EqualSpacingLayout layout) { }

    protected void OnAddControlsOnButtonLayoutRow2(EqualSpacingLayout layout) { }

    @Override
    public void tick() { title_text_ctl.Tick(); }

    @Override
    public void extractBackground(GuiGraphicsExtractor gc, int mouseX, int mouseY, float a)
    {
        if (minecraft.level == null) {
            gc.blit(
                    RenderPipelines.GUI_TEXTURED,
                    BKGD_TEXTURE,
                    0,
                    0,
                    0f,
                    0f,
                    width,
                    height,
                    BKGD_TEXTURE_WIDTH,
                    BKGD_TEXTURE_HEIGHT,
                    BKGD_TEXTURE_WIDTH,
                    BKGD_TEXTURE_HEIGHT
            );
        } else {
            this.extractBlurredBackground(gc);
        }
    }
}
