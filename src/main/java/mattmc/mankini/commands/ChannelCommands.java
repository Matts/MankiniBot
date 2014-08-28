package mattmc.mankini.commands;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.common.ModCommon;
import mattmc.mankini.common.StreamingCommon;
import mattmc.mankini.common.ViewerCommon;
import mattmc.mankini.utils.*;
import net.sourceforge.jeval.Evaluator;
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
            ViewerCommon.updateViewers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        String message = event.getMessage().split(" ")[0];
        if(message.equalsIgnoreCase("!viewers"))    {
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG, event,true).equals(Permissions.Perms.REG)){
                JSONObject json = new JSONObject(JSONParser.readUrl("http://tmi.twitch.tv/group/user/runew0lf/chatters"));
                MessageSending.sendNormalMessage(json.get("chatter_count") + " viewers currently watching!", event);
            }
        }
        if(message.equalsIgnoreCase("!version")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG, event,true).equals(Permissions.Perms.REG)){
                MessageSending.sendNormalMessage("MankiniBot - Current Version: " + MankiniBot.VERSION, event);
            }
        }
        if(message.equalsIgnoreCase("!updateusers")){
            ViewerCommon.updateViewers();
        }
        if(message.equalsIgnoreCase("!updatemods")){
            ModCommon.updateModerators();
        }
        if(message.equalsIgnoreCase("!commands") || message.equalsIgnoreCase("!help")){
            MessageSending.sendNormalMessage("A List Of Commands Can Be Found Here: http://mattmc.info/bots/mankinibot/", event);
        }
        if(message.equalsIgnoreCase("!togglestream")){
            if(event.getUser().getNick().equalsIgnoreCase("runew0lf") || event.getUser().getNick().equalsIgnoreCase("mattsonmc")){
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
        if(message.equalsIgnoreCase("!riot")){
            MessageSending.sendNormalMessage("༼ つ◕_◕༽つ Mankini or Riot ༼ つ◕_◕༽つ", event);
        }
        if(message.equalsIgnoreCase("!riot1")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD, event,true).equals(Permissions.Perms.MOD)){
                MessageSending.sendNormalMessage("༼ つ◕_◕༽つ " + event.getMessage().split(" ")[1]+" or Riot ༼ つ◕_◕༽つ", event);
            }
        }
        if(message.equalsIgnoreCase("!raid")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD, event,true).equals(Permissions.Perms.MOD)){
                MessageSending.sendNormalMessage("/me Thanks for watching! Be sure to follow if you enjoyed the stream. Hope to see you again later! Please go raid http://www.twitch.tv/"+event.getMessage().split(" ")[1]+" and say to them - Runew0lf's Mankini Raid!! ༼ つ◕_◕༽つ", event);
            }
        }

        //this command grabs the title and stream status
        if(message.equalsIgnoreCase("!status")||message.equalsIgnoreCase("!title")||message.equalsIgnoreCase("!game"))    {
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG, event,true).equals(Permissions.Perms.REG)){
                JSONObject json = new JSONObject(JSONParser.readUrl("https://api.twitch.tv/kraken/channels/runew0lf"));
                MessageSending.sendNormalMessage("playing " + json.get("game")+ ": " + json.get("status"), event);

            }
        }
        if(message.equalsIgnoreCase("!stats"))    {
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG, event,true).equals(Permissions.Perms.REG)){
                JSONObject json = new JSONObject(JSONParser.readUrl("https://api.twitch.tv/kraken/channels/runew0lf"));
                MessageSending.sendNormalMessage("Since he started streaming Runew0lf has gained " + json.get("followers") + ", and the Channel has had " + json.get("views") , event);

            }
        }
        if(message.equalsIgnoreCase("!calc")){
            MessageSending.sendMessageWithPrefix(event.getUser().getNick() + " The Result Is " +  new Evaluator().evaluate(event.getMessage().substring(event.getMessage().split(" ")[1].length()+1)), event.getUser().getNick(), event);
        }
    }






}