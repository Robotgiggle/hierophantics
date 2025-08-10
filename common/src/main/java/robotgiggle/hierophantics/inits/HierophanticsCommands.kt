package robotgiggle.hierophantics.inits;

import net.minecraft.text.Text;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;

import robotgiggle.hierophantics.data.HieroServerState;

object HierophanticsCommands {
    fun register(disp: CommandDispatcher<ServerCommandSource>) {
        disp.register(CommandManager.literal("hierophantics")
            .then(CommandManager.literal("addMind")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                    .requires{source -> source.hasPermissionLevel(2)}
                    .executes{ctx -> 
                        var source = ctx.getSource();
                        var target = EntityArgumentType.getPlayer(ctx, "target");
                        HieroServerState.getPlayerState(target).addMind(source.getServer(), null);
                        source.sendFeedback({-> Text.translatable("command.hierophantics.add_mind", target.getName())}, false);
                        return@executes 1
                    }
                )
            )
            .then(CommandManager.literal("disable")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                    .requires{source -> source.hasPermissionLevel(2)}
                    .executes{ctx -> 
                        var source = ctx.getSource();
                        var target = EntityArgumentType.getPlayer(ctx, "target");
                        if (HieroServerState.getPlayerState(target).disabled) {
                            source.sendError(Text.translatable("command.hierophantics.disable.already", target.getName()));
                            return@executes 0
                        } else {
                            HieroServerState.getPlayerState(target).disabled = true;
                            source.sendFeedback({-> Text.translatable("command.hierophantics.disable", target.getName())}, true);
                            return@executes 1
                        }
                    }
                )
            )
        )
    }
}