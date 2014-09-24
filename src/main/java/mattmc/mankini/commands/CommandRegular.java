package mattmc.mankini.commands;

import mattmc.mankini.utils.MessageSending;
import mattmc.mankini.utils.Permissions;
import mattmc.mankini.utils.SQLiteListener;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Project MankiniBot
 * Created by MattMc on 7/12/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */
public class CommandRegular extends SQLiteListener {
    String db = "database\\regulars.db";
    public static boolean isActive;

    public ArrayList<String> regCache = new ArrayList<>();

    public CommandRegular(){
        setupDB();
        updateRegulars();
    }

    public void updateRegulars(){
        openConnection(db);
        regCache.clear();
        try {
        String sql = "SELECT * FROM `REG`";
        PreparedStatement statement = c.prepareStatement(sql);
        ResultSet set;
            set = statement.executeQuery();

        while(set.next()){
            regCache.add(set.getString("USER").toLowerCase());
        }
        statement.close();
        set.close();
        closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupDB() {
        Statement stmt;
        try {
            openConnection(db);
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `REG`(USER CHAR(50));";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }

    @Override
    public void openConnection(String db) {
        super.openConnection(db);
    }

    @Override
    public void closeConnection() {
        super.closeConnection();
    }

    public void addRegular(String user, MessageEvent<PircBotX> event) throws SQLException {
        if(!isRegular(user)){
            openConnection(db);
            String sql = "INSERT INTO `REG`(USER) VALUES(?)";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.executeUpdate();
            statement.close();
            closeConnection();
            MessageSending.sendMessageWithPrefix(user + " is now regular!", user, event);
            regCache.add(user);
        }else{
            MessageSending.sendMessageWithPrefix(user + " is already regular!", user, event);
        }

    }

    public void removeRegular(String user, MessageEvent<PircBotX> event) throws SQLException {
        if(isRegular(user)){
            openConnection(db);
            String sql = "DELETE FROM `REG` WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.executeUpdate();
            MessageSending.sendMessageWithPrefix(user + " is removed from the regular list!", user, event);
            regCache.remove(user);
        }else{
            MessageSending.sendMessageWithPrefix(user + " wasn't regular in the first place!", user, event);
        }
        closeConnection();
    }

    public boolean isRegular(String user) throws SQLException {
        return regCache.contains(user);
    }

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) throws IllegalAccessException, SQLException, InstantiationException {
        super.channelCommand(event);
            if(Permissions.isModerator(user,event)){
                if(args[1].equalsIgnoreCase("add")){
                    try {
                        addRegular(args[2], event);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else if(args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("remove")){
                    try {
                        removeRegular(args[2], event);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else if(args[1].equalsIgnoreCase("check")){
                    try {
                        if(isRegular(args[2])){
                            MessageSending.sendMessageWithPrefix(user + " " + args[2] + " is regular", user, event);
                        }else{
                            MessageSending.sendMessageWithPrefix(user + " " + args[2] + " is not regular", user, event);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
    }
}
