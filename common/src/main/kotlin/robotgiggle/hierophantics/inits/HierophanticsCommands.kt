package robotgiggle.hierophantics.inits

import net.minecraft.network.chat.Component
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.Commands
import net.minecraft.commands.CommandSourceStack
import com.mojang.brigadier.CommandDispatcher

import robotgiggle.hierophantics.data.HieroServerState
import robotgiggle.hierophantics.networking.msg.MsgOwnedMindsS2C

object HierophanticsCommands {
    fun register(disp: CommandDispatcher<CommandSourceStack>) {
        disp.register(Commands.literal("hierophantics")
            .then(Commands.literal("addMind")
                .then(Commands.argument("target", EntityArgument.player())
                    .requires{source -> source.hasPermission(2)}
                    .executes{ctx -> 
                        val source = ctx.getSource()
                        val target = EntityArgument.getPlayer(ctx, "target")
                        val newTotal = HieroServerState.getPlayerState(target).addMind(source.getServer(), null)
                        MsgOwnedMindsS2C(newTotal).sendToPlayer(target)
                        source.sendSuccess({ Component.translatable("command.hierophantics.add_mind", target.getName()) }, false)
                        return@executes 1
                    }
                )
            )
            .then(Commands.literal("disable")
                .then(Commands.argument("target", EntityArgument.player())
                    .requires{source -> source.hasPermission(2)}
                    .executes{ctx -> 
                        val source = ctx.getSource()
                        val target = EntityArgument.getPlayer(ctx, "target")
                        if (HieroServerState.getPlayerState(target).disabled) {
                            source.sendFailure(Component.translatable("command.hierophantics.disable.already", target.getName()))
                            return@executes 0
                        } else {
                            HieroServerState.getPlayerState(target).disabled = true
                            source.sendSuccess({ Component.translatable("command.hierophantics.disable", target.getName()) }, true)
                            return@executes 1
                        }
                    }
                )
            )
        )
    }
}