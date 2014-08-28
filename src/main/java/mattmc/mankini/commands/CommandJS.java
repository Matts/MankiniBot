package mattmc.mankini.commands;

import mattmc.mankini.utils.MessageSending;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Project Mankini
 * Created by MattsMc on 8/28/14.
 */

public class CommandJS extends CommandBase {
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
        // Create and enter a Context. A Context stores information about the execution environment of a script.
        Context cx = Context.enter();

        try
        {
            Object obj = cx.compileString(script, null, 1, null);
            return (String)obj;

        }
        catch( Exception e )
        {
            return e.getMessage();
        }
        finally
        {
            Context.exit();
        }
    }
}
