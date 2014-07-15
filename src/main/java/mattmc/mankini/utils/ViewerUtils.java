package mattmc.mankini.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Project Mankini
 * Created by MattsMc on 7/13/14.
 */
public class ViewerUtils {
    public static ArrayList<String> viewers = new ArrayList<String>();

    public static Thread updateViewers = new Thread("ModUtils"){
        @Override
        public void run() {
            while(true){
                try {
                    updateViewers();
                    updateViewers.sleep(180000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }}};

    public static void updateViewers() throws Exception {
        viewers.clear();
        JSONObject json = new JSONObject(JSONParser.readUrl("http://tmi.twitch.tv/group/user/runew0lf/chatters"));
        JSONArray view = json.getJSONObject("chatters").getJSONArray("viewers");
            for(int j = 0; j < view.length(); j++){
                viewers.add(view.getString(j));
            }
        JSONArray mods = json.getJSONObject("chatters").getJSONArray("moderators");
        for(int j = 0; j < mods.length(); j++){
            viewers.add(mods.getString(j));
        }
    }
}
