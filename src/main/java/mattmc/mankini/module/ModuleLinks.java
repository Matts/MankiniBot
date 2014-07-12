package mattmc.mankini.module;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.ModUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Project Mankini
 * Created by MattsMc on 7/11/14.
 */
public class ModuleLinks extends ListenerAdapter<PircBotX> {
    public static ArrayList<String> permitted = new ArrayList<String>();

    public static ArrayList<String> strike1 = new ArrayList<String>();

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!permit")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) || event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                permitted.add(event.getMessage().split(" ")[1]);
                event.respond(event.getUser().getNick() + Strings.hasBeenPermitted + event.getMessage().split(" ")[1]);
            }
        }

        if(event.getMessage().contains("http") || event.getMessage().contains("www.") || event.getMessage().contains(".com") || event.getMessage().contains(".net") || event.getMessage().contains(".co") || event.getMessage().contains(".co.uk")){
            if(!(boolean)ModuleRegular.class.getMethod("isRegular", String.class).invoke(ModuleRegular.class.newInstance(), event.getUser().getNick())){
            if(!permitted.contains(event.getUser().getNick())){
                if(!(ModUtils.moderators.contains(event.getUser().getNick()))){
                    if(!(MankiniBot.Owner.contains(event.getUser().getNick()))){
                if(!strike1.contains(event.getUser().getNick())){
                    event.getBot().sendRaw().rawLine("PRIVMSG " + event.getChannel().getName() +" :.timeout "+ event.getUser().getNick() + " 5");
                    event.respond(Strings.strike1);
                    strike1.add(event.getUser().getNick());
                }else{
                    event.getBot().sendRaw().rawLine("PRIVMSG " + event.getChannel().getName() +" :.timeout "+ event.getUser().getNick() + Strings.bantime);
                    event.respond(Strings.strike2 + Strings.bantimeOnMSG);
                    strike1.remove(event.getUser().getNick());
                }
                }
                }
                }
            }else{
                permitted.remove(event.getUser().getNick());
            }
        }
    }
}
