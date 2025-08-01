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
            disp.register(CommandManager.literal("hierophantics")
                .then(CommandManager.literal("addMind")
                    .then(CommandManager.argument("target", EntityArgumentType.player())
                        .requires{source -> source.hasPermissionLevel(2)}
                        .executes{ctx -> 
                            var source = ctx.getSource();
                            var target = EntityArgumentType.getPlayer(ctx, "target");
                            HierophanticsAPI.getPlayerState(target).addMind(source.getServer());
                            source.sendFeedback({ -> Text.translatable("command.hierophantics.add_mind", target.getName())}, false);
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
                            HierophanticsAPI.getPlayerState(target).disabled = true;
                            source.sendFeedback({ -> Text.translatable("command.hierophantics.disable", target.getName())}, true);
                            return@executes 1
                        }
                    )
                )
            )
        };
    }
}