package mattmc.mankini.utils;

import org.json.JSONObject;

/**
 * Project Mankini
 * Created by MattsMc on 7/12/14.
 */
public class StreamingUtils {
    public static boolean manualOverride = false;
    public static boolean isStreaming = false;

    public static Thread checkIfOnline = new Thread(){
        @Override
        public void run() {
            while(true){
            try {
                Thread.sleep(240000);
                isOnline();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        }
    };

    public static boolean isOnline() throws Exception {
        try {
            if(!manualOverride){
            if(isStreaming){
            if(new JSONObject(JSONParser.readUrl("https://api.twitch.tv/kraken/streams/runew0lf")).getString("stream").contains("null")){
                isStreaming=false;
            }
            }else if(!new JSONObject(JSONParser.readUrl("https://api.twitch.tv/kraken/streams/runew0lf")).getString("stream").contains("null")){
                isStreaming=true;
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject(JSONParser.readUrl("https://api.twitch.tv/kraken/streams/runew0lf")).getString("stream")!=null;
    }
}
