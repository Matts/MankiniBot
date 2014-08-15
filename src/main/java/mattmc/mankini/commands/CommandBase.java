package mattmc.mankini.commands;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Project MankiniBot
 * Created by MattMc on 5/24/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class CommandBase {
    public CommandBase(){}

    public String message;
    public String command;

    public String[] args;

    public void channelCommand(MessageEvent<PircBotX> event){
        message = event.getMessage();
        command = message.split(" ")[0];
        args = event.getMessage().split(" ");
    }
}
