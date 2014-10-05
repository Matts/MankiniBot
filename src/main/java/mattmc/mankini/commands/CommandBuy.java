package mattmc.mankini.commands;

import mattmc.mankini.utils.*;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.*;
import java.util.HashMap;

/**
 * Project MankiniBot
 * Created by MattMc on 6/1/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class CommandBuy extends SQLiteListener {
    String db = "database\\ranks.db";

    private final String syntax = "Syntax: !rank <list/buy/remove/add/sell/get> [args]";

    public static boolean isActive;

    private static HashMap<String, Ranks> userCache = new HashMap<String, Ranks>();

    @Override
    public void channelCommand(MessageEvent<PircBotX> event) throws IllegalAccessException, SQLException, InstantiationException {
        super.channelCommand(event);
        if (args.length <= 1) {
            try {
                if (CommandBuy.getUserCache().get(user.toLowerCase()) != null) {
                    MessageSending.sendMessageWithPrefix(user + " is " + getUserRank(user).desc, user, event);
                } else {
                    MessageSending.sendMessageWithPrefix(user + " has no rank.", user, event);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("list")) {
                MessageSending.sendMessageWithPrefix(user + " You can see the full list of ranks here: http://mattmc.info/bots/mankinibot/ranks/", user, event);
            }
            try {
                if (args[1].equalsIgnoreCase("buy")) {
                    buyRank(user, Ranks.valueOf(args[2].toLowerCase()), event);
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    if (Permissions.isModerator(getNick(event), event, true)) {
                        removeUserRank(args[2], event, true);
                    }
                }
                if (args[1].equalsIgnoreCase("add")) {
                    if (Permissions.isModerator(getNick(event), event, true)) {
                        addUserRank(args[2], Ranks.valueOf(args[3]), event, true);
                    }
                }
                if (args[1].equalsIgnoreCase("sell")) {
                    try {
                        CommandKinis.class.newInstance().addKinis(user, getUserRank(user).getAmount() / 2);
                        MessageSending.sendMessageWithPrefix(user + " Your rank has been removed, but we couldn't retrieve all your kinis back, only " + getUserRank(user).getAmount() / 2 + " have been found!", user, event);
                        removeUserRank(user, event, false);
                        getUserCache().remove(user.toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (args[1].equalsIgnoreCase("request")) {
                    MessageSending.sendMessageWithPrefix(user + " please send a email to matt@mattmc.info if you have a request or want to nerf something.", user, event);
                }
                if (args[1].equalsIgnoreCase("get")) {
                    if (Permissions.isRegular(getNick(event), event, true)) {
                        if (CommandBuy.getUserCache().get(user.toLowerCase()) != null) {
                            MessageSending.sendMessageWithPrefix(args[2], args[2], event);
                        } else {
                            MessageSending.sendMessageWithPrefix(args[2] + " has no rank.", user, event);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            MessageSending.sendMessageWithPrefix(syntax, user, event);
        }
    }

    private String getNick(MessageEvent<PircBotX> event) {
        return event.getUser().getNick();
    }

    public CommandBuy() {
        setupDB();
        try {
            getUserRanks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupDB() {
        try {
            openConnection(db);
            Statement stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `RANKS`(USER CHAR(50), RANK CHAR(50));";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void buyRank(String user, Ranks rank, MessageEvent event) throws SQLException {
        try {
            int kinis = CommandKinis.class.newInstance().getKinis(user);
            if (kinis >= rank.getAmount()) {
                CommandKinis.class.newInstance().removeKinis(user, rank.getAmount());
                addUserRank(user.toLowerCase(), rank, event, false);
                MessageSending.sendMessageWithPrefix(user + " Successfully Bought " + rank.desc, user, event);
            } else {
                MessageSending.sendMessageWithPrefix(user + " You Do Not Have Enough Kini's!", user, event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserRanks() throws SQLException {
        openConnection(db);
        userCache.clear();
        String sql = "SELECT * FROM `RANKS`";
        PreparedStatement statement = c.prepareStatement(sql);
        ResultSet set = statement.executeQuery();
        while (set.next()) {
            userCache.put(set.getString("USER").toLowerCase(), Ranks.valueOf(set.getString("RANK").toLowerCase()));
        }
        statement.close();
        set.close();
        closeConnection();
    }

    @Override
    public String getSyntax() {
        return syntax;
    }

    private void addUserRank(String user, Ranks rank, MessageEvent event, boolean bcast) throws SQLException {
        if (existsInDatabase(db, "RANKS", user.toLowerCase())) {
            removeUserRank(user, event, false);
            openConnection(db);
            String sql = "INSERT INTO `RANKS`(USER, RANK) VALUES(?,?)";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.setObject(2, rank.name());
            statement.executeUpdate();
            statement.close();
            closeConnection();
        } else {
            openConnection(db);
            String sql = "INSERT INTO `RANKS`(USER, RANK) VALUES(?,?)";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.setObject(2, rank.name());
            statement.executeUpdate();
            statement.close();
            closeConnection();
        }
        if (bcast) {
            MessageSending.sendMessageWithPrefix(user + " has been added to " + rank.desc, user, event);
        }
        userCache.put(user.toLowerCase(), rank);
    }

    private void removeUserRank(String user, MessageEvent event, boolean bcast) throws SQLException {
        if (existsInDatabase(db, "RANKS", user.toLowerCase())) {
            openConnection(db);
            String sql = "DELETE FROM `RANKS` WHERE `USER`=?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, user.toLowerCase());
            statement.executeUpdate();
            statement.close();
            closeConnection();
        }
        if (bcast) {
            MessageSending.sendMessageWithPrefix(user + " has no rank anymore!", user, event);
        }
        userCache.remove(user);
    }

    private Ranks getUserRank(String user) throws SQLException {
        return userCache.get(user);
    }

    public enum Ranks {
        minionnoob(3000, "MinionN00b"),
        kiniwhore(5000, "Kini Whore"),
        mankiniminion(5000, "Mankini Minion"),
        mankiniassasin(5000, "Kini Assassin"),
        dirtylurker(5000, "Dirty Lurker"),
        kinipimp(6000, "Kini Pimp"),
        masterlurker(9000, "Master Lurker"),
        mankinimaster(10000, "Mankini Master"),
        moderator(1000000, "Moderator"),
        developer(1000000, "Developer"),
        kiniqueenie(5000, "Kini Queenie"),
        mistress(10000, "Mankini Mistress");

        private final Integer amount;
        private final String desc;

        Ranks(Integer kinis, String description) {
            amount = kinis;
            desc = description;
        }

        public Integer getAmount() {
            return amount;
        }

        public String getDesc() {
            return desc;
        }
    }

    public static HashMap<String, Ranks> getUserCache() {
        return userCache;
    }
}
