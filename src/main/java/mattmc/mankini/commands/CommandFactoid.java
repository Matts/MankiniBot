package mattmc.mankini.commands;

import mattmc.mankini.utils.*;
import org.pircbotx.*;
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

    public static boolean isActive;

    public CommandFactoid(){
        setupDB();
    }

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) throws IllegalAccessException, SQLException, InstantiationException {
        super.channelCommand(event);
        if(args[1].equalsIgnoreCase("add")){
            if(Permissions.isModerator(getNick(event),event)){
                if(message.length() >= 4){
                    try{
                        int i = args[0].length() + args[1].length() + args[2].length() + args[3].length() + 4;
                        addCommand(args[2], args[3], user, message.substring(i));
                        MessageSending.sendMessageWithPrefix(user + " Done!", user, event);
                    }catch(SQLException e){
                        MessageSending.sendNormalMessage(Colors.RED + e.getMessage(), event);
                    }
                }else{
                    MessageSending.sendMessageWithPrefix(user + " Correct Syntax: !command add <lvl(ALL/REG/MOD)> <command> <output>",user, event);
                }
            }
        }
        if(args[1].equalsIgnoreCase("edit")){
            if(Permissions.isModerator(getNick(event),event)){
                if(message.length() >= 3){
                    try {
                        String perm;
                        String output;
                        if(existsInDatabase(db, "FACTOIDS", args[2].toLowerCase())){
                            int i = args[0].length() + args[1].length() + args[2].length() + args[3].length() + 4;
                            output = message.substring(i);
                            perm = getPermission(args[2]);
                            removeCommand(args[2]);
                            addCommand(perm, args[2].toLowerCase(), null, output);
                            MessageSending.sendMessageWithPrefix(user + " Command Edited!", user, event);
                        }else{
                            MessageSending.sendMessageWithPrefix(user + " That Command Doesn't Exist!", user, event);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else{
                    MessageSending.sendMessageWithPrefix(user + " Correct Args: !command edit <command_name> <new_output>",user,event);
                }
            }
        }
        if(args[1].equalsIgnoreCase("editperm")){
            if(Permissions.isModerator(getNick(event),event)){
                if(message.length() >= 3){
                    String perm;
                    String output;
                    try {
                        if(existsInDatabase(db, "FACTOIDS", args[2].toLowerCase())){
                            output = getOutput(args[2]);
                            perm = args[3];
                            removeCommand(args[2]);
                            addCommand(perm, args[2].toLowerCase(), null, output);
                            MessageSending.sendMessageWithPrefix(user + " The command " + args[2] + " now has new permissions!", user, event);
                        }else{
                            MessageSending.sendMessageWithPrefix(user + " That Command Doesn't Exist!", user, event);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                MessageSending.sendMessageWithPrefix(user + " Correct Args: !command edit <command_name> <new_output>",user,event);
            }
        }
        if(args[1].equalsIgnoreCase("del")){
            if(Permissions.isModerator(getNick(event),event)){
                if(message.length() >= 3){
                    try{
                        //if(existsInDatabase(db, "FACTOIDS", args[2].toLowerCase())){
                            removeCommand(args[2]);
                            MessageSending.sendNormalMessage("Successfully Removed Command!", event);
                        //}else{
                        //    MessageSending.sendMessageWithPrefix(user + " That Command Doesn't Exist!", user, event);
                        //}
                    }catch(SQLException e){
                        MessageSending.sendNormalMessage(Colors.RED + e.getMessage(), event);
                    }
                } else{
                    MessageSending.sendMessageWithPrefix(user + " Correct Args: !command del <command>", user, event);
                }
            }
        }
    }

    private String getNick(MessageEvent<PircBotX> event) {
        return event.getUser().getNick();
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
        //if(existsInDatabase(db, "FACTOIDS", command.toLowerCase())){
            ResultSet result;
            openConnection(db);
            String sql = "SELECT * FROM `FACTOIDS` WHERE `COMMAND`=?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, command.toLowerCase());
            result = preparedStatement.executeQuery();
            if(result.next())
                return result.getString("OUTPUT");
            closeConnection();
        //}
        return command;
    }

    public String getPermission(String command) throws SQLException {
        //if(existsInDatabase(db, "FACTOIDS", command.toLowerCase())){
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
        //}
        return command;
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
