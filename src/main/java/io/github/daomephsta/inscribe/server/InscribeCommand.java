package io.github.daomephsta.inscribe.server;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class InscribeCommand
{
    private static final DynamicCommandExceptionType GUIDE_ID_INVALID_EXCEPTION = new DynamicCommandExceptionType(
        context -> new TranslatableText(Inscribe.MOD_ID + ".command.invalid_guide_id", context));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(literal(Inscribe.MOD_ID)
            .then(giveGuide())
        );
    }

    private static ArgumentBuilder<ServerCommandSource, ?> giveGuide()
    {
        return literal("give-guide")
            .then(argument("selector", DelegatingArgumentType.delegate(EntityArgumentType.players()))
                .then(argument("guide_id", IdentifierArgumentType.identifier())
                    .suggests(InscribeCommand::guideIdSuggestions)
                    .executes(InscribeCommand::giveGuide0)
                )
            );
    }

    private static int giveGuide0(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        Guide guide = getGuide(context);
        ItemStack guideStack = Inscribe.GUIDE_ITEM.forGuide(guide);
        String command = String.format("give %s %s%s", context.getArgument("selector", String.class),
            Registry.ITEM.getId(guideStack.getItem()), guideStack.getNbt().asString());
        return context.getSource().getServer().getCommandManager().getDispatcher()
            .execute(command, context.getSource());
    }

    private static Guide getGuide(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        Identifier guideId = IdentifierArgumentType.getIdentifier(context, "guide_id");
        if (!GuideManager.INSTANCE.hasGuide(guideId))
            throw GUIDE_ID_INVALID_EXCEPTION.create(guideId);
        Guide guide = GuideManager.INSTANCE.getGuide(guideId);
        return guide;
    }

    private static CompletableFuture<Suggestions> guideIdSuggestions(
        CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
    {
        return CommandSource.suggestIdentifiers(GuideManager.INSTANCE.getGuides().stream()
            .map(Guide::getIdentifier), builder);
    }
}
