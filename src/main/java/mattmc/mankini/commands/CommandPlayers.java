package mattmc.mankini.commands;

import mattmc.mankini.utils.MessageSending;
import mattmc.mankini.utils.MinecraftServer;
import mattmc.mankini.utils.Permissions;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Project MankiniBot
 * Created by MattMc on 6/2/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class CommandPlayers extends CommandBase {
    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);
            if(Permissions.getPermission(user, Permissions.Perms.REG, event, true).equals(Permissions.Perms.REG)){
                    MinecraftServer server = new MinecraftServer("runew0lf.com");
                    if(!server.parseData(MinecraftServer.Connection.PING).equalsIgnoreCase("Nothing Found! Please check if the server is on!")){
                        MessageSending.sendNormalMessage(server.parseData(MinecraftServer.Connection.PLAYERS_ONLINE), event);
                    } else {
                        MessageSending.sendMessageWithPrefix("Nothing Found! Please check if the server is on!", user, event);
                    }
                }
    }
}
