package mattmc.mankini.commands;

import mattmc.mankini.utils.MessageSending;
import mattmc.mankini.utils.Permissions;
import mattmc.mankini.utils.SQLiteListener;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Project Mankini
 * Created by MattsMc on 8/15/14.
 */

public class CommandBuy extends SQLiteListener {
    String db = "database\\ranks.db";
    private static HashMap<String, Ranks> userCache = new HashMap<String, Ranks>();

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);
        if(args.length<=1){
            try {
                if(CommandBuy.getUserCache().get(user.toLowerCase())!=null){
                    MessageSending.sendMessageWithPrefix(user + " is " + getUserRank(user).desc, user, event);
                }else{
                    MessageSending.sendMessageWithPrefix(user + " is a CheapAss!", user, event);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(args.length>=2){
            if(args[1].equalsIgnoreCase("list")){
                for(Ranks rank: Ranks.values()){
                    MessageSending.sendNormalMessage(rank.desc + " costs " + rank.amount, event);
                }
            }
            try {
                if(args[1].equalsIgnoreCase("buy")){
                    if(args[2].equalsIgnoreCase("vip")){
                        buyRank(user.toLowerCase(), Ranks.VIP, event);
                    }
                    if(args[2].equalsIgnoreCase("kinilurker")){
                        buyRank(user.toLowerCase(), Ranks.KiniLurker, event);
                    }
                }
                if(args[1].equalsIgnoreCase("remove")){
                    if(Permissions.getPermission(user, Permissions.Perms.MOD).equals(Permissions.Perms.MOD)){
                        removeUserRank(args[2].toLowerCase());
                    }
                }
                if(args[1].equalsIgnoreCase("get")){
                    if(Permissions.getPermission(user, Permissions.Perms.REG).equals(Permissions.Perms.REG)){
                        if(CommandBuy.getUserCache().get(user.toLowerCase())!=null){
                            MessageSending.sendMessageWithPrefix(args[2], args[2], event);
                        }else{
                            MessageSending.sendMessageWithPrefix(user, user, event);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public CommandBuy(){
        setupDB();
        try {
            getUserRanks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupDB() {
        Statement stmt = null;
        try {
            openConnection(db);
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `RANKS`(USER CHAR(50), RANK CHAR(50));";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            closeConnection();
        }
    }

    private void buyRank(String user, Ranks rank, MessageEvent event) throws SQLException {
        try {
            int kinis = CommandKinis.class.newInstance().getKinis(user);
            if(kinis >= rank.amount){
            CommandKinis.class.newInstance().removeKinis(user, rank.amount);
            addUserRank(user, rank);
                MessageSending.sendMessageWithPrefix(user + " "+ rank.desc + " Successfully Bought!", user, event);
            }else{
                MessageSending.sendMessageWithPrefix(user + " You Do Not Have Enough Kini's!", user, event);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void getUserRanks() throws SQLException {
        openConnection(db);
        userCache.clear();
        String sql = "SELECT * FROM `RANKS`";
        PreparedStatement statement = c.prepareStatement(sql);
        ResultSet set = statement.executeQuery();
        while(set.next()){
            System.out.println(set.getString("USER") + " " + set.getString("RANK"));
            userCache.put(set.getString("USER"), Ranks.valueOf(set.getString("RANK")));
        }
        statement.close();
        set.close();
        closeConnection();
    }

    private void addUserRank(String user, Ranks rank) throws SQLException {
        if(userExistsInDB(user)){
           removeUserRank(user);
            openConnection(db);
            String sql = "INSERT INTO `RANKS`(USER, RANK) VALUES(?,?)";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.setObject(2, rank.name());
            statement.executeUpdate();
            statement.close();
            closeConnection();
        }else{
            openConnection(db);
            String sql = "INSERT INTO `RANKS`(USER, RANK) VALUES(?,?)";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.setObject(2, rank.name());
            statement.executeUpdate();
            statement.close();
            closeConnection();
        }
        userCache.put(user, rank);
    }

    public boolean userExistsInDB(String user) throws SQLException {
        openConnection(db);
        String sql = "SELECT * FROM `RANKS` WHERE `USER`=?";
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

    private void removeUserRank(String user) throws SQLException {
        if(userExistsInDB(user)){
            openConnection(db);
            String sql = "DELETE FROM `RANKS` WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.executeUpdate();
            statement.close();
            closeConnection();
        }
        userCache.remove(user);
    }

    private Ranks getUserRank(String user) throws SQLException {
        openConnection(db);
        Ranks rank = null;
        ResultSet result;
        String sql = "SELECT * FROM `RANKS` WHERE `USER`=?";
        PreparedStatement preparedStatement = c.prepareStatement(sql);
        preparedStatement.setString(1, user.toLowerCase());
        result = preparedStatement.executeQuery();
        if(result.next()){
            rank =  Ranks.valueOf(result.getString("RANK"));
            userCache.put(result.getString("USER"), rank);
        }
        closeConnection();
        result.close();
        preparedStatement.close();
        return rank;
    }

    public enum Ranks {
        Developer(100000, "Dev"),
        KiniLurker(100, "KiniLurker"),
        VIP(2000, "VIP");

        private final Integer amount;
        private final String desc;

        Ranks(Integer kinis, String description) {
            amount = kinis;
            desc = description;
        }

        public Integer getAmount(){
            return amount;
        }

        public String getDesc(){
            return desc;
        }
    }

    public static HashMap<String, Ranks> getUserCache(){
        return userCache;
    }
}
