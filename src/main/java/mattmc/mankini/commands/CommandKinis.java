package mattmc.mankini.commands;

import mattmc.mankini.common.StreamingCommon;
import mattmc.mankini.common.ViewerCommon;
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
    static boolean bool = false;
    public static boolean isActive;

    public static Thread kinis = new Thread(){
        public void run(){
            while(true){
                bool=false;
                while(StreamingCommon.isStreaming){
                    System.out.println("Auto Kini's Started");
                    try {
                        sleep(300000);
                        CommandKinis.class.newInstance().autoTickAddKikis();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public CommandKinis(){
        setupDB();
        try {
            ViewerCommon.updateViewers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void autoTickAddKikis() {
        System.out.println("5 Min Kini :D");
        try {
            ViewerCommon.updateViewers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0; i< ViewerCommon.viewers.size();i++){
                addKinis(ViewerCommon.viewers.get(i), 1);
        }

    }

    @Override
    public void setupDB() {
        Statement stmt;
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
        try {
            if(existsInDatabase(db, "KINIS", user.toLowerCase())){
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public void removeKinis(String user, int amount) {
        try {
            if(existsInDatabase(db, "KINIS", user.toLowerCase())){
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addKinis(String user, int amount) {
        try {
            if(existsInDatabase(db, "KINIS", user.toLowerCase())){
                try{
            int oldAmount = getKinis(user.toLowerCase());
            int newAmount = oldAmount+amount;
            openConnection(db);
            String sql = "UPDATE `KINIS` SET `AMOUNT`=? WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setInt(1, newAmount);
            statement.setString(2, user.toLowerCase());
            statement.executeUpdate();
            statement.close();
            closeConnection();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                addUser(user.toLowerCase());
                addKinis(user.toLowerCase(), amount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        PreparedStatement statement;
        try {
            statement = c.prepareStatement(sql);

        statement.setInt(1, amount);
        statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) throws IllegalAccessException, SQLException, InstantiationException {
        super.channelCommand(event);
            if(args.length<=1){
                if(!isLocked){
                    try {
                        if(existsInDatabase(db, "KINIS", user.toLowerCase())){
                                MessageSending.sendMessageWithPrefix(user + " has " +getKinis(user) + " total Kinis!", user, event);
                        }else{
                            addUser(user);
                            MessageSending.sendMessageWithPrefix(user + " has " + getKinis(user) + " total Kinis!", user, event);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else{
                    MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
                }
            }else{

        if(args[1].equalsIgnoreCase("rank")){
            getTop3(event);
        }
        if(args[1].equalsIgnoreCase("wherethefuckami")){
            openConnection(db);
            String sql = "SELECT * FROM `KINIS` ORDER BY AMOUNT DESC";
            try {
                PreparedStatement statement = c.prepareStatement(sql);
                statement.executeQuery();
                MessageSending.sendMessageWithPrefix(user + " This Command Is Not Working, Come Back Later ;)", user, event);
            closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(args[1].equalsIgnoreCase("get")){
            if(!isLocked) {
                if (Permissions.isModerator(user, event)) {
                    if (args.length >= 2) {
                        try {
                            if (existsInDatabase(db, "KINIS", args[2].toLowerCase())) {
                                MessageSending.sendMessageWithPrefix(args[2] + " has " + getKinis(args[2]) + " total Kinis!", args[2], event);
                            } else {
                                addUser(args[1]);
                                MessageSending.sendMessageWithPrefix(args[2] + " has " + getKinis(args[2]) + " total Kinis!", args[2], event);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis get <UserName>", event);
                    }
                } else {
                    MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
                }
            }
        }
        if(args[1].equalsIgnoreCase("give")){
            if(!isLocked){
                if(Permissions.isOwner(getNick(event), event)){
                    if(args.length>=3){
                        try {
                            if(existsInDatabase(db, "KINIS", args[2].toLowerCase())){
                                addKinis(args[2], Integer.parseInt(args[3]));
                                MessageSending.sendMessageWithPrefix(args[3] + " Kini's have been added to " + args[2], args[2],event);
                            }else{
                                addUser(args[2]);
                                addKinis(args[2], Integer.parseInt(args[3]));
                                MessageSending.sendMessageWithPrefix(args[3] + " Kinis's have been added to " + args[2], args[2],event);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }else{
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis give <UserName> <Amount>", event);
                    }
                }
            }else{
                MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }
        if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("rem") || args[1].equalsIgnoreCase("del")){
            if(!isLocked){
                if(Permissions.isOwner(getNick(event), event)){
                    if(args.length>=3){
                        removeKinis(args[2].toLowerCase(), Integer.parseInt(args[3]));
                        MessageSending.sendMessageWithPrefix(args[3] + " Kinis's have been removed from " + args[2].toLowerCase(),args[2], event);
                    }else{
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis remove <UserName> <Amount>", event);
                    }
                }
            }else{
                MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }
        if(args[1].equalsIgnoreCase("giveall")){
            if(!isLocked){
                if(Permissions.isOwner(getNick(event), event)){
                    if(args.length>=2){
                        allKini(Integer.parseInt(args[2]));
                        MessageSending.sendNormalMessage("Everyone got " + args[2] + " Kinis!!", event);
                    }else{
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis giveall <Amount>", event);
                    }
                }
            }else{
                MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }
        if(args[1].equalsIgnoreCase("adduser")){
            if(!isLocked){
                if(Permissions.isOwner(getNick(event), event)){
                    if(args.length>=2){
                        try {
                            if(!existsInDatabase(db, "KINIS", args[2].toLowerCase())){
                                addUser(args[2]);
                                MessageSending.sendNormalMessage(args[2] + " has been added ", event);
                            }else{
                                MessageSending.sendNormalMessage("User Already Exists!", event);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }else{
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis adduser <UserName>", event);
                    }
                }
            }else{
                MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }
        if(args[1].equalsIgnoreCase("removeuser")){
            if(!isLocked){
                if(Permissions.isOwner(getNick(event), event)){
                    if(args.length>=2){
                        try {
                            if(existsInDatabase(db, "KINIS", args[2].toLowerCase())){

                                removeUser(args[2]);
                                MessageSending.sendNormalMessage(args[2] + " has been removed ", event);
                            }else{
                                MessageSending.sendNormalMessage("User Doesn't Exist!", event);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }else{
                        MessageSending.sendNormalMessage("Correct Syntax: !kinis remove <UserName>", event);
                    }
                }
            }else{
               MessageSending.sendNormalMessage("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!", event);
            }
        }

        if(args[1].equalsIgnoreCase("export")){
            try{
                if(Permissions.isOwner(getNick(event),event)){
                isLocked=true;
                MessageSending.sendNormalMessage("Kini Exporting started.. All Kini Systems Locked!", event);
                openConnection(db);
                String sql = "SELECT * FROM `KINIS`";
                PreparedStatement statement = c.prepareStatement(sql);
                ResultSet set = statement.executeQuery();
                File file1 = new File(args[2]);
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
                if(Permissions.isOwner(getNick(event),event)){
                isLocked=true;
                MessageSending.sendNormalMessage("Kini Importing started.. All Kini Systems Locked!", event);
                setupDB();
                File file = new File(args[2]);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;

                String user = null;
                String kinis = null;
                while ((line = reader.readLine()) != null){
                    if(line.startsWith("[#runew0lf.")){
                        user = line.substring(11, line.length()-1);
                    }
                    if(line.startsWith("kinis=")){
                        kinis = line.substring(6, line.length());
                    }
                    if(line.isEmpty()){
                        if(!(Integer.parseInt(kinis) <= 5)){
                            setUserAmount(user, Integer.parseInt(kinis));
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

    private String getNick(MessageEvent<PircBotX> event) {
        return event.getUser().getNick();
    }

    private void setUserAmount(String user, int kinis) {
            removeUser(user);
            addKinis(user, kinis-1);
    }

}
