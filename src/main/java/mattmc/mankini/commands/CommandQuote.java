package mattmc.mankini.commands;

import mattmc.mankini.utils.Permissions;
import org.apache.commons.io.FileUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.*;
import java.util.Random;

/**
 * Project MankiniBot
 * Created by MattMc on 7/12/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */
public class CommandQuote extends CommandBase {
    public static File file = new File("database/quote.txt");

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);
        if(command.equalsIgnoreCase("!quote")){
            if(args.length==1){
                if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG).equals(Permissions.Perms.REG)){
                    pickRandomQuote(event);
                }
            }
            if(args.length>=2){
                if(args[1].equalsIgnoreCase("add")){
                    if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                        addQuote(event.getMessage().substring(11, event.getMessage().length()), event);
                    }
                }
                if(args[1].equalsIgnoreCase("remove")){
                    event.respond("Please ask the streamer to manually remove the command, Java doesn't give a way to find and remove a line from a text document :/");
                }
            }
        }
    }

    private void addQuote(String content, MessageEvent<PircBotX> event) {
        try {
            if (!file.exists()) {
                System.out.println(file.createNewFile());
            }

            FileWriter fw = null;

            fw = new FileWriter(file, true);

            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.newLine();
            bw.close();

            event.respond("Quote Added!");
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
        String line = null;
        Random random = new Random();
        int i = 0;
        while ((line = reader.readLine()) != null){
            if(line != null){
                i++;
            }
        }
        i = random.nextInt(i-1);
        if(FileUtils.readLines(file).get(i)!=null){
        event.getChannel().send().message(FileUtils.readLines(file).get(i).toString());
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
