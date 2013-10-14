/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class SqliteTools {

    private static Logger logger = Logger.getLogger(SqliteTools.class);

    public static Connection connect() {
        // 加载驱动
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动未找到!");
        }
        // 得到连接 会在你所填写的目录建一个你命名的文件数据库
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/usr/iie/transclient/db/agent.db", null, null);
        } catch (SQLException ex) {
            logger.debug("sqlite connect err!",ex);
            conn = null;
        }
        return conn;
    }

    public static boolean remove(Connection conn, String key) {
        if (conn == null) {
            logger.debug("sqlite remove conn err!");
            return false;
        }
        try {
            // 设置自动提交为false
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();

            //判断表是否存在
            ResultSet rsTables = conn.getMetaData().getTables(null, null, "STATUS_LOG", null);
            if (rsTables.next()) {
                ;
            } else {
                logger.debug("table don't exist!return!");
                return false;
            }
            stmt.executeUpdate("delete from STATUS_LOG where tradeid ='" + key + "';");
            // 提交
            conn.commit();
            return true;
        } catch (SQLException e) {
            logger.debug("remove STATUS_LOG err!key = " + key + " | " + e);
            return false;
        }
    }

    public static Map getData(Connection conn) {
        Map map = new HashMap();
        if (conn == null) {
            logger.debug("sqlite getdata conn err!");
            return map;
        }
        try {
            // 得到结果集
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from STATUS_LOG;");
            while (rs.next()) {
                map.put(rs.getString("tradeid"), rs.getString("content"));
            }
            rs.close();
        } catch (SQLException e) {
            logger.debug("getData err!" + e,e);
            createTable(conn);
        }
        return map;
    }

    public static void createTable(Connection conn) {
        try {
            // 设置自动提交为false
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();

            //判断表是否存在
            ResultSet rsTables = conn.getMetaData().getTables(null, null, "STATUS_LOG", null);
            if (rsTables.next()) {
                logger.debug("STATUS_LOG exists!");
            } else {
                logger.debug("STATUS_LOG don't exists!");
                stmt.executeUpdate("create table STATUS_LOG (tradeid integer primary key autoincrement,content);");
            }
            conn.commit();
        } catch (Exception ex) {
            logger.debug("createTable err!" + ex,ex);
        }
    }

    public static boolean save(Connection conn, String content) {

        if (conn == null || content == null || content.equals("")) {
            logger.debug("sqlite save parameter err!");
            return false;
        }
        try {
            // 设置自动提交为false
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();

            //判断表是否存在
            ResultSet rsTables = conn.getMetaData().getTables(null, null, "STATUS_LOG", null);
            if (rsTables.next()) {
                logger.debug("STATUS_LOG exists!");
            } else {
                logger.debug("STATUS_LOG don't exists!");
                stmt.executeUpdate("create table STATUS_LOG (tradeid integer primary key autoincrement,content);");
            }

            stmt.executeUpdate("insert into STATUS_LOG(content) values ('" + content + "');");
            // 提交
            conn.commit();
            // 得到结果集
            /*ResultSet rs = stmt.executeQuery("select * from STATUS_LOG;");
             while (rs.next()) {
             System.out.println("tradeid = " + rs.getString("tradeid"));
             System.out.println("content = " + rs.getString("content"));
             }
             rs.close();*/
            //conn.close();
            return true;
        } catch (SQLException e) {
            logger.debug("save to STATUS_LOG err!" + e);
            return false;
        }

    }
}
