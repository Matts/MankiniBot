package mattmc.mankini.module;

import mattmc.mankini.utils.Permissions;
import mattmc.mankini.utils.SQLiteListener;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Project MankiniBot
 * Created by MattMc on 7/12/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */
public class ModuleRegular extends SQLiteListener {
    String db = "database\\regulars.db";

    public ModuleRegular(){
        setupDB();
    }

    @Override
    public void setupDB() {
        Statement stmt = null;
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
            event.respond(user + " is now regular!");
        }else{
            event.respond(user + " is already regular!");
        }
    }

    public void removeRegular(String user, MessageEvent<PircBotX> event) throws SQLException {
        if(isRegular(user)){
            openConnection(db);
            String sql = "DELETE FROM `REG` WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.executeUpdate();
            event.respond(user + " is removed from the regular list!");
        }else{
            event.respond(user + " wasn't regular in the first place!");
        }
        closeConnection();
    }

    public boolean isRegular(String user) throws SQLException {
        openConnection(db);
        String sql = "SELECT * FROM `REG` WHERE `USER`=?";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        preparedStatement = c.prepareStatement(sql);
        preparedStatement.setString(1, user.toLowerCase());
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next())
            return false;
        resultSet.close();
        preparedStatement.close();
        closeConnection();
        return true;
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        String msg = event.getMessage().split(" ")[0];
        if(msg.equalsIgnoreCase("!reg")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                if(event.getMessage().split(" ")[1].equalsIgnoreCase("add")){
                    addRegular(event.getMessage().split(" ")[2], event);
                }else if(event.getMessage().split(" ")[1].equalsIgnoreCase("del")){
                    removeRegular(event.getMessage().split(" ")[2], event);
                }
            }
        }
    }
}
