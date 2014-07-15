package mattmc.mankini.module;

import mattmc.mankini.utils.Permissions;
import org.apache.commons.io.FileUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.*;
import java.util.Random;

/**
 * Project Mankini
 * Created by MattsMc on 7/12/14.
 */
public class ModuleQuote extends ListenerAdapter<PircBotX> {
    public static File file = new File("database/quote.txt");
    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {

        String command = event.getMessage().split(" ")[0];
        if(command.equalsIgnoreCase("!quote")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG).equals(Permissions.Perms.REG)){
                pickRandomQuote(event);
            }
        }
        if(command.equalsIgnoreCase("!addquote")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                addQuote(event.getMessage().substring(10, event.getMessage().length()), event);
            }
        }
        if(command.equalsIgnoreCase("!removequote")){
            event.respond("Please ask the streamer to manually remove the command, Java doesn't give a way to find and remove a line from a text document :/");
        }
    }

    private void addQuote(String content, MessageEvent<PircBotX> event) throws IOException {
        // if file doesnt exists, then create it
        if (!file.exists()) {
            System.out.println(file.createNewFile());
        }

        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.newLine();
        bw.close();

        event.respond("Quote Added!");
    }

    private void pickRandomQuote(MessageEvent<PircBotX> event) throws IOException {
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
        }

    }

