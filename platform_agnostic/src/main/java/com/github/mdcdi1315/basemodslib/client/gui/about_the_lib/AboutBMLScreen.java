package com.github.mdcdi1315.basemodslib.client.gui.about_the_lib;

import com.github.mdcdi1315.DotNetLayer.System.AppContext;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.RuntimeFeature;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.VersionInfo;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.mods.ModInstanceCollection;
import com.github.mdcdi1315.basemodslib.client.gui.AnimatedTextWidget;
import com.github.mdcdi1315.basemodslib.client.gui.InformationalDialogScreen;
import com.github.mdcdi1315.basemodslib.client.gui.about_the_lib.licenses.LicensesScreen;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.components.MultiLineTextWidget;

import net.minecraft.network.chat.Component;

import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.mixin.MixinEnvironment;

public final class AboutBMLScreen
    extends BaseAboutScreen
{
    private final Button licensing_button;
    private final AnimatedTextWidget love_text;
    private final Button config_screens_button;
    private final Button reporting_messages_button;

    public AboutBMLScreen(Screen parent)
    {
        super(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.title"), parent);
        licensing_button = Button.builder(
                Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.licensing_button_text"),
                this::OpenLicensesScreen
        ).build();
        config_screens_button = Button.builder(
                Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.open_cfg_screen_button_text"),
                this::OpenConfigsScreen
        ).build();
        reporting_messages_button = Button.builder(
                Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.open_diag_screen_button_text"),
                this::OpenReportedMessagesScreen
        ).build();
        InitLayout();
        SetWorkAreaSpacing(10);
        love_text = new AnimatedTextWidget(font);
        love_text.SetHoverScale(1.041551f);
        love_text.SetHoverColor(0xFF45E070);
        love_text.SetTransitionTime(500);
    }

    private void InitLayout()
    {
        AddWorkAreaWidget(
                new MultiLineTextWidget(
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
                                        Built against Gradle: %s
                                        Built against Mixin: %s
                                        Built against Java: %s
                                        """,
                                        VersionInfo.GetPropertyOrEmpty(VersionInfo.PROPERTY_VERSION),
                                        VersionInfo.GetPropertyOrEmpty(VersionInfo.PROPERTY_BUILD_TIME),
                                        VersionInfo.GetPropertyOrEmpty(VersionInfo.PROPERTY_RELEASE_TYPE),
                                        BaseModsLib.GetMinecraftVersion(),
                                        BaseModsLib.GetModLoaderBranding(),
                                        BaseModsLib.GetModLoaderVersion(),
                                        BaseModsLib.GetJavaVersion(),
                                        BaseModsLib.IsDevelopmentEnvironment() ? "Yes" : "No",
                                        VersionInfo.GetPropertyOrEmpty("gradle_version"),
                                        VersionInfo.GetPropertyOrEmpty("compiled_against_mixin"),
                                        VersionInfo.GetPropertyOrEmpty("compiled_against_java")
                                )
                        ),
                        minecraft.font
                )
        );

        AddWorkAreaWidget(
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

        AddWorkAreaWidget(
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

        AddWorkAreaWidget(
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
            AddWorkAreaWidget(
                    new StringWidget(Component.literal(
                            String.format(
                                    "Feature %s: %s",
                                    s,
                                    RuntimeFeature.IsSupported(s) ? "Yes" : "No"
                            )
                    ), minecraft.font)
            );
        }

        AddWorkAreaWidget(
                new MultiLineTextWidget(Component.literal(String.format("""
================================================
                Mixin Info
================================================

Version: %s
Active Transformer: %s
Current obfuscation context: %s
Side: %s
Mixin Service Name: %s
Mixin Service Class: %s
Minimum Compatibility Level: %s
Maximum Compatibility Level: %s
Additional platform agents:
%s
Options:
%s
""",
                            MixinEnvironment.getCurrentEnvironment().getVersion(),
                            GetClassSafe(MixinEnvironment.getCurrentEnvironment().getActiveTransformer()),
                            MixinEnvironment.getCurrentEnvironment().getObfuscationContext(),
                            MixinEnvironment.getCurrentEnvironment().getSide().name(),
                            MixinService.getService().getName(),
                            GetClassSafe(MixinService.getService()),
                            MixinService.getService().getMinCompatibilityLevel(),
                            MixinService.getService().getMaxCompatibilityLevel(),
                            CollectionToString(MixinService.getService().getPlatformAgents()),
                            GetMixinOptionValues()
                        )),
                        minecraft.font
                )
        );

        var minecraft_info = net.minecraft.SharedConstants.getCurrentVersion();

        AddWorkAreaWidget(
                new MultiLineTextWidget(
                        Component.literal(String.format(
"""
================================================
                Minecraft Info
================================================

Version: %s
Build Time: %s
Instance ID: %s
World data version: %s
Network protocol version: %s
Logged-in player info:
%s
""",
                                minecraft_info.name(),
                                java.time.format.DateTimeFormatter.ofPattern("d MMM uuuu")
                                        .format(java.time.LocalDate.ofInstant(minecraft_info.buildTime().toInstant(), java.time.ZoneId.systemDefault())),
                                minecraft_info.id(),
                                minecraft_info.dataVersion().version(),
                                minecraft_info.protocolVersion(),
                                GetPlayerInfo()
                        )),
                        minecraft.font
                )
        );


    }

    private static String GetClassSafe(Object o)
    {
        if (o == null) { return "<unassigned>"; }
        return o.getClass().getName();
    }

    private static String GetPlayerInfo()
    {
        var player = BaseModsLibClient.GetLoggedInPlayer();
        if (player == null)
        {
            return "<No player is associated with this instance>";
        } else {
            GameProfile profile = player.getGameProfile();
            return String.format("""
Logged-in player name: %s
Logged-in player ID: %s
Player properties:
%s
""",
                    profile.name(),
                    profile.id(),
                    CollectionToString(profile.properties().entries())
            );
        }
    }

    private static String GetMixinOptionValues()
    {
        StringBuilder sb = new StringBuilder();
        for (var c : MixinEnvironment.Option.values())
        {
            sb.append(c.name());
            sb.append(": ");
            sb.append(c);
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String CollectionToString(java.lang.Iterable<?> collection)
    {
        StringBuilder sb = new StringBuilder();
        for (var c : collection)
        {
            sb.append(" --> ");
            sb.append(c);
            sb.append("\n");
        }
        return sb.toString();
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

    private void OpenLicensesScreen(Button b)
    {
        minecraft.setScreen(
                new LicensesScreen(this)
        );
    }

    private void OpenConfigsScreen(Button b)
    {
        minecraft.setScreen(
                new OpenConfigScreenScreen(this)
        );
    }

    private void OpenReportedMessagesScreen(Button b)
    {
        if (BaseModsLib.GetMessageReporter() == null) {
            minecraft.setScreen(
                    new InformationalDialogScreen(
                            Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.no_messages.title"),
                            Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.diag_screen.no_messages.text"),
                            this
                    )
            );
        } else {
            minecraft.setScreen(
                    new MessageReporterMessagesScreen(this)
            );
        }
    }

    @Override
    protected void OnAddControlsOnButtonLayoutRow0(EqualSpacingLayout layout)
    {
        layout.addChild(licensing_button);

        super.OnAddControlsOnButtonLayoutRow0(layout);

        layout.addChild(config_screens_button);
    }

    @Override
    protected void OnAddControlsOnButtonLayoutRow1(EqualSpacingLayout layout)
    {
        love_text.ResetTickTime();
        love_text.SetText(Component.translatable("mdcdi1315_base_mods_lib.about_bml_screen.footer_quote").withColor(0xFF00FFFF));
        layout.addChild(love_text);
        layout.addChild(reporting_messages_button);
    }

    @Override
    public void tick()
    {
        super.tick();
        love_text.Tick();
    }
}
