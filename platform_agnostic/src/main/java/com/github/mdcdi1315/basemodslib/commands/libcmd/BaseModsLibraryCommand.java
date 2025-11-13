package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.basemodslib.commands.RegistersSubCommandsAbstractCommand;

public final class BaseModsLibraryCommand
    extends RegistersSubCommandsAbstractCommand
{
    public BaseModsLibraryCommand() {
        super("bml",
                new BaseModsLibraryDevPermission(),
                new UpdateStructureTemplateCommand(),
                new UpdateStructureTemplatesCommand(),
                new DescribeHeldItemCommand(),
                new GetDimensionBiomeTemperaturesCommand());
    }
}
