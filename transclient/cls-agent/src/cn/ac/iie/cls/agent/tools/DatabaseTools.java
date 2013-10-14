/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.subThread.SubFtpThread;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class DatabaseTools {

    private static Logger logger = Logger.getLogger(DatabaseTools.class);
    public static int connTimes = 5;

    public static boolean downFile(String name,String url, int port, String username, String password/*
             * , String remotePath
             */, String localPath, String sql, String instance, String database_type) {
        boolean connFlag = false;
        if (database_type.equals("oracle")) {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            for (int n = 0; n < connTimes; n++) {
                try {
                    conn = DBUtil.getConnection(url, port, username, password, instance);
                    if (conn != null) {
                        connFlag = true;
                        break;
                    }else{
                        connFlag=false;
                        logger.debug(url + " get conn error!");
                    }
                } catch (SQLException ex) {
                    logger.debug(url + " get conn error!catch!reconnect!");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex1) {
                        logger.debug(url + " sleep err!");
                    }
                }
            }

            if (!connFlag) {
                logger.debug(url + " get conn error!"+connTimes+" times!");
                return connFlag;
            } else {
                try {
                    ps = conn.prepareStatement(sql);
                    rs = ps.executeQuery();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mmss");
                    String times = sdf.format(new Date());
                    String path = localPath+"/" + name+times + ".txt";
                    
                   
                    
                    File file = new File(localPath);
                    if(!file.exists()||!file.isDirectory()){
                        file.mkdir();
                    }
                    FileOutputStream fos = new FileOutputStream(path);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int numCols = rsmd.getColumnCount();
                    if (true) {//if add colum name
                        for (int i = 1; i <= numCols; i++) {
                            if (i > 1) {
                                osw.write("|");
                            }
                            osw.write(rsmd.getColumnLabel(i));
                        }
                        osw.write("\r\n");
                    }
                    while (rs.next()) {
                        for (int i = 1; i <= numCols; i++) {
                            if (i > 1) {
                                osw.write("|");
                            }
                            String str = rs.getString(i);
                            osw.write(str);
                        }
                        osw.write("\r\n");
                    }
                    osw.flush();
                    osw.close();
                    //create .ok
                    File okfile = new File(localPath+"/" + name+times + ".txt.ok");
                    if(!okfile.exists()||okfile.isDirectory()){
                        okfile.createNewFile();
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(url + " " + localPath+"/" + name+times + ".txt" + " create sucess!",""));
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                } catch (IOException ex) {
                    logger.debug(url+" The file writen error!"+ex);
                    connFlag = false;
                } catch (SQLException ex) {
                    logger.debug(url+" Get connection  error!"+ex);
                    connFlag = false;
                } finally {
                    try {
                        DBUtil.close(rs, ps, conn);
                    } catch (SQLException ex) {
                        logger.debug(url+" Close the connection  error!"+ex);
                        connFlag = false;
                    }
                }
            }
        }
        return connFlag;
    }
}
