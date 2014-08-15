package mattmc.mankini.module;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.commands.CommandFactoid;
import mattmc.mankini.commands.CommandKinis;
import mattmc.mankini.commands.CommandLinks;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.ModUtils;
import mattmc.mankini.utils.Permissions;
import mattmc.mankini.utils.StreamingUtils;
import mattmc.mankini.utils.ViewerUtils;
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

        if(!CommandKinis.class.newInstance().userExists(event.getUser().getNick())){
            CommandKinis.class.newInstance().addUser(event.getUser().getNick());
        }

        String command = event.getMessage().split(" ")[0];
        if(command.startsWith("!")){
            String output = CommandFactoid.class.newInstance().getOutput(event.getMessage().split(" ")[0].substring(1));
            if(output.contains("%r")){
                if(event.getMessage().split(" ").length>1){
                    output = output.replaceAll("%r", event.getMessage().split(" ")[1]);
                }
            }
            if(output.contains("%s")){
                if(event.getMessage().split(" ").length>2){
                    output = output.replaceAll("%s", event.getMessage().split(" ")[2]);
                }
            }
            if(!CommandFactoid.class.newInstance().getPermission(event.getMessage().split(" ")[0].substring(1)).equalsIgnoreCase("ALL")){
                if(!CommandFactoid.class.newInstance().getPermission(event.getMessage().split(" ")[0].substring(1)).equalsIgnoreCase("REG")){
                    if(CommandFactoid.class.newInstance().getPermission(event.getMessage().split(" ")[0].substring(1)).equalsIgnoreCase("MOD")){
                        if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                            event.getChannel().send().message(output);
                        }
                    }
                }else{
                    if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG).equals(Permissions.Perms.REG)){
                        event.getChannel().send().message(output);
                    }
                }
            }else{
                event.getChannel().send().message(output);
            }
        }
        /**
         * Links Hook
         */
        if(event.getMessage().contains("http") || event.getMessage().contains("www.") || event.getMessage().contains(".com")
                || event.getMessage().contains(".net") || event.getMessage().contains(".co") ||
                event.getMessage().contains(".co.uk")){
            if(!Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG).equals(Permissions.Perms.REG)){
                if(!CommandLinks.permitted.contains(event.getUser().getNick())){
                    if(!(ModUtils.moderators.contains(event.getUser().getNick()))){
                        if(!(MankiniBot.Owner.contains(event.getUser().getNick()))){
                            if(!CommandLinks.strike1.contains(event.getUser().getNick())){
                                event.getBot().sendRaw().rawLine("PRIVMSG " + event.getChannel().getName()
                                        +" :.timeout "+ event.getUser().getNick() + " 5");
                                event.respond(Strings.strike1);
                                CommandLinks.strike1.add(event.getUser().getNick());

                            }else{
                                if(CommandLinks.strike1.contains(event.getUser().getNick())){
                                    event.getBot().sendRaw().rawLine("PRIVMSG " + event.getChannel().getName() +" :.timeout "+ event.getUser()
                                            .getNick() + Strings.bantime);
                                    event.respond(Strings.strike2 + Strings.bantimeOnMSG);
                                    CommandLinks.strike1.remove(event.getUser().getNick());
                                }
                            }
                        }
                    }
                }
            }
            CommandLinks.permitted.remove(event.getUser().getNick());
        }


        if(ViewerUtils.updateViewers.getState().equals(Thread.State.NEW)){
            ViewerUtils.updateViewers.start();
        }
        if(ModUtils.updateMods.getState().equals(Thread.State.NEW)){
            ModUtils.updateMods.start();
        }
        if(StreamingUtils.checkIfOnline.getState().equals(Thread.State.NEW)){
            StreamingUtils.checkIfOnline.start();
        }

    }
}
