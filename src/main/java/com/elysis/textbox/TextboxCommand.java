package com.elysis.textbox;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;

public class TextboxCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        SuggestionProvider<CommandSourceStack> quoteSuggestions = (context, builder) -> {
            if (builder.getRemaining().isEmpty()) builder.suggest("\"\"");
            return builder.buildFuture();
        };

        SuggestionProvider<CommandSourceStack> positionSuggestions = (context, builder) -> {
            builder.suggest("bottom");
            builder.suggest("top");
            return builder.buildFuture();
        };

        SuggestionProvider<CommandSourceStack> sizeSuggestions = (context, builder) -> {
            for (int p : TextboxMod.SIZE_PERCENTS) builder.suggest(String.valueOf(p));
            return builder.buildFuture();
        };

        // Sugiere ítems y también jugadores online (con @)
        SuggestionProvider<CommandSourceStack> iconSuggestions = (context, builder) -> {
            for (ServerPlayer p : context.getSource().getServer().getPlayerList().getPlayers()) {
                builder.suggest("@" + p.getGameProfile().getName());
            }
            for (ResourceLocation loc : BuiltInRegistries.ITEM.keySet()) {
                builder.suggest(loc.toString());
            }
            return builder.buildFuture();
        };

        dispatcher.register(
                Commands.literal("textbox").requires(source -> source.hasPermission(2))
                        .then(Commands.literal("area")
                                .then(Commands.literal("chat")
                                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                                .executes(ctx -> executeAreaChat(ctx, BoolArgumentType.getBool(ctx, "enabled")))
                                        )
                                )
                        )
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("speaker", StringArgumentType.string()).suggests(quoteSuggestions)
                                        .then(Commands.argument("text", StringArgumentType.string()).suggests(quoteSuggestions)
                                                .then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                                                        .then(Commands.argument("icon", StringArgumentType.string()).suggests(iconSuggestions)
                                                                .then(Commands.argument("position", StringArgumentType.word()).suggests(positionSuggestions)
                                                                        .then(Commands.argument("time_seconds", FloatArgumentType.floatArg(0.1f))
                                                                                .then(Commands.argument("blocking", BoolArgumentType.bool())
                                                                                        .executes(c -> executeCommand(c, 1.0f))
                                                                                        .then(Commands.argument("size", StringArgumentType.word()).suggests(sizeSuggestions)
                                                                                                .executes(c -> executeCommand(c, parseSize(StringArgumentType.getString(c, "size"))))
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int executeAreaChat(CommandContext<CommandSourceStack> context, boolean enabled) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable("textbox.command.player_only"));
            return 0;
        }
        AreaChatManager.setEnabled(player.getUUID(), enabled);
        context.getSource().sendSuccess(
                () -> Component.translatable(enabled ? "textbox.command.area_chat.enabled" : "textbox.command.area_chat.disabled"),
                false
        );
        return 1;
    }

    private static float parseSize(String value) {
        return switch (value) {
            case "90" -> 0.9f;
            case "80" -> 0.8f;
            case "70" -> 0.7f;
            default -> 1.0f;
        };
    }

    private static String getRes(CommandContext<CommandSourceStack> c, String name) {
        return ResourceLocationArgument.getId(c, name).toString();
    }

    private static int executeCommand(CommandContext<CommandSourceStack> context, float size) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        String speaker = StringArgumentType.getString(context, "speaker");
        String text = StringArgumentType.getString(context, "text");
        String soundId = getRes(context, "sound");
        String iconItem = StringArgumentType.getString(context, "icon");
        String position = StringArgumentType.getString(context, "position");
        float time = FloatArgumentType.getFloat(context, "time_seconds");
        boolean blocking = BoolArgumentType.getBool(context, "blocking");

        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, new TextboxPayload(speaker, text, soundId, iconItem, position, time, blocking, size, ""));
        }
        return players.size();
    }
}
