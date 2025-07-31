package robotgiggle.hierophantics.inits;

import net.minecraft.text.Text;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import robotgiggle.hierophantics.HierophanticsAPI;

object HierophanticsCommands {
    fun init() {
        CommandRegistrationCallback.EVENT.register{disp, _, _ -> 
            val base = CommandManager.literal("hierophantics")
            base.then(CommandManager.literal("addMind")
                .requires{source -> source.hasPermissionLevel(2)}
                .executes{ctx -> 
                    var source = ctx.getSource();
                    var player = source.getPlayerOrThrow();
                    HierophanticsAPI.getPlayerState(player).addMind(source.getServer());
                    source.sendFeedback({ -> Text.literal("Added new embedded mind")}, false);
                    return@executes 1
                }
            )
            base.then(CommandManager.literal("disable")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                    .requires{source -> source.hasPermissionLevel(2)}
                    .executes{ctx -> 
                        var source = ctx.getSource();
                        var target = EntityArgumentType.getPlayer(ctx, "target");
                        HierophanticsAPI.getPlayerState(target).disabled = true;
                        source.sendFeedback({ -> Text.literal("Disabled minds of ").append(target.getName())}, true);
                        return@executes 1
                    }
                )
            )
            disp.register(base)
        };
    }
}