package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public abstract class CommandPermission
    implements Predicate<CommandSourceStack>
{
    protected abstract boolean IsSatisfied(@DisallowNull CommandSourceStack stack);

    @Override
    public final boolean test(CommandSourceStack commandSourceStack) {
        return IsSatisfied(commandSourceStack);
    }
}
