/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.po.MessagePo;

import cn.ac.iie.cls.agent.tools.PropsFiles;
import java.io.IOException;
import org.apache.log4j.Logger;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Iterator;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author root
 */
public class Heartbeat implements Runnable {

    private static Logger logger = Logger.getLogger(Heartbeat.class);
    String name;

    @Override
    public void run() {
        logger.debug("heartbeat Thread run.");
        int sleepTime = 0;
        try {
            sleepTime = Integer.parseInt(PropsFiles.getValue("heartbeatTime"));
        } catch (IOException ex) {
            logger.debug("heartbeat sleep time error!read property file error!" + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
        }
        if (sleepTime == 0) {
            logger.debug("sleeptime == 0");
            return;
        }
        logger.debug("synList sendAndRemove start!");

        //10s send heart beat singnal and log to server
        while (true) {
            //String _name = this.name;//system name
            //String _heartbeatSingnal = "ok";//heartbeat Singnal
            //String _log = "test";//log
            if (Controller.sysControllerIP.equals("")) {
                logger.debug("Controller.sysControllerIP err! so heartbeat return");
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception ex) {
                    logger.debug("sleeptime err! " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
                }
                continue;
            }

            try {
                HttpClient httpClient = new DefaultHttpClient();
                //HttpPost httppost = new HttpPost("http://127.0.0.1:7060/resources/clsagent/report");
                //HttpPost httppost = new HttpPost("http://192.168.111.167:7060/resources/clsagent/report");
                HttpPost httppost = new HttpPost("http://" + Controller.sysControllerIP + "/resources/clsagent/report");

                //sqlite send
                Connection conn = SqliteTools.connect();
                Map map = SqliteTools.getData(conn);
                Iterator itrSqlite = map.keySet().iterator();
                while (itrSqlite.hasNext()) {
                    String key = itrSqlite.next().toString();
                    String value = map.get(key).toString();
                    logger.debug("map key:" + key + "|value:" + value);
                    InputStreamEntity reqEntity = new InputStreamEntity(new ByteArrayInputStream(value.getBytes()), -1);
                    reqEntity.setContentType("binary/octet-stream");
                    reqEntity.setChunked(true);
                    httppost.setEntity(reqEntity);
                    try {
                        HttpResponse response = httpClient.execute(httppost);
                        if (SqliteTools.remove(conn, key)) {
                            logger.debug("success! " + value + " has been removed!");
                        } else {
                            logger.debug("err! " + value + " hasn't been removed!");
                        }
                    } catch (Exception e) {
                        logger.debug("sqlite send err!" + e);
                    }
                    httppost.releaseConnection();
                }


                Iterator<MessagePo> itr1 = Controller.sendBuffer.iterator();
 //               logger.debug("synList.size() is " + Controller.sendBuffer.size());
                //Controller.sendBuffer.put(Controller.getObjectFromContent((new Date()).toString() + " heartbeat", ""));
                while (itr1.hasNext()) {
                    //logger.debug("@@@@@synList.size() is "+synList.size());
                    MessagePo mesPo = itr1.next();
                    if (!mesPo.getReadFlag()) {
                        InputStreamEntity reqEntity = new InputStreamEntity(new ByteArrayInputStream(mesPo.getXml().getBytes()), -1);
                        reqEntity.setContentType("binary/octet-stream");
                        reqEntity.setChunked(true);
                        httppost.setEntity(reqEntity);
                        try {
                            HttpResponse response = httpClient.execute(httppost);
                            mesPo.setReadFlag(true);
                        } catch (Exception e) {
                            logger.debug("Jetty client send message err!" + e);
                            //Connection conn = SqliteTools.connect();
                            if (conn != null) {
                                SqliteTools.save(conn, mesPo.getXml());
                                mesPo.setReadFlag(true);
                            } else {
                                ;
                            }
                        }
                        httppost.releaseConnection();

                    }
                }
                try {
                    conn.close();
                } catch (SQLException ex) {
                    logger.debug("conn close err!" + ex);
                }
                Iterator<MessagePo> itr = Controller.sendBuffer.iterator();
                while (itr.hasNext()) {
                    MessagePo mesPo = itr.next();
                    if (mesPo.getReadFlag()) {
                        itr.remove();
                    }
                }

                //Controller.avc.run();
                //logger.debug("jetty send run ok.");
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                logger.debug("jetty send error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
                //Logger.getLogger(Heartbeat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Heartbeat(String name) {
        this.name = name;
    }
}
