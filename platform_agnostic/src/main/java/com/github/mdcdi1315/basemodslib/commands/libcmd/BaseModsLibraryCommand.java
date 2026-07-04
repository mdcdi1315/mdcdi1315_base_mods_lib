package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.basemodslib.commands.EnumArgument;
import com.github.mdcdi1315.basemodslib.commands.ICommandRegistrar;
import com.github.mdcdi1315.basemodslib.commands.RegistersSubCommandsAbstractCommand;

public final class BaseModsLibraryCommand
    extends RegistersSubCommandsAbstractCommand
{
    public BaseModsLibraryCommand()
    {
        super(
                "bml",
                new BaseModsLibraryDevPermission(),
                new UpdateStructureTemplateCommand(),
                new UpdateStructureTemplatesCommand(),
                new DescribeHeldItemCommand(),
                new GetDimensionBiomeTemperaturesCommand(),
                new DisplayLoadedBMLModsCommand(),
                new BMLInfoCommand()
        );
    }

    /**
     * Initializes the BML commands. Do not use it by your code!
     * @param registrar The library's command registrar instance.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void InitializeLibraryCommandSupport(ICommandRegistrar registrar)
    {
        registrar.RegisterArgumentTypeInfo("enum_value", EnumArgument.class, new EnumArgument.Info());
        registrar.RegisterByCommand(BaseModsLibraryCommand::new);
    }
}
