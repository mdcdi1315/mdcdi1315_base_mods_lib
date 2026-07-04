package com.github.mdcdi1315.basemodslib.commands;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentException;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.google.gson.JsonObject;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.Collection;
import java.util.stream.Stream;
import java.util.concurrent.CompletableFuture;

/**
 * Provides a Brigadier argument deduced from an enumeration type. <br />
 * The BML registers this to Minecraft inherently, so you do not need to register this yourself.
 * @param <T> The type of the enumeration to process.
 * @since 1.0.35
 */
public class EnumArgument<T extends Enum<T>>
        implements ArgumentType<T>
{
    private final Class<T> enum_class;

    /**
     * Creates a new enumeration argument over the specified enumeration class.
     * @param enum_class The enumeration class to construct the argument from.
     * @return A new instance of the {@link EnumArgument} class, representing the specified enumeration type.
     * @param <T> The actual type of the enumeration.
     */
    public static <T extends Enum<T>> EnumArgument<T> Create(Class<T> enum_class)
        throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(enum_class, "enum_class");
        return new EnumArgument<>(enum_class);
    }

    private EnumArgument(Class<T> enumClass)
    {
        super();
        this.enum_class = enumClass;
    }

    @Override
    public T parse(StringReader reader)
            throws CommandSyntaxException
    {
        String s = reader.readString();
        try {
            return T.valueOf(enum_class, s);
        } catch (IllegalArgumentException e) {
            throw new SimpleCommandExceptionType(Component.literal(
                    String.format(
                            "Cannot parse value \"%s\" because it is not a valid enumeration case in enumeration %s.",
                            s,
                            enum_class.getSimpleName()
                    )
            )).createWithContext(reader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Stream.of(this.enum_class.getEnumConstants()).map(Enum::name), builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        var cts = enum_class.getEnumConstants();
        if (cts.length == 0) {
            return List.of();
        } else if (cts.length == 1) {
            return List.of(cts[0].name());
        } else {
            return List.of(
                    cts[0].name(),
                    cts[cts.length - 1].name()
            );
        }
    }

    @SuppressWarnings("NullableProblems")
    public static final class Info<T extends Enum<T>>
            implements ArgumentTypeInfo<EnumArgument<T>, Info.Template<T>>
    {
        public Info() { super(); }

        public void serializeToNetwork(Template<T> template, FriendlyByteBuf buffer) { buffer.writeUtf(template.enumClass.getName()); }

        @SuppressWarnings("unchecked")
        public Template<T> deserializeFromNetwork(FriendlyByteBuf buffer)
        {
            String name = buffer.readUtf();
            try {
                return new Template<>(this, (Class<T>)Class.forName(name));
            } catch (ClassNotFoundException var3) {
                throw new ArgumentException("Could not find class " + name + " needed for decoding the enumeration argument.");
            }
        }

        public void serializeToJson(Template<T> template, JsonObject json)
        {
            json.addProperty("enum", template.enumClass.getName());
        }

        public Template<T> unpack(EnumArgument<T> argument) { return new Template<>(this, argument.enum_class); }

        public static final class Template<T extends Enum<T>>
                implements ArgumentTypeInfo.Template<EnumArgument<T>>
        {
            private final Info<T> info;
            private final Class<T> enumClass;

            private Template(Info<T> information_holder, Class<T> enumClass)
            {
                super();
                this.enumClass = enumClass;
                this.info = information_holder;
            }

            public Info<T> type() { return info; }

            public EnumArgument<T> instantiate(CommandBuildContext cbc) { return new EnumArgument<>(this.enumClass); }
        }
    }
}
