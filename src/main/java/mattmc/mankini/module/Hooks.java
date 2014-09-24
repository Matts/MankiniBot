package mattmc.mankini.module;

import mattmc.mankini.commands.CommandFactoid;
import mattmc.mankini.commands.CommandKinis;
import mattmc.mankini.commands.CommandLinks;
import mattmc.mankini.common.ModCommon;
import mattmc.mankini.common.StreamingCommon;
import mattmc.mankini.common.ViewerCommon;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.Permissions;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Project MankiniBot
 * Created by MattMc on 5/24/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class Hooks extends ListenerAdapter<PircBotX> {
    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {

        if (!CommandKinis.class.newInstance().existsInDatabase("database\\kinis.db", "KINIS", event.getUser().getNick().toLowerCase())) {
            CommandKinis.class.newInstance().addUser(event.getUser().getNick());
        }

        String command = event.getMessage().split(" ")[0];
        if (command.startsWith("!")) {
            String output = CommandFactoid.class.newInstance().getOutput(event.getMessage().split(" ")[0].substring(1));
            if (output.contains("%r")) {
                if (event.getMessage().split(" ").length > 1) {
                    output = output.replaceAll("%r", event.getMessage().split(" ")[1]);
                }
            }
            if (output.contains("%s")) {
                if (event.getMessage().split(" ").length > 2) {
                    output = output.replaceAll("%s", event.getMessage().split(" ")[2]);
                }
            }
            if (!CommandFactoid.class.newInstance().getPermission(event.getMessage().split(" ")[0].substring(1)).equalsIgnoreCase("ALL")) {
                if (!CommandFactoid.class.newInstance().getPermission(event.getMessage().split(" ")[0].substring(1)).equalsIgnoreCase("REG")) {
                    if (CommandFactoid.class.newInstance().getPermission(event.getMessage().split(" ")[0].substring(1)).equalsIgnoreCase("MOD")) {
                        if (Permissions.isModerator(event.getUser().getNick(), event)) {
                            event.getChannel().send().message(output);
                        }
                    }
                } else {
                    if (Permissions.isRegular(event.getUser().getNick(), event)) {
                        event.getChannel().send().message(output);
                    }
                }
            } else {
                event.getChannel().send().message(output);
            }
        }
        /**
         * Links Hook
         */
        if (event.getMessage().contains("http") || event.getMessage().contains("www.") || event.getMessage().contains(".com")
                || event.getMessage().contains(".net") || event.getMessage().contains(".co") ||
                event.getMessage().contains(".co.uk") || event.getMessage().contains(".org")) {

            if (Permissions.isPermitted(event.getUser().getNick(), event)) {
                CommandLinks.permitted.remove(event.getUser().getNick().toLowerCase());
            }
        }

        if (CommandKinis.kinis.getState().equals(Thread.State.NEW)) {
            CommandKinis.kinis.start();
        }
        if (ViewerCommon.updateViewers.getState().equals(Thread.State.NEW)) {
            ViewerCommon.updateViewers.start();
        }
        if (ModCommon.updateMods.getState().equals(Thread.State.NEW)) {
            ModCommon.updateMods.start();
        }
        if (StreamingCommon.checkIfOnline.getState().equals(Thread.State.NEW)) {
            StreamingCommon.checkIfOnline.start();
        }

    }
}
