package mattmc.mankini.common;

import mattmc.mankini.commands.*;
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
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        if(event.getMessage().startsWith("!")){
            if(commands.containsKey(event.getMessage().substring(1).split(" ")[0])){
                commands.get(event.getMessage().substring(1).split(" ")[0]).channelCommand(event);
            }
        }
    }
}
