package mattmc.mankini.commands;

import mattmc.mankini.utils.MessageSending;
import mattmc.mankini.utils.Permissions;
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
        super.channelCommand(event);
        String var = "";
        for(int i=1;i<event.getMessage().split(" ").length; i++){
            if(Permissions.getPermission(user, Permissions.Perms.MOD, event, true).equals(Permissions.Perms.MOD)){
                if(event.getMessage().split(" ")[i]!="null"){
                    var+=event.getMessage().split(" ")[i] + " ";
                }
            }
        }
        MessageSending.sendNormalMessage(executeJavaScript(var), event);
    }

    public String executeJavaScript(String script)
    {
        try{
        Context cx = Context.enter();
        cx.setClassShutter(new ClassShutter() {
            public boolean visibleToScripts(String className) {
                return className.startsWith("adapter");
            }
        });
        Scriptable scope = cx.initStandardObjects();
        Object result = cx.evaluateString(scope, script, "<cmd>", 1, null);
        return result.toString();
        }catch(Exception e){
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Context.exit();
        }
    }
}
