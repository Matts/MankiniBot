package mattmc.mankini.commands;

import mattmc.mankini.utils.MessageSending;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Project Mankini
 * Created by MattsMc on 8/28/14.
 */

public class CommandJS extends CommandBase {

    public static boolean isActive;
    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        String var = "";
        for(int i=1;i<event.getMessage().split(" ").length; i++){
            if(event.getMessage().split(" ")[i]!="null"){
                var+=event.getMessage().split(" ")[i] + " ";
            }
        }
        System.out.println(var);
            MessageSending.sendNormalMessage(executeJavaScript(var), event);
    }

    public String executeJavaScript(String script)
    {
        try{
        Context cx = Context.enter();
        cx.setClassShutter(new ClassShutter() {
            public boolean visibleToScripts(String className) {
                if(className.startsWith("adapter"))
                    return true;
                return false;
            }
        });
        Scriptable scope = cx.initStandardObjects();
        Object result = cx.evaluateString(scope, script, "<cmd>", 1, null);
        return (String)result;
        }catch(Exception e){
            return e.getMessage();
        } finally {
            Context.exit();
        }
    }
}
