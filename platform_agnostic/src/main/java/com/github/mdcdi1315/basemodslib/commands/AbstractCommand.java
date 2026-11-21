package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.DisallowNull;

import com.github.mdcdi1315.basemodslib.utils.ChatComponentSupplier;
import com.github.mdcdi1315.basemodslib.utils.annotations.APIStatus;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * Provides the base class for initializing and using a Minecraft command. <br />
 * Non-abstract subclasses of this do represent actual Minecraft commands.
 */
public abstract class AbstractCommand
{
    private final String name;

    /**
     * Constructs a new command instance. <br />
     * It's name will be the one passed to the {@code command_name} parameter.
     * @param command_name The name of the newly created command.
     * @throws ArgumentException {@code command_name} is {@code null} or the empty string (&quot;&quot;).
     */
    protected AbstractCommand(String command_name)
        throws ArgumentException
    {
        ArgumentException.ThrowIfNullOrEmpty(command_name, "command_name");
        name = command_name;
    }

    /**
     * Internal implementation detail. Do not use.
     * @param dispatcher The command dispatcher to use.
     */
    public final void RegisterToDispatcher(@DisallowNull CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(CommandImplementation(Commands.literal(name)));
    }

    /**
     * Internal implementation detail. Do not use.
     * @param builder The command builder to use.
     */
    public final LiteralArgumentBuilder<CommandSourceStack> RegisterByBuilder(@DisallowNull LiteralArgumentBuilder<CommandSourceStack> builder)
    {
        return builder.then(CommandImplementation(Commands.literal(name)));
    }

    /**
     * Provides the command's implementation.
     * @param builder The command implementation builder to use.
     * @return The transformed builder, passing through all the commands
     */
    @NotNull
    protected abstract LiteralArgumentBuilder<CommandSourceStack> CommandImplementation(@DisallowNull LiteralArgumentBuilder<CommandSourceStack> builder);

    /**
     * Gets the name of this command.
     * @return The command's name.
     */
    @NotNull
    public final String getName() { return name; }

    /**
     * Offloads a command's task to the {@linkplain ForkJoinPool#commonPool() Fork-Join common pool}.
     * @param stack The {@link CommandSourceStack} providing state access to the offloaded task.
     * @param executor The method that provides the work to do.
     * @param state Additional state data required by the offloaded task.
     * @param <TS> The type of the state value provided through {@code state}.
     * @throws ArgumentNullException {@code stack} and/or {@code executor} and/or {@code state} are {@code null}.
     * @since 1.0.40-beta7
     */
    @SuppressWarnings("resource")
    @APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.40-beta7")
    protected static <TS> void OffloadTask(CommandSourceStack stack, OffloadedCommandTask<TS> executor, TS state)
            throws ArgumentNullException
    {
        ForkJoinPool.commonPool().execute((ForkJoinTask<?>) new OffloadedServerCommandTask<>(stack, executor, state));
    }

    /**
     * Prints an abstract command success message to the specified command source stack.
     * @param stack The command source stack to use.
     * @throws ArgumentNullException {@code stack} is {@code null}.
     */
    @APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.40-beta7")
    protected static void SendCommandSuccessMessage(CommandSourceStack stack) throws ArgumentNullException { SendCommandSuccessMessage(stack, true); }

    /**
     * Prints an abstract command success message to the specified command source stack.
     * @param stack The command source stack to use.
     * @param broadcast Whether to broadcast the success message to the server's administrators as well.
     * @throws ArgumentNullException {@code stack} is {@code null}.
     */
    @APIStatus(Status = APIStatus.Status.STABLE, Since = "1.0.40-beta7")
    protected static void SendCommandSuccessMessage(CommandSourceStack stack, boolean broadcast)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(stack, "stack");
        stack.sendSuccess(
                new ChatComponentSupplier(
                        Component.translatable("mdcdi1315_base_mods_lib.cmds.completed_sucessfully")
                                .withStyle(ChatFormatting.GREEN)
                ),
                broadcast
        );
    }
}
