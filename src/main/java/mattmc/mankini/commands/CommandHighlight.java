package mattmc.mankini.commands;

import mattmc.mankini.utils.*;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.*;
import java.util.HashMap;

/**
 * Project MankiniBot
 * Created by MattMc on 6/1/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class CommandHighlight extends SQLiteListener {
    public static String db = "database/highlight";

    public static boolean isActive;

    public CommandHighlight(){
        setupDB();
        try {
            updateCache(db, "HIGHLIGHTS", highlights, "NAME", "URL");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> highlights = new HashMap<String, String>();

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) throws IllegalAccessException, SQLException, InstantiationException {
        super.channelCommand(event);
        if(args.length==2){
            try {
                if(existsInDatabase(db, "HIGHLIGHTS", args[1].toLowerCase())){
                    MessageSending.sendMessageWithPrefix(user + getHighlight(args[1]), user, event);
                }else{
                    MessageSending.sendMessageWithPrefix(user + " that highlight doesn't exist!", user, event);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(args.length > 2){
            if(args[1].equalsIgnoreCase("add")){
                if(Permissions.isModerator(getNick(event),event)){
                    addHighlight(args[2], args[3]);
                }
            }
            if(args[1].equalsIgnoreCase("remove")||args[1].equalsIgnoreCase("del")){
                if(Permissions.isModerator(getNick(event),event)){
                    removeHighlight(args[2]);
                }
            }
        }
    }

    private String getNick(MessageEvent<PircBotX> event) {
        return event.getUser().getNick();
    }

    public void addHighlight(String name, String url){
        openConnection(db);
        String sql = "INSERT INTO `HIGHLIGHTS`(NAME, URL) VALUES(?,?)";
        try{
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, name.toLowerCase());
            statement.setString(2, url);
            statement.executeUpdate();
            statement.close();
            closeConnection();
            highlights.put(name, url);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void removeHighlight(String name){
        try {
            if(existsInDatabase(db, "HIGHLIGHTS", name.toLowerCase())){
                openConnection(db);
                String sql = "DELETE FROM `HIGHLIGHTS` WHERE `NAME`=?";
                try{
                    PreparedStatement statement = c.prepareStatement(sql);
                    statement.setString(1, name.toLowerCase());
                    statement.executeUpdate();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
        highlights.remove(name);
    }

    public String getHighlight(String name){
        return " here you go, " + highlights.get(name) + " have fun laughing :D";
    }

    @Override
    public void setupDB() {
        try {
            openConnection(db);
            Statement stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `HIGHLIGHTS`(NAME CHAR(50), URL CHAR(50));";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            closeConnection();
        }
    }
}
