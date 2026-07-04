package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.Predicate;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import net.minecraft.commands.CommandSourceStack;

/**
 * Defines the base class for command permissions. <br />
 * These permissions control whether a command that registers sub-commands can be used in a given Minecraft session.
 */
public abstract class CommandPermission
    implements Predicate<CommandSourceStack>
{
    /**
     * Gets a value whether the permission defined by the implementation of this method is satisfied by the given command source stack.
     * @param stack The command source stack providing information about the current Minecraft session.
     * @return A value whether the permission is satisfied or not.
     */
    protected abstract boolean IsSatisfied(@DisallowNull CommandSourceStack stack);

    @Override
    public final boolean test(CommandSourceStack stack) { return IsSatisfied(stack); }

    @Override
    public final boolean predicate(CommandSourceStack stack) { return IsSatisfied(stack); }
}
