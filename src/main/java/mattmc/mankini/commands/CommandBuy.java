package mattmc.mankini.commands;

import mattmc.mankini.utils.SQLiteListener;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
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
    private static HashMap<Ranks, Integer> ranks = new HashMap<Ranks, Integer>();
    private static HashMap<String, Ranks> userCache = new HashMap<String, Ranks>();

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) {
        super.channelCommand(event);

    }

    public CommandBuy(){
        setupDB();
        ranks.clear();
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

    private void getUserRanks() throws SQLException {
        openConnection(db);
        userCache.clear();
        String sql = "SELECT * FROM `RANKS`";
        PreparedStatement statement = c.prepareStatement(sql);
        ResultSet set = statement.executeQuery();
        while(set.next()){
            userCache.put(set.getString("USER"), (Ranks) set.getObject("RANK"));
        }
        statement.close();
        set.close();
        closeConnection();
    }

    private void addUserRank(String user, Ranks rank) throws SQLException {
        if(userExistsInDB(user)){
           removeUserRank(user, rank);
           addUserRank(user, rank);
        }else{
            openConnection(db);
            String sql = "INSERT INTO `RANKS`(USER, RANK) VALUES(?,?)";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.setObject(2, rank);
            statement.executeUpdate();
            statement.close();
            closeConnection();
        }
        userCache.put(user, rank);
    }

    private boolean userExistsInDB(String user) throws SQLException {
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

    private void removeUserRank(String user, Ranks rank) throws SQLException {
        if(userExistsInDB(user)){
            openConnection(db);
            String sql = "DELETE FROM `KINIS` WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.executeUpdate();
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
            rank = (Ranks) result.getObject("RANK");
        }
        closeConnection();
        result.close();
        preparedStatement.close();
        return rank;
    }

    public enum Ranks {

    }

    public static HashMap<String, Ranks> getUserCache(){
        return userCache;
    }

    public static HashMap getRanks(){
        return ranks;
    }
}
