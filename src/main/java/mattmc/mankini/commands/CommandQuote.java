package mattmc.mankini.commands;

import mattmc.mankini.utils.MessageSending;
import mattmc.mankini.utils.Permissions;
import org.apache.commons.io.FileUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.*;
import java.sql.SQLException;
import java.util.Random;

/**
 * Project MankiniBot
 * Created by MattMc on 7/12/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */
public class CommandQuote extends CommandBase {
    public static File file = new File("database/quote.txt");
    public static boolean isActive;

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) throws IllegalAccessException, SQLException, InstantiationException {
        super.channelCommand(event);
        if(command.equalsIgnoreCase("!quote")){
            if(args.length==1){
                if(Permissions.isRegular(user,event)){
                    pickRandomQuote(event);
                }
            }
            if(args.length>=2){
                if(args[1].equalsIgnoreCase("add")){
                    if(Permissions.isModerator(user,event)){
                        addQuote(message.substring(11, message.length()), event);
                    }
                }
                if(args[1].equalsIgnoreCase("remove")){
                    MessageSending.sendNormalMessage("Please ask the streamer to manually remove the command, Java doesn't give a way to find and remove a line from a text document :/", event);
                }
            }
        }
    }

    private void addQuote(String content, MessageEvent<PircBotX> event) {
        try {
            if (!file.exists()) {
                System.out.println(file.createNewFile());
            }

            FileWriter fw;

            fw = new FileWriter(file, true);

            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.newLine();
            bw.close();

            MessageSending.sendNormalMessage("Quote Added!", event);
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private void pickRandomQuote(MessageEvent<PircBotX> event) {
        try {
        if (!file.exists()) {
                System.out.println(file.createNewFile());
        }
        FileReader fw = new FileReader(file);
        BufferedReader reader = new BufferedReader(fw);
        String line;
        Random random = new Random();
        int i = 0;
        while ((line = reader.readLine()) != null){
            if(line != null){
                i++;
            }
        }
        i = random.nextInt(i-1);
        if(FileUtils.readLines(file).get(i)!=null){
        MessageSending.sendNormalMessage(FileUtils.readLines(file).get(i).toString(), event);
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
