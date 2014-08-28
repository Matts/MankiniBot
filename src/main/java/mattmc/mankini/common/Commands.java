package mattmc.mankini.common;

import mattmc.mankini.commands.*;
import mattmc.mankini.utils.MessageSending;
import mattmc.mankini.utils.Permissions;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.HashMap;

/**
 * Project MankiniBot
 * Created by MattMc on 6/1/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class Commands extends ListenerAdapter<PircBotX> {
    public HashMap<String, CommandBase> commands = new HashMap<>();

    public Commands(){
        commands.clear();
        commands.put("quote", new CommandQuote());
        commands.put("permit", new CommandLinks());
        commands.put("players", new CommandPlayers());
        commands.put("command", new CommandFactoid());
        commands.put("kinis", new CommandKinis());
        commands.put("reg", new CommandRegular());
        commands.put("rank", new CommandBuy());
        commands.put("highlight", new CommandHighlight());
        commands.put("js", new CommandJS());
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        if(event.getMessage().startsWith("!")){
            //NOTE: The isActive Is Reversed
            if(!commands.get(event.getMessage().split(" ")[0]).isActive){
                if(commands.containsKey(event.getMessage().substring(1).split(" ")[0])){
                    commands.get(event.getMessage().substring(1).split(" ")[0]).channelCommand(event);
                }
            }
            if(event.getMessage().split(" ")[0].equalsIgnoreCase("!module")){
                if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD, event, true).equals(Permissions.Perms.MOD)){
                    String command = event.getMessage().split(" ")[1];
                    commands.get(command).isActive = (!commands.get(command).isActive);
                    if(commands.get(command).isActive){
                        MessageSending.sendMessageWithPrefix(event.getUser().getNick() + " " +  event.getMessage().split(" ")[1] + " is now disabled.", event.getUser().getNick(), event);
                    }else{
                        MessageSending.sendMessageWithPrefix(event.getUser().getNick() + " " +  event.getMessage().split(" ")[1] + " is now enabled.", event.getUser().getNick(), event);
                    }
                }
            }
        }
    }
}
