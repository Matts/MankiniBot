package mattmc.mankini.commands;

import com.google.common.collect.ImmutableSortedSet;
import mattmc.mankini.MankiniBot;
import mattmc.mankini.libs.Strings;
import mattmc.mankini.utils.ModUtils;
import mattmc.mankini.utils.SQLiteListener;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.*;
import java.util.List;

/**
 * Project Mankini
 * Created by MattsMc on 7/10/14.
 */
public class CommandKinis extends SQLiteListener {
    String db = "database\\kinis.db";
    private boolean isKiniOn = true;

    MessageEvent<PircBotX> events;

    public CommandKinis(){
        setupDB();
    }

    Thread kinis = new Thread(){
        public void run(){
            while(isKiniOn){
                try {
                    autoTickAddKikis(events);
                    Thread.sleep(300000);

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
        statement.setString(1, user);
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
            statement.setString(1, user);
            statement.executeUpdate();
        }
        closeConnection();
    }

    public void removeKinis(String user, int amount) throws SQLException {
        if(userExists(user)){
            int oldAmount = Integer.parseInt(getKinis(user));
            int newAmount = oldAmount-amount;
            openConnection(db);
            String sql = "UPDATE `KINIS` SET `AMOUNT`=? WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
        statement.setString(1, newAmount+"");
            statement.setString(2, user);
            statement.executeUpdate();
            closeConnection();
         }
    }

    public void addKinis(String user, int amount) throws SQLException {
        if(userExists(user)){
        int oldAmount = Integer.parseInt(getKinis(user));
        int newAmount = oldAmount+amount;
        openConnection(db);
        String sql = "UPDATE `KINIS` SET `AMOUNT`=? WHERE `USER`=?";
        PreparedStatement statement = c.prepareStatement(sql);
        statement.setString(1, newAmount+"");
        statement.setString(2, user);
        statement.executeUpdate();
        closeConnection();
        }
    }

    private boolean userExists(String user) throws SQLException {
        openConnection(db);
        String sql = "SELECT * FROM `KINIS` WHERE `USER`=?";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        preparedStatement = c.prepareStatement(sql);
        preparedStatement.setString(1, user);
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
            preparedStatement.setString(1, user);
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
        if(!userExists(event.getUser().getNick())){
        addUser(event.getUser().getNick());
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
            event.respond(event.getUser().getNick() + Strings.has + getKinis(event.getUser().getNick()) + Strings.totalKinis);
        }
        if(msg.equalsIgnoreCase("!getkinis")){
            if(event.getMessage().split(" ").length >= 2){
                event.respond(event.getMessage().split(" ")[1] + Strings.has + getKinis(event.getMessage().split(" ")[1]) + Strings.totalKinis);
            }else{
                event.respond(Strings.getKinisExplain);
            }
        }
        if(msg.equalsIgnoreCase("!addkinis")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(event.getMessage().split(" ").length>=3){
                    addKinis(event.getMessage().split(" ")[1], Integer.parseInt(event.getMessage().split(" ")[2]));
                    event.respond(event.getMessage().split(" ")[2] + Strings.haveBeenAdded + event.getMessage().split(" ")[1]);
                }else{
                    event.respond(Strings.addKiniExplain);
                }
            }else{
                event.respond(Colors.RED + Strings.NoPerms);
            }
        }
        if(msg.equalsIgnoreCase("!removekinis")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) || event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(event.getMessage().split(" ").length>=3){
                    removeKinis(event.getMessage().split(" ")[1], Integer.parseInt(event.getMessage().split(" ")[2]));
                    event.respond(event.getMessage().split(" ")[2] + Strings.haveBeenRemoved + event.getMessage().split(" ")[1]);
                }else{
                    event.respond(Strings.removeKinisExplain);
                }
            }else{
                event.respond(Colors.RED + Strings.NoPerms);
            }
        }
        if(msg.equalsIgnoreCase("!kiniall")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(event.getMessage().split(" ").length>=2){
                    allKini(Integer.parseInt(event.getMessage().split(" ")[1]));
                    event.respond(Strings.everyoneGot + event.getMessage().split(" ")[2] + Strings.kinis);
                }else{
                    event.respond(Strings.kiniAllExplain);
                }
            }else{
                event.respond(Colors.RED + Strings.NoPerms);
            }
        }
        if(msg.equalsIgnoreCase("!adduser")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(event.getMessage().split(" ").length>=2){
                    addUser(event.getMessage().split(" ")[1]);
                    event.respond(event.getMessage().split(" ")[1] + Strings.haveBeenAdded);
                }else{
                    event.respond(Strings.addUserExplain);
                }
            }else{
                event.respond(Colors.RED + Strings.NoPerms);
            }
        }
        if(msg.equalsIgnoreCase("!removeuser")){
            if(ModUtils.moderators.contains(event.getUser().getNick()) ||  event.getUser().getNick().equalsIgnoreCase(MankiniBot.Owner)){
                if(event.getMessage().split(" ").length>=2){
                removeUser(event.getMessage().split(" ")[1]);
                    event.respond(event.getMessage().split(" ")[1] + Strings.haveBeenRemoved);
                }else{
                    event.respond(Strings.removeUserExplain);
                }
            }else{
                event.respond(Colors.RED + Strings.NoPerms);
            }
        }
    }

}
