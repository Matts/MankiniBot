package mattmc.mankini.module;

import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.Permissions;
import mattmc.mankini.utils.SQLiteListener;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import java.sql.*;

/**
 * Project MrBot
 * Created by MattsMc on 6/1/14.
 */
public class ModuleFactoid extends SQLiteListener
{
    String db = "database\\factoid.db";
    public ModuleFactoid(){
        setupDB();
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        String command = event.getMessage().split(" ")[0];
        if(command.startsWith("!")){
            String output = getOutput(event.getMessage().split(" ")[0].substring(1));
            if(output.contains("%r")){
                if(event.getMessage().split(" ").length>1){
                    output = output.replaceAll("%r", event.getMessage().split(" ")[1]);
                }
            }
            if(output.contains("%s")){
                if(event.getMessage().split(" ").length>2){
                    output = output.replaceAll("%s", event.getMessage().split(" ")[2]);
                }
            }
            if(!getPermission(event.getMessage().split(" ")[0].substring(1)).equalsIgnoreCase("ALL")){
                if(!getPermission(event.getMessage().split(" ")[0].substring(1)).equalsIgnoreCase("REG")){
                    if(getPermission(event.getMessage().split(" ")[0].substring(1)).equalsIgnoreCase("MOD")){
                        if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                            event.getChannel().send().message(output);
                        }
                    }
            }else{
                if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.REG).equals(Permissions.Perms.REG)){
                    event.getChannel().send().message(output);
                }
            }
            }else{
                event.getChannel().send().message(output);
            }
        }


        if(command.equalsIgnoreCase("!addcommand")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
            if(event.getMessage().length() >= 4){
                try{
               // if(!commandExists(event.getMessage().split(" ")[1])){
                    int i = event.getMessage().split(" ")[0].length()+event.getMessage().split(" ")[1].length()+event.getMessage().split(" ")[2].length()+3;
                    addCommand(event.getMessage().split(" ")[1], event.getMessage().split(" ")[2], event.getUser().getNick(), event.getMessage().substring(i));
                    event.getChannel().send().message("Done!");
                //}else{
                    //event.respond(Strings.alreadyExists);
                //}
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
        if(command.equalsIgnoreCase("!delcommand")){
            if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
            if(event.getMessage().length() >= 2){
                try{
                    if(commandExists(event.getMessage().split(" ")[1])){
                        removeCommand(event.getMessage().split(" ")[1]);
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
