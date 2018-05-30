package ru.naztrans.Filecloud.fileserver;

import java.sql.*;
import java.util.Properties;


public class SQLService {
    private static Connection conn;
    private static Statement stat;
    private static Properties prop;
    public static void init () throws ClassNotFoundException, SQLException {
        prop=new Properties();
        prop.setProperty("useSSL", "false"); //без этого не апускается
        prop.setProperty("serverTimezone", "Europe/Moscow"); //и без этого тоже
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn =DriverManager.getConnection("jdbc:mysql://fcuser:fcpass@localhost:3306/filecloud", prop);
        stat=conn.createStatement();

    }
    public static boolean addUser (String  name, String pass) {
        try {
            return stat.executeUpdate(String.format("INSERT into users values ('%s', '%s')", name, pass))>0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    static boolean userExist(String name){
        try {
            ResultSet rs = stat.executeQuery(String.format(
                    "SELECT name FROM users WHERE name='%s';", name
            ));
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static String getUser (String name, String pass) {
        try {
            ResultSet rs = stat.executeQuery(String.format(
                    "SELECT name FROM users WHERE name='%s' and pass='%s';", name, pass
            ));
            if (rs.next()){
                return  rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static  void quit(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
