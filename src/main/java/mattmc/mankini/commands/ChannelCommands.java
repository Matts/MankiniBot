package mattmc.mankini.commands;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.module.ModuleKinis;
import mattmc.mankini.module.ModuleRegular;
import mattmc.mankini.module.ModuleSendMessages;
import mattmc.mankini.utils.JSONParser;
import mattmc.mankini.utils.ModUtils;
import mattmc.mankini.utils.StreamingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import sun.misc.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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
    if(event.getMessage().split(" ")[0].equalsIgnoreCase("!updatemods")){
        ModUtils.updateModerators();
    }
        if(event.getMessage().split(" ")[0].equals("!commands") || event.getMessage().split(" ")[0].equals("!help")){
            event.respond("A List Of Commands Can Be Found Here: http://pastebin.com/pwRCtNYx");
        }
        if(event.getMessage().split(" ")[0].equals("!togglestream")){
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
                System.out.println(StreamingUtils.isOnline());
                }
            }

        if(ModUtils.updateMods.getState().equals(Thread.State.NEW)){
            ModUtils.updateMods.start();
        }
        if(StreamingUtils.checkIfOnline.getState().equals(Thread.State.NEW)){
            StreamingUtils.checkIfOnline.start();
        }
    }






}
