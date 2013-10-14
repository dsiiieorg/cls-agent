/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.controller;

import cn.ac.iie.cls.agent.tools.AvroClient;
import cn.ac.iie.cls.agent.tools.PropsFiles;
import cn.ac.iie.cls.agent.subThread.Heartbeat;
import cn.ac.iie.cls.agent.subThread.ZooKeeperThread;
import cn.ac.iie.cls.agent.subThread.ListernThread;
import cn.ac.iie.cls.agent.po.MessagePo;
import cn.ac.iie.cls.agent.po.TimePo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.ipc.generic.GenericRequestor;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author root
 */
public class Controller {

    private static Logger logger = Logger.getLogger(Controller.class);                          //start log4j
    public static BlockingQueue<MessagePo> sendBuffer = new LinkedBlockingQueue<MessagePo>();   //log list
    public static BlockingQueue<String> subThreadList = new LinkedBlockingQueue<String>();      //active subThread list
    public static String sysControllerIP = "";
    public static AvroClient avc = null;
    public static GenericRecord requestData = null;
    public static GenericRecord request = null;
    public static GenericRequestor requestor = null;
    public static String localIP = "";

    public static void main(String[] args) {

        //get log4j properites
        try {
            PropertyConfigurator.configure(PropsFiles.getValue("propertiesRootPath") + "/cls_agent_log.properties");
        } catch (IOException ex) {
            logger.debug("read propertiesRootPath err!");
            return;
        }
        try {
            localIP = PropsFiles.getValue("localIP");
        } catch (IOException ex) {
            logger.debug("read localIP err!");
            return;
        }
        logger.debug("Controller is begining.");
        try {
            Controller.sendBuffer.put(Controller.getObjectFromContent("Client Controller is begining.", ""));
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }


        //reg zookeeper
        try {
            logger.debug("new zookeeper thread.");
            ZooKeeperThread zookeeper = new ZooKeeperThread("ZooKeeper");
            Thread zookeeperThread = new Thread(zookeeper);
            zookeeperThread.start();
            logger.debug("Zookeeper thread start ok.");
        } catch (Exception ex) {
            logger.debug("reg zookeeper error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            return;
        }


        //start listern thread:get server xml str and start client and start timer for killing(需要改动)
        try {
            logger.debug("new listern thread.");
            ListernThread lsnThread = new ListernThread("ListernThread");
            Thread listernThread = new Thread(lsnThread);
            listernThread.start();
            logger.debug("listern thread start ok.");
        } catch (Exception ex) {
            logger.debug("start listern error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            return;
        }


        //start heartbeat thread(contains sending log)
        try {
            logger.debug("new heartbeat thread.");
            Heartbeat hb = new Heartbeat("heartbeat");
            Thread hbThread = new Thread(hb);
            hbThread.start();
            logger.debug("heartbeat thread start ok.");
        } catch (Exception ex) {
            logger.debug("start heartbeat error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            return;
        }
        try {
            Controller.sendBuffer.put(Controller.getObjectFromContent("Client Controller has finished!", ""));
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }
        //if Controller.sysControllerIP ="" return,beacause zookeeper err!
        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex) {
                ;
            }
            if (Controller.sysControllerIP.equals("")) {
                logger.debug("beacause zookeeper err,Controller.sysControllerIP is empty.System.exit(1)");
                System.exit(1);
            }
        }

    }

    public static MessagePo getObjectFromContent(String statusStr, String databaseStr) {
        logger.debug(localIP + " statusStr:" + statusStr + ",databaseStr:" + databaseStr);
        MessagePo mesPo = new MessagePo();
        mesPo.setId("2");
        mesPo.setContent(new Date().toString() + " " + localIP + " " + statusStr);
        mesPo.setReadFlag(false);
        if (databaseStr.equals("")) {
            databaseStr = "status|" + mesPo.getContent();
        } else {
            databaseStr = "database|" + databaseStr;
        }
        mesPo.setXml(databaseStr);
        return mesPo;
    }
}
