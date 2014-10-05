package mattmc.mankini.commands;

import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.Permissions;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Project MankiniBot
 * Created by MattMc on 6/1/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class CommandLinks extends CommandBase {
    public static ArrayList<String> permitted = new ArrayList<String>();
    public static ArrayList<String> strike1 = new ArrayList<String>();

    public static ArrayList<String> links = new ArrayList<String>();

    public static boolean isActive;
    public static boolean hasCheckedLinks=false;

    public CommandLinks(){
        if(!hasCheckedLinks){
            updateTLDs();
        }
    }

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) throws IllegalAccessException, SQLException, InstantiationException {
        super.channelCommand(event);
        if(Permissions.isModerator(user,event, true)){
                permitted.add(args[1].toLowerCase());
                event.respond(user + " has given permissions to post a link to " + args[1]);
                System.out.println(permitted.get(0).toString());
        }
    }

    //TODO: Clean This Up!
    public static boolean sentenceContainsLink(String sentence, MessageEvent<PircBotX> event) throws IllegalAccessException, SQLException, InstantiationException {
        String[] splitted =  sentence.toLowerCase().split("\\.");
        System.out.println(splitted[1]);
            for (int i=0; i< splitted.length; i++){
                System.out.println(splitted[i]);
                int y =0;
                while(links.iterator().hasNext() && y<links.size()){
                    if(links.get(y).equalsIgnoreCase(splitted[i])){
                    if (Permissions.isPermitted(event.getUser().getNick(), event)) {

                        return false;
                    } else {
                        return true;
                    }

                }
                    y++;
                }

                }
        return false;
    }

    public void updateTLDs(){
        File file = new File("database/TLDs.txt");
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            for(String x = in.readLine(); x != null; x = in.readLine()){
                links.add(x.toLowerCase());
                System.out.println(x.toLowerCase());
            }
            hasCheckedLinks=false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            updateTLDs();
        }
    }
}