/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.tools.ZooKeeperOperator;
import cn.ac.iie.cls.agent.tools.PropsFiles;
import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.po.MessagePo;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;

/**
 *
 * @author root
 * @
 */
public class ZooKeeperThread implements Runnable {

    private static Logger logger = Logger.getLogger(ZooKeeperThread.class);
    public String name = "";

    @Override
    public void run() {
        logger.debug("zookeeper reg start");
        try {
            Controller.sendBuffer.put(Controller.getObjectFromContent("zookeeper reg is beginning! please wait...", ""));
            logger.debug("zookeeper reg is beginning! please wait...");
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }
        ZooKeeperOperator zkoperator = new ZooKeeperOperator();
        try {
            zkoperator.connect(PropsFiles.getValue("zookeeperServer"));
            if (zkoperator.exists("/agent").equals("err")) {
                ;
            } else if (zkoperator.exists("/agent").equals("true")) {
                ;
            } else if (zkoperator.exists("/agent").equals("false")) {
                zkoperator.create("/agent", null, "PERSISTENT");
            }

            zkoperator.create("/agent/" + PropsFiles.getValue("localIP"), null, "EPHEMERAL");
        } catch (Exception ex) {
            logger.debug("zkoperator reg err! " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent("zookeeper reg err.", ""));
            } catch (InterruptedException ex1) {
                logger.debug("Controller put err!" + ex1);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex1) {
                logger.debug("zookeeper sleep err!" + ex1);
            }
            //throw new Exception("zookeeper err!");
            return;
        }
        logger.debug("zookeeper reg ok");
        try {
            Controller.sendBuffer.put(Controller.getObjectFromContent("zookeeper reg ok.", ""));
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }
        logger.debug("zookeeper watcher start");
        try {
            zkoperator.setWatcher("/cls-cc/master");
            logger.debug("zookeeper watcher ok");
        } catch (Exception ex) {
            logger.debug("zkoperator setWatcher err! " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
        }
    }

    public ZooKeeperThread(String name) {
        this.name = name;
    }
}
