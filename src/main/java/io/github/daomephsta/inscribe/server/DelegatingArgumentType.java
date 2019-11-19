package io.github.daomephsta.inscribe.server;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.github.daomephsta.inscribe.common.mixin.ArgumentTypesAccessors;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.util.PacketByteBuf;

public class DelegatingArgumentType implements ArgumentType<String>
{
    private final ArgumentType<?> delegate;

    private DelegatingArgumentType(ArgumentType<?> delegate)
    {
        this.delegate = delegate;
    }

    public static DelegatingArgumentType delegate(ArgumentType<?> delegate)
    {
        return new DelegatingArgumentType(delegate);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        int cursor = reader.getCursor();
        String stringForm = reader.readStringUntil(' ');
        reader.setCursor(cursor);
        //Throw any errors, but ignore the result
        delegate.parse(reader);
        return stringForm;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return delegate.listSuggestions(context, builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return delegate.getExamples();
    }

    public static class Serialiser implements ArgumentSerializer<DelegatingArgumentType>
    {
        @Override
        public void toPacket(DelegatingArgumentType argumentType, PacketByteBuf buf)
        {
            ArgumentTypes.toPacket(buf, argumentType.delegate);
        }

        @Override
        public DelegatingArgumentType fromPacket(PacketByteBuf buf)
        {
            return new DelegatingArgumentType(ArgumentTypes.fromPacket(buf));
        }

        @Override
        public void toJson(DelegatingArgumentType argumentType, JsonObject json)
        {
            ArgumentTypesAccessors.invokeToJson(json, argumentType.delegate);
        }
    }
}
