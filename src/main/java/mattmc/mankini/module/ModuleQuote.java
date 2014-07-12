package mattmc.mankini.module;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.utils.ModUtils;
import org.apache.commons.io.FileUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
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
            if(ModUtils.moderators.contains(event.getUser().getNick())||event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
            pickRandomQuote(event);
            }
        }
        if(command.equalsIgnoreCase("!addquote")){
            if(ModUtils.moderators.contains(event.getUser().getNick())||event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
            addQuote(event.getMessage().substring(10, event.getMessage().length()), event);
            }
        }
        if(command.equalsIgnoreCase("!removequote")){
            event.respond("Please ask the streamer to manually remove the command, Java doesn't give a way to find and remove a line from a text document :/");
            removeQuote(event.getMessage().substring(10, event.getMessage().length()));
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

    private void removeQuote(String content) throws IOException {
        FileUtils.readLines(file).remove(1);
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

