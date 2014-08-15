package mattmc.mankini.commands;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.HashMap;

/**
 * Project Mankini
 * Created by MattsMc on 8/15/14.
 */
public class CommandBuy extends CommandBase {
    HashMap<Ranks, Integer> ranks = new HashMap<Ranks, Integer>();

    public CommandBuy(){
        ranks.clear();
    }

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);

    }

    public enum Ranks {

    }
}
