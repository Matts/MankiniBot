package mattmc.mankini.commands;

import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.Permissions;
import mattmc.mankini.utils.SQLiteListener;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import java.sql.*;

/**
 * Project MankiniBot
 * Created by MattMc on 6/1/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class CommandFactoid extends SQLiteListener
{
    String db = "database\\factoid.db";
    public CommandFactoid(){
        setupDB();
    }

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);
        if(args[1].equalsIgnoreCase("add")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                if(event.getMessage().length() >= 4){
                    try{
                        int i = args[0].length() + args[1].length() + args[2].length() + args[3].length() + 4;
                        addCommand(event.getMessage().split(" ")[2], event.getMessage().split(" ")[3], event.getUser().getNick(), event.getMessage().substring(i));
                        event.getChannel().send().message("Done!");
                    }catch(SQLException e){
                        event.respond(Colors.RED + e.getMessage());
                    }
                }else{
                    event.respond("Correct Syntax: ^addCommand <lvl(ALL/REG/MOD)> <command> <output>");
                }
            }else{
                event.respond(Strings.NoPerms);
            }
        }
        if(args[1].equalsIgnoreCase("del")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                if(event.getMessage().length() >= 2){
                    try{
                        if(commandExists(event.getMessage().split(" ")[2])){
                            removeCommand(event.getMessage().split(" ")[2]);
                            event.getChannel().send().message(Strings.successfullyRemoved);
                        }
                    }catch(SQLException e){
                        event.respond(Colors.RED + e.getMessage());
                    }
                }
            }else{
                event.respond(Strings.NoPerms);
            }
        }
    }


    @Override
    public void setupDB() {
        Statement stmt = null;
        try {
            openConnection(db);
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `FACTOIDS`(COMMAND CHAR(50), USER CHAR(50), OUTPUT CHAR(255), PERM CHAR(50)) ";
            stmt.executeUpdate(sql);
            stmt.close();
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getOutput(String command) throws SQLException {
        if(commandExists(command)){
        ResultSet result;
        openConnection(db);
        String sql = "SELECT * FROM `FACTOIDS` WHERE `COMMAND`=?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, command.toLowerCase());
            result = preparedStatement.executeQuery();
            if(result.next())
                return result.getString("OUTPUT");
            closeConnection();
        }
        return command;
    }

    public String getPermission(String command) throws SQLException {
        if(commandExists(command)){
            ResultSet result;
            openConnection(db);
            String sql = "SELECT * FROM `FACTOIDS` WHERE `COMMAND`=?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, command.toLowerCase());
            result = preparedStatement.executeQuery();
            if(result.next()){
                return result.getString("PERM");
            }
            closeConnection();
        }
        return command;
    }

    private boolean commandExists(String command) throws SQLException {
        openConnection(db);
        String sql = "SELECT * FROM `FACTOIDS` WHERE `COMMAND`=?";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
            preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, command.toLowerCase());
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next())
                return false;
            resultSet.close();
            preparedStatement.close();
            closeConnection();
        return true;
    }

    public void addCommand(String s, String command, String user, String output) throws SQLException {
        openConnection(db);
        String sql = "INSERT INTO `FACTOIDS` (COMMAND, USER, OUTPUT, PERM) VALUES(?,?,?,?)";
            PreparedStatement statement = c.prepareStatement(sql);
            System.out.println(s + " " + command + " " + user + " " + output);
            statement.setString(1, command.toLowerCase());
            statement.setString(2, user);
            statement.setString(3, output);
            statement.setString(4, s);
            statement.executeUpdate();
        statement.close();
        closeConnection();
    }

    public void removeCommand(String command) throws SQLException {
        openConnection(db);
        String sql = "DELETE FROM `FACTOIDS` WHERE `COMMAND` = ?;";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, command.toLowerCase());
            statement.executeUpdate();
            statement.close();
            closeConnection();
    }
}
