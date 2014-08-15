package mattmc.mankini.utils;

import mattmc.mankini.commands.CommandBuy;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Project Mankini
 * Created by MattsMc on 8/15/14.
 */
public class MessageSending {
    public static void sendMessageWithPrefix(String message, String user, MessageEvent event){
        if(CommandBuy.getUserCache().get(user.toLowerCase())==null){
            String prefix = "[CheapAss] ";
            message = prefix += message;
            event.getChannel().send().message(message);
        }else{
            String prefix = "[" + CommandBuy.getUserCache().get(user.toLowerCase()).getDesc() + "] ";
            message = prefix += message;
            event.getChannel().send().message(message);
        }

    }

    public static void sendNormalMessage(String message, MessageEvent event){
        event.getChannel().send().message(message);
    }


}
