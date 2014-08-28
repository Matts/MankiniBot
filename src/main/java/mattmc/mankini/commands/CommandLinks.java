package mattmc.mankini.commands;

import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.Permissions;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;

/**
 * Project MankiniBot
 * Created by MattMc on 6/1/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class CommandLinks extends CommandBase {
    public static ArrayList<String> permitted = new ArrayList<String>();
    public static ArrayList<String> strike1 = new ArrayList<String>();

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);
            if(Permissions.getPermission(user, Permissions.Perms.MOD, event, true).equals(Permissions.Perms.MOD)){
                permitted.add(args[1]);
                event.respond(user + " has given permissions to post a link to " + args[1]);
        }
    }

}