package mattmc.mankini.utils;

import mattmc.mankini.MankiniBot;
import mattmc.mankini.commands.CommandBase;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Project MankiniBot
 * Created by MattMc on 6/1/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public abstract class SQLiteListener extends CommandBase {
    public Connection c = null;

    public SQLiteListener(){
        setupDB();
    }

    public abstract void setupDB();

    public void openConnection(String db){
        if((boolean) MankiniBot.conf.get("useSQLite")){
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" +db);
        }catch (Exception e){
            e.printStackTrace();
        }
        }else{
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String sql = "jdbc:mysql://"+MankiniBot.conf.get("MySQLHost") + "/" + MankiniBot.conf.get("MySQLTable")+ "?user="+MankiniBot.conf.get("MySQLUserName")+"&password="+MankiniBot.conf.get("MySQLPassword");
                c = DriverManager.getConnection(sql);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public boolean existsInDatabase(String SQLiteDB, String tableToLookIn, Object thingToBeChecked) throws SQLException {
        openConnection(SQLiteDB);
        String sql = "SELECT * FROM `"+tableToLookIn+"` WHERE `USER`=?";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        preparedStatement = c.prepareStatement(sql);
        preparedStatement.setObject(1, thingToBeChecked);
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next())
            return false;
        resultSet.close();
        preparedStatement.close();
        closeConnection();
        return true;
    }

    public void updateCache(String db, String table, HashMap list, String key, String value) throws SQLException {
        openConnection(db);
        try{
            ResultSet result;
            String sql = "SELECT * FROM `"+table+"`";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            result = preparedStatement.executeQuery();
            while(result.next()){
                list.put(result.getString(key), result.getString(value));
            }
            closeConnection();
            result.close();
            preparedStatement.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}
