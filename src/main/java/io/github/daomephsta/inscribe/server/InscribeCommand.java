package io.github.daomephsta.inscribe.server;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static net.minecraft.command.argument.IdentifierArgumentType.getIdentifier;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
    private static final DynamicCommandExceptionType FLAG_ID_INVALID_EXCEPTION = new DynamicCommandExceptionType(
        context -> new TranslatableText(Inscribe.MOD_ID + ".command.flags.invalid_flag_id", context));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(literal(Inscribe.MOD_ID)
            .then(giveGuide())
            .then(flags())
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
            Registry.ITEM.getId(guideStack.getItem()), guideStack.getTag().asString());
        return context.getSource().getMinecraftServer().getCommandManager().getDispatcher()
            .execute(command, context.getSource());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> flags()
    {
        return literal("flags")
            .then
            (argument("guide_id", IdentifierArgumentType.identifier())
                .suggests(InscribeCommand::guideIdSuggestions)
                .then(argument("flag_id", IdentifierArgumentType.identifier())
                    .suggests(InscribeCommand::flagIdSuggestions)
                    .then(literal("get").executes(InscribeCommand::getFlag))
                    .then(literal("set")
                        .then(argument("value", bool()).executes(InscribeCommand::setFlag)))
                )
            );
    }

    private static int getFlag(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
            Guide guide = getGuide(context);
            Identifier flagId = getFlagId(guide, context);
            context.getSource().sendFeedback(new TranslatableText(Inscribe.MOD_ID + ".command.flags.print_flag", 
                flagId, guide.getFlags().isTrue(flagId)), false);
            return Command.SINGLE_SUCCESS;
    }

    private static int setFlag(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        Guide guide = getGuide(context);
        Identifier flagId = getFlagId(guide, context);
        boolean value = getBool(context, "value");
        guide.getFlags().set(flagId, value);
        context.getSource().sendFeedback(new TranslatableText(Inscribe.MOD_ID + ".command.flags.print_flag", 
            flagId, value), false);
        return Command.SINGLE_SUCCESS;
    }

    private static Identifier getFlagId(
        Guide guide, CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        Identifier flagId = getIdentifier(context, "flag_id");
        if (!guide.getFlags().has(flagId))
            throw FLAG_ID_INVALID_EXCEPTION.create(flagId);
        return flagId;
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
    
    private static CompletableFuture<Suggestions> flagIdSuggestions(
        CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
    {
        Guide guide = GuideManager.INSTANCE.getGuide(getIdentifier(context, "guide_id"));
        return CommandSource.suggestIdentifiers(guide.getFlags().getIds(), builder);
    }
}
