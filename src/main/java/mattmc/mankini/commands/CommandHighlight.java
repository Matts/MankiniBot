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
        getHighlights();
    }

    public HashMap<String, String> highlights = new HashMap<String, String>();

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);
        if(args.length==2){
            if(doesHighlightExist(args[1])){
                MessageSending.sendMessageWithPrefix(user + getHighlight(args[1]), user, event);
            }else{
                MessageSending.sendMessageWithPrefix(user + " that highlight doesn't exist!", user, event);
            }
        }
        if(args.length > 2){
            if(args[1].equalsIgnoreCase("add")){
                if(Permissions.getPermission(user, Permissions.Perms.MOD, event, true).equals(Permissions.Perms.MOD)){
                    addHighlight(args[2], args[3]);
                }
            }
            if(args[1].equalsIgnoreCase("remove")||args[1].equalsIgnoreCase("del")){
                if(Permissions.getPermission(user, Permissions.Perms.MOD, event, true).equals(Permissions.Perms.MOD)){
                    removeHighlight(args[2]);
                }
            }
        }
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
        if(doesHighlightExist(name)){
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
        closeConnection();
        highlights.remove(name);
    }

    public boolean doesHighlightExist(String name){
        try{
            openConnection(db);
            String sql = "SELECT * FROM `HIGHLIGHTS` WHERE `NAME`=?";
            PreparedStatement preparedStatement;
            ResultSet resultSet;
            preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, name.toLowerCase());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return true;
            resultSet.close();
            preparedStatement.close();
            closeConnection();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String getHighlight(String name){
        String url = null;
        url=highlights.get(name);
        return " here you go, " + url + " have fun laughing :D";
    }

    public void getHighlights(){
        String url = "Not Found";
        String name = null;
        openConnection(db);
        try{
            ResultSet result;
            String sql = "SELECT * FROM `HIGHLIGHTS`";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            result = preparedStatement.executeQuery();
            while(result.next()){
                url = result.getString("URL");
                name = result.getString("NAME");
                highlights.put(name, url);
            }
            closeConnection();
            result.close();
            preparedStatement.close();
        }catch(Exception e){
            e.printStackTrace();
        }

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
