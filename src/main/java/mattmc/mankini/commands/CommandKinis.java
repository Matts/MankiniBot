package mattmc.mankini.commands;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.*;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;

/**
 * Project MankiniBot
 * Created by MattMc on 7/10/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class CommandKinis extends SQLiteListener {
    String db = "database\\kinis.db";
    boolean isLocked=false;
    boolean bool = false;
    public Thread kinis = new Thread(){
        public void run(){
            while(true){
                bool=false;
                while(StreamingUtils.isStreaming){
                    System.out.println("Auto Kini's Started");
                    try {
                        kinis.sleep(300000);
                        autoTickAddKikis();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                try {
                    kinis.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public CommandKinis(){
        setupDB();
        if(kinis.getState().equals(Thread.State.NEW)){
                kinis.start();
        }
    }

    public void autoTickAddKikis() {
        System.out.println("5 Min Kini :D");
        for(int i = 0; i<ViewerUtils.viewers.size();i++){
            try {
                addKinis(ViewerUtils.viewers.get(i), 1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setupDB() {
        Statement stmt = null;
        try {
            openConnection(db);
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `KINIS`(USER CHAR(50), AMOUNT INT);";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }

    public String getTop3(MessageEvent<PircBotX> event) throws SQLException {
        openConnection(db);
        String sql = "SELECT * FROM `KINIS` ORDER BY AMOUNT DESC LIMIT 3";
        PreparedStatement statement = c.prepareStatement(sql);
        ResultSet set = statement.executeQuery();
        set.next();
        String user1 = ("1: " + set.getString("USER") + " : " + set.getString("AMOUNT") + " kinis");
        set.next();
        String user2 = ("2: " + set.getString("USER") + " : " + set.getString("AMOUNT") + " kinis");
        set.next();
        String user3 = ("3: " + set.getString("USER") + " : " + set.getString("AMOUNT") + " kinis");
        MessageSending.sendNormalMessage(user1 + "  --  " + user2 + "  --  " + user3, event);

        closeConnection();
        return null;
    }

    public void addUser(String user) throws SQLException {
        openConnection(db);
        String sql = "INSERT INTO `KINIS`(USER, AMOUNT) VALUES(?,?)";
        PreparedStatement statement = c.prepareStatement(sql);
        statement.setString(1, user.toLowerCase());
        statement.setInt(2, 1);
        statement.executeUpdate();
        statement.close();
        closeConnection();
    }

    public void removeUser(String user) throws SQLException {
        if(userExists(user)){
            openConnection(db);
            String sql = "DELETE FROM `KINIS` WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.executeUpdate();
        }
        closeConnection();
    }

    public void removeKinis(String user, int amount) throws SQLException {
        if(userExists(user)){
            int oldAmount = getKinis(user.toLowerCase());
            int newAmount = oldAmount-amount;
            openConnection(db);
            String sql = "UPDATE `KINIS` SET `AMOUNT`=? WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
        statement.setInt(1, newAmount);
            statement.setString(2, user.toLowerCase());
            statement.executeUpdate();
            closeConnection();
         }
    }

    public void addKinis(String user, int amount) throws SQLException {
        if(userExists(user)){
        int oldAmount = getKinis(user.toLowerCase());
        int newAmount = oldAmount+amount;
        openConnection(db);
        String sql = "UPDATE `KINIS` SET `AMOUNT`=? WHERE `USER`=?";
        PreparedStatement statement = c.prepareStatement(sql);
        statement.setInt(1, newAmount);
        statement.setString(2, user.toLowerCase());
        statement.executeUpdate();
        closeConnection();
        }else{
            addUser(user.toLowerCase());
            addKinis(user.toLowerCase(), amount);
        }
    }

    public boolean userExists(String user) throws SQLException {
        openConnection(db);
        String sql = "SELECT * FROM `KINIS` WHERE `USER`=?";
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

    public int getKinis(String user) throws SQLException {
        int resulty = -1;
            openConnection(db);
            ResultSet result;
            String sql = "SELECT * FROM `KINIS` WHERE `USER`=?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, user.toLowerCase());
            result = preparedStatement.executeQuery();
            if(result.next()){
                resulty = result.getInt("AMOUNT");
            }
           closeConnection();
           result.close();
           preparedStatement.close();
            return resulty;
    }

    private void allKini(int amount) throws SQLException {
        openConnection(db);
        String sql = "UPDATE `KINIS` SET `AMOUNT`=AMOUNT+?";
        PreparedStatement statement = c.prepareStatement(sql);
        statement.setInt(1, amount);
        statement.executeUpdate();
        closeConnection();
    }

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);
        try{
            if(message.equalsIgnoreCase("!kinis")){
            if(args.length<=1){
                if(!isLocked){
                    if(userExists(event.getUser().getNick())){
                            MessageSending.sendMessageWithPrefix(user + Strings.has + getKinis(event.getUser().getNick()) + Strings.totalKinis, user, event);
                    }else{
                        addUser(event.getUser().getNick());
                        MessageSending.sendMessageWithPrefix(user + Strings.has + getKinis(event.getUser().getNick()) + Strings.totalKinis, user, event);
                    }
                }else{
                    MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
                }
            }
            }
        if(args[1].equalsIgnoreCase("rank")){
            getTop3(event);
        }

        if(args[1].equalsIgnoreCase("get")){
            if(!isLocked){
                if(event.getMessage().split(" ").length >= 2){
                    if(userExists(event.getMessage().split(" ")[2])){
                       MessageSending.sendMessageWithPrefix(event.getMessage().split(" ")[2] + Strings.has + getKinis(event.getMessage().split(" ")[2]) + Strings.totalKinis, event.getMessage().split(" ")[2],  event);
                    }else{
                        addUser(event.getMessage().split(" ")[1]);
                        MessageSending.sendMessageWithPrefix(event.getMessage().split(" ")[2] + Strings.has + getKinis(event.getMessage().split(" ")[2]) + Strings.totalKinis,event.getMessage().split(" ")[2], event);
                    }
                }else{
                    MessageSending.sendNormalMessage(Strings.getKinisExplain,event);
                }
            }else{
                MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }
        if(args[1].equalsIgnoreCase("add")){
            if(!isLocked){
                if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                    if(event.getMessage().split(" ").length>=3){
                        if(userExists(event.getMessage().split(" ")[2])){
                            addKinis(event.getMessage().split(" ")[2], Integer.parseInt(event.getMessage().split(" ")[3]));
                            MessageSending.sendMessageWithPrefix(event.getMessage().split(" ")[2] + Strings.haveBeenAdded + event.getMessage().split(" ")[3], event.getMessage().split(" ")[2],event);
                        }else{
                            addUser(event.getMessage().split(" ")[2]);
                            addKinis(event.getMessage().split(" ")[2], Integer.parseInt(event.getMessage().split(" ")[3]));
                            MessageSending.sendMessageWithPrefix(event.getMessage().split(" ")[2] + Strings.haveBeenAdded + event.getMessage().split(" ")[3], event.getMessage().split(" ")[2],event);
                        }
                    }else{
                        MessageSending.sendNormalMessage(Strings.addKiniExplain, event);
                    }
                }else{
                    MessageSending.sendNormalMessage(Strings.NoPerms, event);
                }
            }else{
                MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }
        if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("rem") || args[1].equalsIgnoreCase("del")){
            if(!isLocked){
                if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                    if(event.getMessage().split(" ").length>=3){
                        removeKinis(event.getMessage().split(" ")[2].toLowerCase(), Integer.parseInt(event.getMessage().split(" ")[3]));
                        MessageSending.sendMessageWithPrefix(event.getMessage().split(" ")[2] + Strings.haveBeenRemoved + event.getMessage().split(" ")[3].toLowerCase(),event.getMessage().split(" ")[2], event);
                    }else{
                        MessageSending.sendNormalMessage(Strings.removeKinisExplain, event);
                    }
                }else{
                    MessageSending.sendNormalMessage(Strings.NoPerms, event);
                }
            }else{
                MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }
        if(args[1].equalsIgnoreCase("giveall")){
            if(!isLocked){
                if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                    if(event.getMessage().split(" ").length>=2){
                        allKini(Integer.parseInt(event.getMessage().split(" ")[2]));
                        MessageSending.sendNormalMessage(Strings.everyoneGot + event.getMessage().split(" ")[2] + Strings.kinis, event);
                    }else{
                        MessageSending.sendNormalMessage(Strings.kiniAllExplain, event);
                    }
                }else{
                    MessageSending.sendNormalMessage(Strings.NoPerms, event);
                }
            }else{
                MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }
        if(args[1].equalsIgnoreCase("adduser")){
            if(!isLocked){
                if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                    if(event.getMessage().split(" ").length>=2){
                        if(!userExists(event.getMessage().split(" ")[2])){
                            addUser(event.getMessage().split(" ")[2]);
                            MessageSending.sendNormalMessage(event.getMessage().split(" ")[2] + Strings.haveBeenAdded, event);
                        }else{
                            MessageSending.sendNormalMessage("User Already Exists!", event);
                        }
                    }else{
                        MessageSending.sendNormalMessage(Strings.addUserExplain, event);
                    }
                }else{
                    MessageSending.sendNormalMessage(Strings.NoPerms, event);
                }
            }else{
                MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }
        if(args[1].equalsIgnoreCase("removeuser")){
            if(!isLocked){
                if(Permissions.getPermission(event.getUser().getNick(), Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                    if(event.getMessage().split(" ").length>=2){
                        if(userExists(event.getMessage().split(" ")[2])){
                            removeUser(event.getMessage().split(" ")[2]);
                            MessageSending.sendNormalMessage(event.getMessage().split(" ")[2] + Strings.haveBeenRemoved, event);
                        }else{
                            MessageSending.sendNormalMessage("User Doesn't Exist!", event);
                        }
                    }else{
                        MessageSending.sendNormalMessage(Strings.removeUserExplain, event);
                    }
                }else{
                    MessageSending.sendNormalMessage(Strings.NoPerms, event);
                }
            }else{
               MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }

        if(args[1].equalsIgnoreCase("export")){
            if(event.getUser().getNick().equalsIgnoreCase("runew0lf") ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                isLocked=true;
                MessageSending.sendNormalMessage("Kini Importing started.. All Kini Systems Locked!", event);
                isLocked=false;
            }
        }

        if(args[1].equalsIgnoreCase("import")){
            if(event.getUser().getNick().equalsIgnoreCase("runew0lf") ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                isLocked=true;
                MessageSending.sendNormalMessage("Kini Importing started.. All Kini Systems Locked!", event);
                File dbfile = new File("database\\kinis.db");
                if((boolean)MankiniBot.conf.get("useSQLite")){
                    dbfile.delete();
                    dbfile.createNewFile();
                }
                setupDB();
                File file = new File(event.getMessage().split(" ")[2]);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                int i=0;
                String user = null;
                String kinis = null;
                while ((line = reader.readLine()) != null){
                    if(line.startsWith("[#runew0lf.")){
                        String line1 = line.substring(11, line.length()-1);
                        System.out.println(line1);
                        user = line1;
                        i++;
                    }
                    if(line.startsWith("kinis=")){
                        String line1 = line.substring(6, line.length());
                        System.out.println(line1);
                        kinis = line1;
                        i++;
                    }
                    if(line.isEmpty()){
                        if(!(Integer.parseInt(kinis) <= 5)){
                            setUserAmount(user, Integer.parseInt(kinis));
                            i++;
                            continue;
                        }
                    }
                }
            }
            isLocked=false;
            MessageSending.sendNormalMessage("The Writing Has Been Completed, All Systems Unlocked And Running!", event);
    }
        }catch(Exception e){
            MessageSending.sendNormalMessage(e.getMessage(), event);
        }
    }

    private void setUserAmount(String user, int kinis) {
        try {
            removeUser(user);
            addKinis(user, kinis-1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
