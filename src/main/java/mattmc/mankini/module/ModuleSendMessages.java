package mattmc.mankini.module;

import mattmc.mankini.libs.Strings;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Random;

/**
 * Project Mankini
 * Created by MattsMc on 7/12/14.
 */

public class ModuleSendMessages extends ListenerAdapter<PircBotX> {
    private static MessageEvent event;
    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        this.event = event;
        if(ModuleSendMessages.sendMessage.getState().equals(Thread.State.NEW)){
            ModuleSendMessages.sendMessage.start();
        }
    }

    public static Thread sendMessage = new Thread(){
        @Override
        public void run() {
            while(true){
               try {
                   event.getChannel().send().message(Strings.sendMessage);
                   sendMessage.sleep(60000*Strings.sendMessageSleepTime);
                    } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
