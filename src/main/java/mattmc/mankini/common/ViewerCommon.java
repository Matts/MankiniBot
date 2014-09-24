package mattmc.mankini.common;

import mattmc.mankini.utils.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Project MankiniBot
 * Created by MattMc on 7/13/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */
public class ViewerCommon {
    public static ArrayList<String> viewers = new ArrayList<String>();

    public static Thread updateViewers = new Thread("ModCommon"){
        @Override
        public void run() {
            while(true){
                try {
                    updateViewers();
                    sleep(180000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }}};

    public static void updateViewers() throws Exception {

        JSONObject json = new JSONObject(JSONParser.readUrl("http://tmi.twitch.tv/group/user/runew0lf/chatters"));
        JSONArray view = json.getJSONObject("chatters").getJSONArray("viewers");
        viewers.clear();
            for(int j = 0; j < view.length(); j++){
                viewers.add(view.getString(j));
            }
        JSONArray mods = json.getJSONObject("chatters").getJSONArray("moderators");
        for(int j = 0; j < mods.length(); j++){
            viewers.add(mods.getString(j));
        }
    }
}
