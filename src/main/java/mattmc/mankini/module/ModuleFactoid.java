package mattmc.mankini.module;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.ModUtils;
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
        if(command.startsWith("?")){
            try{
            if(commandExists(event.getMessage().split(" ")[0].substring(1))){
                event.getChannel().send().message(getOutput(event.getMessage().split(" ")[0].substring(1)));
            }
            }catch(SQLException e){
                event.respond(Colors.RED + e.getMessage());
            }

        }

        if(command.equalsIgnoreCase("^addcommand")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) || event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
            if(event.getMessage().length() >= 3){
                try{
                if(!commandExists(event.getMessage().split(" ")[1])){
                    int i = 12+event.getMessage().split(" ")[1].length()+1;
                    addCommand(event.getMessage().split(" ")[1], event.getUser().getNick(), event.getMessage().substring(i));
                    event.getChannel().send().message("Done!");
                }else{
                    event.respond(Strings.alreadyExists);
                }
                }catch(SQLException e){
                    event.respond(Colors.RED + e.getMessage());
                }
            }
        }else{
                event.respond(Strings.NoPerms);
            }
        }
        if(command.equalsIgnoreCase("^delcommand")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) || event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
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
            String sql = "CREATE TABLE IF NOT EXISTS `FACTOIDS`(COMMAND CHAR(50), USER CHAR(50), OUTPUT CHAR(50)) ";
            stmt.executeUpdate(sql);
            stmt.close();
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openConnection(String db) {
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" +db);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        super.closeConnection();
    }

    public String getOutput(String command) throws SQLException {
        if(commandExists(command)){
        String result;
        openConnection(db);
        String sql = "SELECT * FROM `FACTOIDS` WHERE `COMMAND`=?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, command.toLowerCase());
            result = preparedStatement.executeQuery().getString("OUTPUT");
            closeConnection();
            return result;
        }
        return command;
    }

    public String getOwner(String command) throws SQLException {
        openConnection(db);
        if(commandExists(command)){
        String result;
        String sql = "SELECT * FROM `FACTOIDS` WHERE `COMMAND`=?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, command.toLowerCase());
            result = preparedStatement.executeQuery().getString("USER");
            closeConnection();
            return result;
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

    public void addCommand(String command, String user, String output) throws SQLException {
        openConnection(db);
        String sql = "INSERT INTO `FACTOIDS` (COMMAND, USER, OUTPUT) VALUES(?,?,?)";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, command.toLowerCase());
            statement.setString(2, user);
            statement.setString(3, output);
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
