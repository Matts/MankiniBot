package mattmc.mankini;


import mattmc.mankini.common.Commands;
import mattmc.mankini.module.Hooks;
import mattmc.mankini.commands.*;
import mattmc.mankini.module.*;
import mattmc.mankini.libs.GuiApp;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Scanner;

/**
 * Project MankiniBot
 * Created by MattMc on 5/24/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class MankiniBot {

    public static final String VERSION = "V2.2";
    public static boolean runGUI = true;

    public static Logger logger = LoggerFactory.getLogger(MankiniBot.class);

    public static Map<String, Object> conf, strings;

    static Yaml yaml = new Yaml();

    public static void main(String[] args){
        setupDefaultConfigs();
        
        if (!GraphicsEnvironment.isHeadless()) {
            new GuiApp(args);
        }
        
        new MankiniBot();
    }

    static File serverConfig = new File("config/server.yml");
    static File stringsFile = new File("config/strings.yml");

    public static void setupDefaultConfigs(){
        try{
            File f = new File("database");
            File g = new File("config");
            f.mkdir();
            g.mkdir();
        } catch(Exception e){
            e.printStackTrace();
        }

        CheckStrings();

        CheckServer();

        try {
            conf = (Map<String, Object>) yaml.load(new FileInputStream(
                    serverConfig));
            strings = (Map<String, Object>) yaml.load(new FileInputStream(stringsFile));
        } catch (FileNotFoundException e) {
            logger.info(e.getMessage());
        }
    }

    private static void CheckServer() {
        if(!serverConfig.exists()){
            logger.info("First Time Config Setup, Please edit the config after it got written...");
            try {
                serverConfig.createNewFile();
                Scanner scanner = new Scanner(MankiniBot.class.getResourceAsStream(
                        "./defaultServerConfig.yml"));
                FileWriter fileWriter = new FileWriter(serverConfig);
                while (scanner.hasNextLine()) {
                    fileWriter.write(scanner.nextLine() + '\n');
                }
                fileWriter.close();
                scanner.close();
                logger.info("Finished writing default config.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void CheckStrings() {
        if(!stringsFile.exists()){
            try {
                stringsFile.createNewFile();
                System.out.println(MankiniBot.class.getPackage());
                Scanner scanner = new Scanner(MankiniBot.class.getResourceAsStream(
                        "./defaultStrings.yml"));
                FileWriter fileWriter = new FileWriter(stringsFile);
                while (scanner.hasNextLine()) {
                    fileWriter.write(scanner.nextLine() + '\n');
                }
                fileWriter.close();
                scanner.close();
                logger.info("Finished writing strings");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    Configuration server = new Configuration.Builder()
                .setEncoding(Charset.forName("UTF8"))

                .setName((String) conf.get("nick"))
                .setAutoNickChange(true)

                .setServerHostname((String) conf.get("serverHost"))
                .setServerPassword((String) conf.get("OAuth"))
                .setServerPort(6667)
                .addAutoJoinChannel("#" + conf.get("autoJoinChannel"))

                .addListener(new ModuleSendMessages())

                .addListener(new ChannelCommands())

                .addListener(new Commands())
                .addListener(new Hooks())

                .buildConfiguration();

    public MankiniBot(){
       try {
           PircBotX myBot = new PircBotX(server);
           myBot.startBot();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }
}
