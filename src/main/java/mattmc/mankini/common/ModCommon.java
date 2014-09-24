package mattmc.mankini.common;

import mattmc.mankini.utils.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Project MankiniBot
 * Created by MattMc on 7/11/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */
public class ModCommon {
    public static ArrayList<String> moderators = new ArrayList<>();

    public static Thread updateMods = new Thread("ModCommon"){
        @Override
        public void run() {
            while(true){
                try {
                    updateModerators();
                    sleep(300000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public static void updateModerators() throws Exception {
        moderators.clear();
        JSONObject json = new JSONObject(JSONParser.readUrl("http://tmi.twitch.tv/group/user/runew0lf/chatters"));
        for(int i = 0; i < json.length(); i++){
            JSONArray mods = json.getJSONObject("chatters").getJSONArray("moderators");
            for(int j = 0; j < mods.length(); j++){
                moderators.add(mods.getString(j));
            }
        }
    }
}
