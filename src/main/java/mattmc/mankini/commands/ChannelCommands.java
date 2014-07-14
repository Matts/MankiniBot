package mattmc.mankini.commands;

import com.sun.xml.internal.ws.api.server.Module;
import mattmc.mankini.MankiniBot;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.module.ModuleKinis;
import mattmc.mankini.module.ModuleRegular;
import mattmc.mankini.utils.JSONParser;
import mattmc.mankini.utils.ModUtils;
import mattmc.mankini.utils.StreamingUtils;
import mattmc.mankini.utils.ViewerUtils;
import org.json.JSONObject;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

/**
 * Project MrBot
 * Created by MattsMc on 5/24/14.
 */

public class ChannelCommands extends ListenerAdapter<PircBotX> {


    public ChannelCommands(){
        try {
            ModUtils.updateModerators();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
    if(event.getMessage().split(" ")[0].equalsIgnoreCase("!viewers"))    {
        if((boolean)ModuleRegular.class.getMethod("isRegular", String.class).invoke(ModuleRegular.class.newInstance(), event.getUser().getNick())){
        JSONObject json = new JSONObject(JSONParser.readUrl("http://tmi.twitch.tv/group/user/runew0lf/chatters"));
        event.respond(json.get("chatter_count") + Strings.currentlyWatching);
        }
    }
    if(event.getMessage().split(" ")[0].equalsIgnoreCase("!updateusers")){
        ModUtils.updateModerators();
    }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!updatemods")){
            ViewerUtils.updateViewers();
        }
        if(event.getMessage().split(" ")[0].equals("!commands") || event.getMessage().split(" ")[0].equals("!help")){
            event.respond("A List Of Commands Can Be Found Here: http://mattmc.info/bots/mankinibot/");
        }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!togglestream")){
            if(event.getUser().getNick().equalsIgnoreCase("runew0lf") || event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(StreamingUtils.isStreaming==false){
                    event.getChannel().send().message("Runew0lf has started streaming!");
                    StreamingUtils.isStreaming=true;
                    StreamingUtils.manualOverride=true;
                }else if(StreamingUtils.isStreaming==true){
                    event.getChannel().send().message("Runew0lf has stopped streaming!");
                    StreamingUtils.isStreaming=false;
                    StreamingUtils.manualOverride=false;
                }
                }
            }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!riot")){
            event.getChannel().send().message("༼ つ◕_◕༽つ Mankini or Riot ༼ つ◕_◕༽つ");
        }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!riot1")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) || event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                event.getChannel().send().message("༼ つ◕_◕༽つ " + event.getMessage().split(" ")[1]+" or Riot ༼ つ◕_◕༽つ");
            }
        }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!raid")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) || event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                event.getChannel().send().message("/me Thanks for watching! Be sure to follow if you enjoyed the stream. Hope to see you again later! Please go raid http://www.twitch.tv/"+event.getMessage().split(" ")[1]+" and say to them - Runew0lf's Mankini Raid!! ༼ つ◕_◕༽つ");
            }
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
