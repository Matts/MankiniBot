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

    public ChannelCommands() {
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
        if (message.equalsIgnoreCase("!viewers")) {
            if (Permissions.isRegular(getNick(event), event)) {
                JSONObject json = new JSONObject(JSONParser.readUrl("http://tmi.twitch.tv/group/user/runew0lf/chatters"));
                MessageSending.sendNormalMessage(json.get("chatter_count") + " viewers currently watching!", event);
            }
        }
        if (message.equalsIgnoreCase("!version")) {
            if (Permissions.isRegular(getNick(event), event)) {
                MessageSending.sendNormalMessage("MankiniBot - Current Version: " + MankiniBot.VERSION, event);
            }
        }
        if (message.equalsIgnoreCase("!updateusers")) {
            if (Permissions.isModerator(getNick(event), event)) {
                ViewerCommon.updateViewers();
            }
        }
        if (message.equalsIgnoreCase("!updatemods")) {
            if (Permissions.isModerator(getNick(event), event)) {
                ModCommon.updateModerators();
            }
        }
        if (message.equalsIgnoreCase("!commands") || message.equalsIgnoreCase("!help")) {
            //todo make list of commands
            MessageSending.sendNormalMessage("A list of commands is currently in the works... Please try again on a later date.", event);
        }
        if (message.equalsIgnoreCase("!togglestream")) {
            if (Permissions.isOwner(getNick(event), event)) {
                if (StreamingCommon.isStreaming == false) {
                    MessageSending.sendMessageWithPrefix("Runew0lf has started streaming!", "runew0lf", event);
                    StreamingCommon.isStreaming = true;
                    StreamingCommon.manualOverride = true;
                } else if (StreamingCommon.isStreaming == true) {
                    MessageSending.sendMessageWithPrefix("Runew0lf has stopped streaming!", "runew0lf", event);
                    StreamingCommon.isStreaming = false;
                    StreamingCommon.manualOverride = false;
                }
            }
        }
        if (message.equalsIgnoreCase("!riot")) {
            MessageSending.sendNormalMessage("༼ つ◕_◕༽つ Mankini or Riot ༼ つ◕_◕༽つ", event);
        }
        if (message.equalsIgnoreCase("!riot1")) {
            if (Permissions.isModerator(getNick(event), event)) {
                MessageSending.sendNormalMessage("༼ つ◕_◕༽つ " + event.getMessage().split(" ")[1] + " or Riot ༼ つ◕_◕༽つ", event);
            }
        }
        if (message.equalsIgnoreCase("!raid")) {
            if (Permissions.isModerator(getNick(event), event)) {
                MessageSending.sendNormalMessage("/me Thanks for watching! Be sure to follow if you enjoyed the stream. Hope to see you again later! Please go raid http://www.twitch.tv/" + event.getMessage().split(" ")[1] + " and say to them - Runew0lf's Mankini Raid!! ༼ つ◕_◕༽つ", event);
            }
        }

        //this command grabs the title and stream status
        if (message.equalsIgnoreCase("!status") || message.equalsIgnoreCase("!title") || message.equalsIgnoreCase("!game")) {
            if (Permissions.isRegular(getNick(event), event)) {
                JSONObject json = new JSONObject(JSONParser.readUrl("https://api.twitch.tv/kraken/channels/runew0lf"));
                MessageSending.sendNormalMessage("playing " + json.get("game") + ": " + json.get("status"), event);

            }
        }
        if (message.equalsIgnoreCase("!stats")) {
            if (Permissions.isRegular(getNick(event), event)) {
                JSONObject json = new JSONObject(JSONParser.readUrl("https://api.twitch.tv/kraken/channels/runew0lf"));
                MessageSending.sendNormalMessage("Since he started streaming Runew0lf has gained " + json.get("followers") + " followers, and the Channel has had " + json.get("views") + " views", event);

            }
        }
        if (message.equalsIgnoreCase("!calc")) {
            MessageSending.sendMessageWithPrefix(getNick(event) + " The Result Is " + new Evaluator().evaluate(event.getMessage().substring(event.getMessage().split(" ")[1].length() + 1)), getNick(event), event);
        }
    }

    private String getNick(MessageEvent<PircBotX> event) {
        return event.getUser().getNick();
    }

    @Override
    public void onSetChannelBan(SetChannelBanEvent<PircBotX> event) throws Exception {
        super.onSetChannelBan(event);
        event.getChannel().send().message(event.getHostmask() + " You Have Been A Bad Boy! GTFO!");
    }
}