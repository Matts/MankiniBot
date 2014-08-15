package mattmc.mankini.commands;

import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.Permissions;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;

/**
 * Project Mankini
 * Created by MattsMc on 7/11/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class CommandLinks extends CommandBase {
    public static ArrayList<String> permitted = new ArrayList<String>();
    public static ArrayList<String> strike1 = new ArrayList<String>();

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                permitted.add(event.getMessage().split(" ")[1]);
                event.respond(event.getUser().getNick() + Strings.hasBeenPermitted + event.getMessage().split(" ")[1]);
        }
    }

}