package mattmc.mankini.module;

import mattmc.mankini.common.StreamingCommon;
import mattmc.mankini.libs.Strings;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Project MankiniBot
 * Created by MattMc on 7/12/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class ModuleSendMessages extends ListenerAdapter<PircBotX> {
    private static MessageEvent event;
    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        ModuleSendMessages.event = event;
        if(ModuleSendMessages.sendMessage.getState().equals(Thread.State.NEW)){
            ModuleSendMessages.sendMessage.start();
        }
    }

    public static Thread sendMessage = new Thread(){
        @Override
        public void run() {
            while(true){
                if(StreamingCommon.isStreaming){
                try {
                    event.getChannel().send().message(Strings.sendMessage);
                    sleep(60000 * Strings.sendMessageSleepTime);
                        } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
