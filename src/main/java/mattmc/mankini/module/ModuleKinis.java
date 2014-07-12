package mattmc.mankini.module;

import com.google.common.collect.ImmutableSortedSet;
import mattmc.mankini.MankiniBot;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.ModUtils;
import mattmc.mankini.utils.SQLiteListener;
import org.apache.commons.io.FileUtils;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.List;

/**
 * Project Mankini
 * Created by MattsMc on 7/10/14.
 */
public class ModuleKinis extends SQLiteListener {
    String db = "database\\kinis.db";
    private boolean isKiniOn = true;

    MessageEvent<PircBotX> events;

    public ModuleKinis(){
        setupDB();
    }

    Thread kinis = new Thread(){
        public void run(){
            while(isKiniOn){
                try {
                    Thread.sleep(300000);
                    autoTickAddKikis(events);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void autoTickAddKikis(MessageEvent<PircBotX> event) {
        ImmutableSortedSet set = event.getChannel().getUsers();
            List<User> list = set.asList();

            int i = 0;
            while(set.iterator().hasNext()){
                try {
                    addKinis(list.get(i).getNick(), 1);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException ex){

                }finally {
                    i++;
                }
            }
        }

    @Override
    public void setupDB() {
        Statement stmt = null;
        try {
            openConnection(db);
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `KINIS`(USER CHAR(50), AMOUNT CHAR(50));";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }

    public void addUser(String user) throws SQLException {
        openConnection(db);
        String sql = "INSERT INTO `KINIS`(USER, AMOUNT) VALUES(?,?)";
        PreparedStatement statement = c.prepareStatement(sql);
        statement.setString(1, user.toLowerCase());
        statement.setString(2, "1");
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
            int oldAmount = Integer.parseInt(getKinis(user.toLowerCase()));
            int newAmount = oldAmount-amount;
            openConnection(db);
            String sql = "UPDATE `KINIS` SET `AMOUNT`=? WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
        statement.setString(1, newAmount+"");
            statement.setString(2, user.toLowerCase());
            statement.executeUpdate();
            closeConnection();
         }
    }

    public void addKinis(String user, int amount) throws SQLException {
        if(userExists(user)){
        int oldAmount = Integer.parseInt(getKinis(user.toLowerCase()));
        int newAmount = oldAmount+amount;
        openConnection(db);
        String sql = "UPDATE `KINIS` SET `AMOUNT`=? WHERE `USER`=?";
        PreparedStatement statement = c.prepareStatement(sql);
        statement.setString(1, newAmount+"");
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

    public String getKinis(String user) throws SQLException {
        String resulty = "-1";
            openConnection(db);
            ResultSet result;
            String sql = "SELECT * FROM `KINIS` WHERE `USER`=?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, user.toLowerCase());
            result = preparedStatement.executeQuery();
           resulty = result.getString("AMOUNT");
           closeConnection();
           result.close();
           preparedStatement.close();
            return resulty;
    }

    private void allKini(int amount) throws SQLException {
        openConnection(db);
        String sql = "UPDATE `KINIS` SET `AMOUNT`=AMOUNT+?";
        PreparedStatement statement = c.prepareStatement(sql);
        statement.setString(1, amount+"");
        statement.executeUpdate();
        closeConnection();
    }

    @Override
    public void onJoin(JoinEvent<PircBotX> event) throws Exception {
        if(!userExists(event.getUser().getNick().toLowerCase())){
            addUser(event.getUser().getNick().toLowerCase());
        }
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        String msg = event.getMessage().split(" ")[0];
        this.events = event;

        if(kinis.getState().equals(Thread.State.NEW)){
            event.getChannel().send().message(Strings.autoKinisStarted);
            kinis.start();
        }

        if(msg.equalsIgnoreCase("!kinis")){
            if(userExists(event.getUser().getNick())){
            event.getChannel().send().message(event.getUser().getNick() + Strings.has + getKinis(event.getUser().getNick()) + Strings.totalKinis);
            }else{
                addUser(event.getUser().getNick());
                event.getChannel().send().message(event.getUser().getNick() + Strings.has + getKinis(event.getUser().getNick()) + Strings.totalKinis);
            }

        }
        if(msg.equalsIgnoreCase("!getkinis")){
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
        }
        if(msg.equalsIgnoreCase("!addkinis")){
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
        }
        if(msg.equalsIgnoreCase("!removekinis")){
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
        }
        if(msg.equalsIgnoreCase("!kiniall")){
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
        }
        if(msg.equalsIgnoreCase("!adduser")){
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
        }
        if(msg.equalsIgnoreCase("!removeuser")){
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
        }

        if(msg.equalsIgnoreCase("!importkinisfromfile")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                event.respond("Kini Importing started.. Expect some bot lag!");
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
                        if(!(Integer.parseInt(kinis) <= 30)){
                            setUserAmount(user, Integer.parseInt(kinis));
                            i++;
                            continue;
                        }
                    }
                }
            }
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
