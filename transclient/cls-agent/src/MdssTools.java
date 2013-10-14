/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import cn.ac.iie.cls.agent.tools.*;
import cn.ac.iie.cls.agent.controller.Controller;
import iie.mdss.client.Executer;
import iie.mdss.client.MdssClient;
import iie.mdss.client.ResultSet;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author zmc
 */
public class MdssTools {

    private static Logger logger = Logger.getLogger(DatabaseTools.class);
    public static int connTimes = 5;

    public static boolean downFile(String name, String localPath, String sql, String database_type) {
        boolean connFlag = false;
        if (database_type.equals("MDSS")) {
            MdssClient mdssClient = null;
            Executer executer = null;
            ResultSet resultSet = null;

            for (int n = 0; n < connTimes; n++) {
                try {
                    mdssClient = new MdssClient();
                    if (mdssClient != null) {
                        connFlag = true;
                        break;
                    } else {
                        connFlag = false;
                        logger.debug("Mdss get client error!");
                    }
                } catch (Exception ex) {
                    logger.debug("Mdss get client error!catch!reconnect!");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex1) {
                        logger.debug("Mdss sleep err!");
                    }
                }
            }
            if (!connFlag) {
                logger.debug("Mdss get client error!" + connTimes + " times!");
                return connFlag;
            } else {
                try {
                    executer = mdssClient.getExecuter();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mmss");
                    String times = sdf.format(new Date());
                    String path = localPath + "/" + name + times + ".txt";

                    File file = new File(localPath);
                    if (!file.exists() || !file.isDirectory()) {
                        file.mkdir();
                    }
                    
                    resultSet = executer.executeQuery(sql, path);

                    //create .ok
                    File okfile = new File(localPath + "/" + name + times + ".txt.ok");
                    if (!okfile.exists() || okfile.isDirectory()) {
                        okfile.createNewFile();
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent("MDSS" + " " + localPath + "/" + name + times + ".txt" + " create sucess!", ""));
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                } catch (Exception ex) {
                    logger.debug("MDSS" + " executer error!" + ex);
                    connFlag = false;
                } finally {
                    resultSet.close();
                    executer.close();
                    mdssClient.close();
                }
            }
        }
        return connFlag;
    }
}