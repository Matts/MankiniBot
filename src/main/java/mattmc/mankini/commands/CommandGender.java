package mattmc.mankini.commands;

import mattmc.mankini.utils.*;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.*;
import java.util.HashMap;

/**
 * Project Mankini
 * Created by MattsMc on 8/29/14.
 */
public class CommandGender extends SQLiteListener{
    String db = "database\\kinis.db";
    public static HashMap<String, String> gender = new HashMap<>();

    public static boolean isActive;

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);
        if(args.length<2){
            if(hasGender(user)){
                MessageSending.sendMessageWithPrefix(user + " is a " + getGender(user), user, event);
            }else{
                MessageSending.sendMessageWithPrefix(user + " doesn't have a gender!", user, event);
            }
        }
        if(args.length>=2){
            if(args[1].equalsIgnoreCase("get")){
                if(hasGender(user)){
                    MessageSending.sendMessageWithPrefix(user + " " + args[2] + " is a " + getGender(args[2]), user, event);
                }else{
                    MessageSending.sendMessageWithPrefix(user + " " + args[2] + " doesn't have a gender!", user, event);
                }
            }
            if(args[1].equalsIgnoreCase("add")){
                addGender(user, message.substring(args[0].length()+args[1].length()+2), event);
            }
            if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("del")){
                removeGender(user, event);
            }
            if(args[1].equalsIgnoreCase("madd")){
                if(Permissions.getPermission(user, Permissions.Perms.MOD, event, true).equals(Permissions.Perms.MOD)){
                    addGender(args[2], message.substring(args[0].length() + args[1].length() + args[2].length() + 3), event);
                }
            }
            if(args[1].equalsIgnoreCase("mremove") || args[1].equalsIgnoreCase("mdel")){
                if(Permissions.getPermission(user, Permissions.Perms.MOD, event, true).equals(Permissions.Perms.MOD)){
                    removeGender(args[2], event);
                }
            }
        }
    }

    public CommandGender(){
        setupDB();
        getGenders();
    }

    @Override
    public void setupDB() {
        Statement stmt;
        try {
            openConnection(db);
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `GENDER`(USER CHAR(50), GENDER CHAR(50));";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }

    private void getGenders(){
        gender.clear();
        openConnection(db);
        try {
            String sql = "SELECT * FROM `GENDER`";
            PreparedStatement statement = c.prepareStatement(sql);
            ResultSet set;
            set = statement.executeQuery();

            while(set.next()){
                gender.put(set.getString("USER").toLowerCase(), set.getString("GENDER"));
            }
            statement.close();
            set.close();
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addGender(String user, String genders, MessageEvent<PircBotX> event){
        openConnection(db);
        String sql = "INSERT INTO `GENDER`(USER, GENDER) VALUES(?,?)";
        try{
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.setString(2, genders);
            statement.executeUpdate();
            statement.close();
            closeConnection();
            gender.put(user.toLowerCase(), genders);
            MessageSending.sendMessageWithPrefix(user + " now has the gender " + genders, user, event);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void removeGender(String user, MessageEvent<PircBotX> event){
        if(hasGender(user)){
            openConnection(db);
            String sql = "DELETE FROM `GENDER` WHERE `USER`=?";
            try{
                PreparedStatement statement = c.prepareStatement(sql);
                statement.setString(1, user.toLowerCase());
                statement.executeUpdate();
                gender.remove(user.toLowerCase());
                MessageSending.sendMessageWithPrefix(user + " now doesn't have a gender anymore!", user, event);
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }

    }

    public String getGender(String user){
        if(hasGender(user)){
                return gender.get(user.toLowerCase());
        }
        return user;
    }

    public boolean hasGender(String user){
        if(gender.containsKey(user.toLowerCase())){
            return true;
        }
        return false;
    }
}
