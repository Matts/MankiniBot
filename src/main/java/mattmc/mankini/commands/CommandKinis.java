package mattmc.mankini.commands;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.common.StreamingCommon;
import mattmc.mankini.common.ViewerCommon;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.*;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.*;
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
                while(StreamingCommon.isStreaming){
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
        for(int i = 0; i< ViewerCommon.viewers.size();i++){
                addKinis(ViewerCommon.viewers.get(i), 1);
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

    public String getTop3(MessageEvent<PircBotX> event) {
        try{
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
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void addUser(String user){
        openConnection(db);
        String sql = "INSERT INTO `KINIS`(USER, AMOUNT) VALUES(?,?)";
        try{
        PreparedStatement statement = c.prepareStatement(sql);
        statement.setString(1, user.toLowerCase());
        statement.setInt(2, 1);
        statement.executeUpdate();
        statement.close();
        closeConnection();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void removeUser(String user){
        if(userExists(user)){
            openConnection(db);
            String sql = "DELETE FROM `KINIS` WHERE `USER`=?";
            try{
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.executeUpdate();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        closeConnection();
    }

    public void removeKinis(String user, int amount) {
        if(userExists(user)){
            try{
            int oldAmount = getKinis(user.toLowerCase());
            int newAmount = oldAmount-amount;
            openConnection(db);
            String sql = "UPDATE `KINIS` SET `AMOUNT`=? WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
        statement.setInt(1, newAmount);
            statement.setString(2, user.toLowerCase());
            statement.executeUpdate();
            closeConnection();
            }catch(Exception e){
                e.printStackTrace();
            }
         }
    }

    public void addKinis(String user, int amount) {
        if(userExists(user)){
            try{
        int oldAmount = getKinis(user.toLowerCase());
        int newAmount = oldAmount+amount;
        openConnection(db);
        String sql = "UPDATE `KINIS` SET `AMOUNT`=? WHERE `USER`=?";
        PreparedStatement statement = c.prepareStatement(sql);
        statement.setInt(1, newAmount);
        statement.setString(2, user.toLowerCase());
        statement.executeUpdate();
        closeConnection();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            addUser(user.toLowerCase());
            addKinis(user.toLowerCase(), amount);
        }
    }

    public boolean userExists(String user) {
        try{
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
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public int getKinis(String user) {
        int resulty = -1;
            openConnection(db);
        try{
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
        }catch(Exception e){
            e.printStackTrace();
        }
            return resulty;
    }

    private void allKini(int amount) {
        openConnection(db);
        String sql = "UPDATE `KINIS` SET `AMOUNT`=AMOUNT+?";
        try{
        PreparedStatement statement = c.prepareStatement(sql);
        statement.setInt(1, amount);
        statement.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
        closeConnection();
    }

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);
            if(args.length<=1){
                if(!isLocked){
                    if(userExists(user)){
                            MessageSending.sendMessageWithPrefix(user + " has " +getKinis(user) + " total Kinis!", user, event);
                    }else{
                        addUser(user);
                        MessageSending.sendMessageWithPrefix(user + " has " + getKinis(user) + " total Kinis!", user, event);
                    }
                }else{
                    MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
                }
            }else{

        if(args[1].equalsIgnoreCase("rank")){
            getTop3(event);
        }

        if(args[1].equalsIgnoreCase("get")){
            if(!isLocked){
                if(args.length >= 2){
                    if(userExists(args[2])){
                       MessageSending.sendMessageWithPrefix(args[2] +  " has " + getKinis(args[2]) + " total Kinis!", args[2],  event);
                    }else{
                        addUser(args[1]);
                        MessageSending.sendMessageWithPrefix(args[2] + " has " + getKinis(args[2]) + " total Kinis!",args[2], event);
                    }
                }else{
                    MessageSending.sendNormalMessage("Correct Syntax: !kinis get <UserName>",event);
                }
            }else{
                MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }
        if(args[1].equalsIgnoreCase("add")){
            if(!isLocked){
                if(Permissions.getPermission(user, Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                    if(args.length>=3){
                        if(userExists(args[2])){
                            addKinis(args[2], Integer.parseInt(args[3]));
                            MessageSending.sendMessageWithPrefix(args[2] + " have been added " + args[3], args[2],event);
                        }else{
                            addUser(args[2]);
                            addKinis(args[2], Integer.parseInt(args[3]));
                            MessageSending.sendMessageWithPrefix(args[2] + " have been added " + args[3], args[2],event);
                        }
                    }else{
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis add <UserName> <Amount>", event);
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
                if(Permissions.getPermission(user, Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                    if(args.length>=3){
                        removeKinis(args[2].toLowerCase(), Integer.parseInt(args[3]));
                        MessageSending.sendMessageWithPrefix(args[2] + " have been removed " + args[3].toLowerCase(),args[2], event);
                    }else{
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis remove <UserName> <Amount>", event);
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
                if(Permissions.getPermission(user, Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                    if(args.length>=2){
                        allKini(Integer.parseInt(args[2]));
                        MessageSending.sendNormalMessage("Everyone got " + args[2] + " Kinis!!", event);
                    }else{
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis giveall <Amount>", event);
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
                if(Permissions.getPermission(user, Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                    if(args.length>=2){
                        if(!userExists(args[2])){
                            addUser(args[2]);
                            MessageSending.sendNormalMessage(args[2] + " have been removed ", event);
                        }else{
                            MessageSending.sendNormalMessage("User Already Exists!", event);
                        }
                    }else{
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis adduser <UserName>", event);
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
                if(Permissions.getPermission(user, Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                    if(args.length>=2){
                        if(userExists(args[2])){

                            removeUser(args[2]);
                            MessageSending.sendNormalMessage(args[2] + " have been removed ", event);
                        }else{
                            MessageSending.sendNormalMessage("User Doesn't Exist!", event);
                        }
                    }else{
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis remove <UserName>", event);
                    }
                }else{
                    MessageSending.sendNormalMessage(Strings.NoPerms, event);
                }
            }else{
               MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }

        if(args[1].equalsIgnoreCase("export")){
            try{
            if(user.equalsIgnoreCase("runew0lf") ||  user.equalsIgnoreCase(MankiniBot.Owner)){
                isLocked=true;
                MessageSending.sendNormalMessage("Kini Exporting started.. All Kini Systems Locked!", event);
                openConnection(db);
                String sql = "SELECT * FROM `KINIS`";
                PreparedStatement statement = c.prepareStatement(sql);
                ResultSet set = statement.executeQuery();
                File file1 = new File(args[2]);
                if(file1.exists()){
                    file1.delete();
                }
                file1.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file1));
                while(set.next()){
                writer.write("[#runew0lf."+set.getString("USER").toLowerCase() + "]");
                    writer.newLine();
                writer.write("kinis="+set.getInt("AMOUNT"));
                writer.newLine();
                    writer.newLine();
                }
                set.close();
                statement.close();
                writer.close();
                isLocked=false;
                MessageSending.sendNormalMessage("The Writing Has Been Completed, All Systems Unlocked And Running!", event);
                closeConnection();
            }
            }catch(Exception e){
                e.printStackTrace();
            }

        }

        if(args[1].equalsIgnoreCase("import")){
            try{
            if(user.equalsIgnoreCase("runew0lf") ||  user.equalsIgnoreCase(MankiniBot.Owner)){
                isLocked=true;
                MessageSending.sendNormalMessage("Kini Importing started.. All Kini Systems Locked!", event);
                File dbfile = new File("database\\kinis.db");
                if((boolean)MankiniBot.conf.get("useSQLite")){
                    dbfile.delete();
                    dbfile.createNewFile();
                }
                setupDB();
                File file = new File(args[2]);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                int i=0;
                String user = null;
                String kinis = null;
                while ((line = reader.readLine()) != null){
                    if(line.startsWith("[#runew0lf.")){
                        String line1 = line.substring(11, line.length()-1);
                        user = line1;
                        i++;
                    }
                    if(line.startsWith("kinis=")){
                        String line1 = line.substring(6, line.length());
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
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    }

    private void setUserAmount(String user, int kinis) {
            removeUser(user);
            addKinis(user, kinis-1);
    }

}
