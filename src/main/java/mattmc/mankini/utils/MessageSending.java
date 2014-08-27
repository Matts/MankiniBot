package mattmc.mankini.utils;

import mattmc.mankini.commands.CommandBuy;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Project MankiniBot
 * Created by MattMc on 6/1/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class MessageSending {
    public static void sendMessageWithPrefix(String message, String user, MessageEvent event){
        if(CommandBuy.getUserCache().get(user.toLowerCase())==null){
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
