package mattmc.mankini.module;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.ModUtils;
import mattmc.mankini.utils.Permissions;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;

/**
 * Project Mankini
 * Created by MattsMc on 7/11/14.
 */
public class ModuleLinks extends ListenerAdapter<PircBotX> {
    public static ArrayList<String> permitted = new ArrayList<String>();

    public static ArrayList<String> strike1 = new ArrayList<String>();

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
    	
    	//This permits a user to post 1 link
    	//if a mod uses !permit <username> 
        if(event.getMessage().split(" ")[0].equalsIgnoreCase("!permit")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                //take the massage and split it to get the user name 
            	//while adding it to the bot
            	permitted.add(event.getMessage().split(" ")[1]);
            	
                                
                event.respond(event.getUser().getNick() + Strings.hasBeenPermitted + event.getMessage().split(" ")[1]);
            }
        }
        // If the Message is a link
        if(event.getMessage().contains("http") || event.getMessage().contains("www.") || event.getMessage().contains(".com") 
        		|| event.getMessage().contains(".net") || event.getMessage().contains(".co") ||
        		event.getMessage().contains(".co.uk")){
        	//if there not  reg
            if(!Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG).equals(Permissions.Perms.REG)){
            	//if not on the permitted list ?
            	if(!permitted.contains(event.getUser().getNick())){
            		//if not Moderator
            		if(!(ModUtils.moderators.contains(event.getUser().getNick()))){
            			//if not Owner
            			if(!(MankiniBot.Owner.contains(event.getUser().getNick()))){
            				//if not on the strike one list
            				if(!strike1.contains(event.getUser().getNick())){
            					
            					//The actual action
            					event.getBot().sendRaw().rawLine("PRIVMSG " + event.getChannel().getName()
            							+" :.timeout "+ event.getUser().getNick() + " 5");
            					event.respond(Strings.strike1);
            					strike1.add(event.getUser().getNick());
            				
            	}else{
            		if(strike1.contains(event.getUser().getNick())){
            			event.getBot().sendRaw().rawLine("PRIVMSG " + event.getChannel().getName() +" :.timeout "+ event.getUser()
            			.getNick() + Strings.bantime);
            			event.respond(Strings.strike2 + Strings.bantimeOnMSG);
            			strike1.remove(event.getUser().getNick());
                }
            	}
            }
                }
                }
            }//else{
            	//String permedperson = event.getUser().getNick();
                permitted.remove(event.getUser().getNick());
                
            //}
        }
    }
    
}
