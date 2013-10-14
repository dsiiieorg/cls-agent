package cn.ac.iie.cls.agent.tools;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.log4j.Logger;

public class DBUtil {
    private static Logger logger = Logger.getLogger(DBUtil.class);
    public static Connection getConnection(String url,int port,String username,String password,String instance) throws SQLException {
        url = "jdbc:oracle:thin:@"+url+":"+port+":"+instance;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException ex) {
            logger.debug(url+" get conn err!");
            return null;
        }
        return DriverManager.getConnection(url, username, password);
    }

    public static void close(ResultSet rs, PreparedStatement ps, Connection conn) throws SQLException {
        if (rs != null) {
            rs.close();
        }

        if (ps != null) {
            ps.close();
        }

        if (conn != null) {
            conn.close();
        }
    }

    public static void close(PreparedStatement ps, Connection conn) throws SQLException {
        if (ps != null) {
            ps.close();
        }

        if (conn != null) {
            conn.close();
        }
    }
}
