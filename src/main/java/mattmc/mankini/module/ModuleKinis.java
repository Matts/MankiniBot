package mattmc.mankini.module;

import com.google.common.collect.ImmutableSortedSet;
import mattmc.mankini.MankiniBot;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.ModUtils;
import mattmc.mankini.utils.SQLiteListener;
import mattmc.mankini.utils.StreamingUtils;
import mattmc.mankini.utils.ViewerUtils;
import org.apache.commons.io.FileUtils;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Project Mankini
 * Created by MattsMc on 7/10/14.
 */
public class ModuleKinis extends SQLiteListener {
    String db = "database\\kinis.db";
 boolean isLocked=false;
    public Thread kinis = new Thread(){
        public void run(){
            while(true){

                    try {
                        kinis.sleep(300000);
                        autoTickAddKikis();
                    } catch (Exception e){
                        e.printStackTrace();
                    }


            }
        }
    };

    public ModuleKinis(){
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
        event.getChannel().send().message(user1 + "  --  " + user2 + "  --  " + user3);

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

    private boolean userExists(String user) throws SQLException {
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
    public void onJoin(JoinEvent<PircBotX> event) throws Exception {
        if(!userExists(event.getUser().getNick().toLowerCase())){
            addUser(event.getUser().getNick().toLowerCase());
        }
    }
public static boolean confirm=false;
    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        String msg = event.getMessage().split(" ")[0];
        if(msg.equalsIgnoreCase("!kinirank")){
            getTop3(event);
        }

        if(msg.equalsIgnoreCase("!kinis")){
            if(!isLocked){
                if(userExists(event.getUser().getNick())){
                    event.getChannel().send().message(event.getUser().getNick() + Strings.has + getKinis(event.getUser().getNick()) + Strings.totalKinis);
                }else{
                    addUser(event.getUser().getNick());
                    event.getChannel().send().message(event.getUser().getNick() + Strings.has + getKinis(event.getUser().getNick()) + Strings.totalKinis);
                }
            }else{
                event.respond("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!");
            }
        }

        if(msg.equalsIgnoreCase("!getkinis")){
            if(!isLocked){
            if(event.getMessage().split(" ").length >= 2){
                if(userExists(event.getMessage().split(" ")[1])){
                    event.respond(event.getMessage().split(" ")[1] + Strings.has + getKinis(event.getMessage().split(" ")[1]) + Strings.totalKinis);
                }else{
                    addUser(event.getMessage().split(" ")[1]);
                    event.respond(event.getMessage().split(" ")[1] + Strings.has + getKinis(event.getMessage().split(" ")[1]) + Strings.totalKinis);
                }
            }else{
                event.respond(Strings.getKinisExplain);
            }
            }else{
                event.respond("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!");
            }
        }
        if(msg.equalsIgnoreCase("!addkinis")){
            if(!isLocked){
            if(ModUtils.moderators.contains(event.getUser().getNick()) ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(event.getMessage().split(" ").length>=3){
                    if(userExists(event.getMessage().split(" ")[1])){
                        addKinis(event.getMessage().split(" ")[1], Integer.parseInt(event.getMessage().split(" ")[2]));
                        event.respond(event.getMessage().split(" ")[2] + Strings.haveBeenAdded + event.getMessage().split(" ")[1]);
                    }else{
                        addUser(event.getMessage().split(" ")[1]);
                        addKinis(event.getMessage().split(" ")[1], Integer.parseInt(event.getMessage().split(" ")[2]));
                        event.respond(event.getMessage().split(" ")[2] + Strings.haveBeenAdded + event.getMessage().split(" ")[1]);
                    }
                }else{
                    event.respond(Strings.addKiniExplain);
                }
            }else{
                event.respond(Strings.NoPerms);
            }
            }else{
                event.respond("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!");
            }
        }
        if(msg.equalsIgnoreCase("!removekinis")){
            if(!isLocked){
            if(ModUtils.moderators.contains(event.getUser().getNick()) || event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(event.getMessage().split(" ").length>=3){
                    removeKinis(event.getMessage().split(" ")[1].toLowerCase(), Integer.parseInt(event.getMessage().split(" ")[2]));
                    event.respond(event.getMessage().split(" ")[2] + Strings.haveBeenRemoved + event.getMessage().split(" ")[1].toLowerCase());
                }else{
                    event.respond(Strings.removeKinisExplain);
                }
            }else{
                event.respond(Strings.NoPerms);
            }
            }else{
                event.respond("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!");
            }
        }
        if(msg.equalsIgnoreCase("!kiniall")){
            if(!isLocked){
            if(ModUtils.moderators.contains(event.getUser().getNick()) ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(event.getMessage().split(" ").length>=2){
                    allKini(Integer.parseInt(event.getMessage().split(" ")[1]));
                    event.respond(Strings.everyoneGot + event.getMessage().split(" ")[1] + Strings.kinis);
                }else{
                    event.respond(Strings.kiniAllExplain);
                }
            }else{
                event.respond(Strings.NoPerms);
            }
            }else{
                event.respond("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!");
            }
        }
        if(msg.equalsIgnoreCase("!adduser")){
            if(!isLocked){
            if(ModUtils.moderators.contains(event.getUser().getNick()) ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(event.getMessage().split(" ").length>=2){
                    if(!userExists(event.getMessage().split(" ")[1])){
                    addUser(event.getMessage().split(" ")[1]);
                    event.respond(event.getMessage().split(" ")[1] + Strings.haveBeenAdded);
                    }else{
                        event.respond("User Already Exists!");
                    }
                }else{
                    event.respond(Strings.addUserExplain);
                }
            }else{
                event.respond(Strings.NoPerms);
            }
            }else{
                event.respond("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!");
            }
        }
        if(msg.equalsIgnoreCase("!removeuser")){
            if(!isLocked){
            if(ModUtils.moderators.contains(event.getUser().getNick()) ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(event.getMessage().split(" ").length>=2){
                    if(userExists(event.getMessage().split(" ")[1])){
                    removeUser(event.getMessage().split(" ")[1]);
                    event.respond(event.getMessage().split(" ")[1] + Strings.haveBeenRemoved);
                    }else{
                        event.respond("User Doesn't Exist!");
                    }
                }else{
                    event.respond(Strings.removeUserExplain);
                }
            }else{
                event.respond(Strings.NoPerms);
            }
            }else{
                event.respond("A High Payload Is Getting Sent To The DB ATM, Please Wait Till Thats Complete!");
            }
        }

        if(msg.equalsIgnoreCase("!importkinisfromfile")){
            isLocked=true;
            if(event.getUser().getNick().equalsIgnoreCase("runew0lf") ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                event.respond("Kini Importing started.. All Kini Systems Locked!");
                File dbfile = new File("database\\kinis.db");
                if((boolean) MankiniBot.conf.get("useSQLite")){
                    dbfile.delete();
                    dbfile.createNewFile();
                }
                setupDB();
                File file = new File(event.getMessage().split(" ")[1]);
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
            event.getChannel().send().message("The Writing Has Been Completed, All Systems Unlocked And Running!");

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
