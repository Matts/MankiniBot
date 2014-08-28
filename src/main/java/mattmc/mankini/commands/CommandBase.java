package mattmc.mankini.commands;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Project MankiniBot
 * Created by MattMc on 5/24/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public abstract class CommandBase {
    public CommandBase(){}

    public static boolean isActive;

    public String message;
    public String command;
    public String user;

    public Channel channel;

    public String[] args;

    public void channelCommand(MessageEvent<PircBotX> event){
        message = event.getMessage();
        command = message.split(" ")[0];
        user = event.getUser().getNick();
        args = event.getMessage().split(" ");
        channel = event.getChannel();
    }
}
