package mattmc.mankini.utils;

import mattmc.mankini.commands.CommandLinks;
import mattmc.mankini.commands.CommandRegular;
import mattmc.mankini.common.ModCommon;
import mattmc.mankini.libs.Strings;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.SQLException;

/**
 * Project MankiniBot
 * Created by MattMc on 7/14/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */
public class Permissions {
    public static boolean isOwner(String user, MessageEvent event, boolean bcast) {
        //TODO: Remove Matts Testing Override
        if (user.equals("runew0lf") || user.equalsIgnoreCase("MattMc")) {
            return true;
        } else {
            noPermissionMessage(user, event, bcast);
        }
        return false;
    }

    public static boolean isModerator(String user, MessageEvent event, boolean bcast) {
        if (ModCommon.moderators.contains(user.toLowerCase()) || isOwner(user, event, bcast)) {
            return true;
        } else {
            noPermissionMessage(user, event, bcast);
        }
        return false;
    }

    public static boolean isRegular(String user, MessageEvent event, boolean bcast) throws IllegalAccessException, InstantiationException, SQLException {
        if (CommandRegular.class.newInstance().isRegular(user) || isModerator(user, event, bcast) || isOwner(user, event, bcast)) {
            return true;
        } else {
            noPermissionMessage(user, event, bcast);
        }
        return false;
    }

    public static boolean isPermitted(String user, MessageEvent event) throws IllegalAccessException, SQLException, InstantiationException {
        if (CommandLinks.permitted.contains(user) || isRegular(user,event,false)) {
            return true;
        } else {
            if (!CommandLinks.strike1.contains(user)) {
                event.getBot().sendRaw().rawLine("PRIVMSG " + event.getChannel().getName() + " :.timeout " + event.getUser().getNick() + " 5");
                event.respond(Strings.strike1);
                CommandLinks.strike1.add(user);

            } else {
                if (CommandLinks.strike1.contains(user)) {
                    event.getBot().sendRaw().rawLine("PRIVMSG " + event.getChannel().getName() + " :.timeout " + event.getUser()
                            .getNick() + Strings.bantime);
                    event.respond(Strings.strike2 + Strings.bantimeOnMSG);
                    CommandLinks.strike1.remove(user);
                }
            }
        }
        return false;
    }

    public static void noPermissionMessage(String user, MessageEvent event, boolean bcast) {
        if(bcast){
            MessageSending.sendMessageWithPrefix(user + " You Do Not Have Permissions To Do That", user, event);
        }
    }
}