package mattmc.mankini.common;

import mattmc.mankini.commands.*;
import mattmc.mankini.utils.MessageSending;
import mattmc.mankini.utils.Permissions;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
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
    }

    ArrayList<String> disabledCommands = new ArrayList<String>();

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        if(event.getMessage().startsWith("!")){
            //NOTE: The isActive Is Reversed
                if(commands.containsKey(event.getMessage().substring(1).split(" ")[0])){
                    if(!disabledCommands.contains(event.getMessage().substring(1).split(" ")[0])){
                        commands.get(event.getMessage().substring(1).split(" ")[0]).channelCommand(event);
                    }
                }
            if(event.getMessage().split(" ")[0].equalsIgnoreCase("!toggle")){
                if(Permissions.isModerator(event.getUser().getNick(), event)){
                    String command = event.getMessage().split(" ")[1];
                    if(commands.containsKey(command.toLowerCase())){
                        if(disabledCommands.contains(command.toLowerCase())){
                            disabledCommands.remove(command.toLowerCase());
                            MessageSending.sendMessageWithPrefix(event.getUser().getNick() + " " + command + " has been enabled.", event.getUser().getNick(), event);
                        }else{
                            disabledCommands.add(command.toLowerCase());
                            MessageSending.sendMessageWithPrefix(event.getUser().getNick() + " " + command + " has been disabled.", event.getUser().getNick(), event);
                        }
                    }
                }
            }
            if(event.getMessage().split(" ")[0].equalsIgnoreCase("!?")){
                System.out.println(event.getMessage().split(" ")[1].toLowerCase());
                if(commands.containsKey(event.getMessage().split(" ")[1].toLowerCase())){
                    MessageSending.sendMessageWithPrefix(commands.get(event.getMessage().split(" ")[1].toLowerCase()).getSyntax(), event.getUser().getNick(), event);
                }
            }
        }
    }
}
