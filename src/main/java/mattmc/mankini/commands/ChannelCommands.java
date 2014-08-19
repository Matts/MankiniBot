package mattmc.mankini.commands;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.common.ModCommon;
import mattmc.mankini.common.StreamingCommon;
import mattmc.mankini.common.ViewerCommon;
import mattmc.mankini.utils.*;
import org.json.JSONObject;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

/**
 * Project MankiniBot
 * Created by MattMc on 5/24/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class ChannelCommands extends ListenerAdapter<PircBotX> {


    public ChannelCommands(){
        try {
            ModCommon.updateModerators();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!viewers"))    {
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG).equals(Permissions.Perms.REG)){
                JSONObject json = new JSONObject(JSONParser.readUrl("http://tmi.twitch.tv/group/user/runew0lf/chatters"));
                MessageSending.sendNormalMessage(json.get("chatter_count") + " viewers currently watching!", event);
            }
        }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!version")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG).equals(Permissions.Perms.REG)){
                MessageSending.sendNormalMessage("MankiniBot - Current Version: " + MankiniBot.VERSION, event);
            }
        }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!updateusers")){
            ViewerCommon.updateViewers();
        }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!updatemods")){
            ModCommon.updateModerators();
        }
        if(event.getMessage().split(" ")[0].equals("!commands") || event.getMessage().split(" ")[0].equals("!help")){
            MessageSending.sendNormalMessage("A List Of Commands Can Be Found Here: http://mattmc.info/bots/mankinibot/", event);
        }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!togglestream")){
            if(event.getUser().getNick().equalsIgnoreCase("runew0lf") || event.getUser().getNick().equalsIgnoreCase("MattMc")){
                if(StreamingCommon.isStreaming==false){
                    MessageSending.sendMessageWithPrefix("Runew0lf has started streaming!", "runew0lf", event);
                    StreamingCommon.isStreaming=true;
                    StreamingCommon.manualOverride=true;
                } else if(StreamingCommon.isStreaming==true){
                    MessageSending.sendMessageWithPrefix("Runew0lf has stopped streaming!", "runew0lf", event);
                    StreamingCommon.isStreaming=false;
                    StreamingCommon.manualOverride=false;
                }
            }
        }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!riot")){
            MessageSending.sendNormalMessage("༼ つ◕_◕༽つ Mankini or Riot ༼ つ◕_◕༽つ", event);
        }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!riot1")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                MessageSending.sendNormalMessage("༼ つ◕_◕༽つ " + event.getMessage().split(" ")[1]+" or Riot ༼ つ◕_◕༽つ", event);
            }
        }
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!raid")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                MessageSending.sendNormalMessage("/me Thanks for watching! Be sure to follow if you enjoyed the stream. Hope to see you again later! Please go raid http://www.twitch.tv/"+event.getMessage().split(" ")[1]+" and say to them - Runew0lf's Mankini Raid!! ༼ つ◕_◕༽つ", event);
            }
        }
    }






}
