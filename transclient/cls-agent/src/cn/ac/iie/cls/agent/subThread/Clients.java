/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.tools.ProcessTools;
import cn.ac.iie.cls.agent.tools.PropsFiles;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class Clients implements Runnable {

    private static Logger logger = Logger.getLogger(Clients.class);
    String name;

    @Override
    public void run() {
        logger.debug("clients Thread run.");


        //10s send heart beat singnal and log to server
        while (true) {
            String _name = this.name;//system name
            try {
                //get config path for every thread
                String configPath = "";
                try {
                    configPath = PropsFiles.getValue("configPath");
                    //logger.debug(PropsFiles.getValue("configPath"));
                } catch (Exception ex) {
                    logger.debug("load configPath is error.");
                    Thread.sleep(10000);
                    continue;
                }
                File configDir = new File(configPath);
                //start every thread
                List<String> processNameList = new ArrayList<String>();

                for (int i = 0; i < configDir.listFiles().length; i++) {
                    String _filename = (configDir.listFiles())[i].getName();
                    logger.debug("all start client thread " + _filename);
                    processNameList.clear();
                    processNameList = ProcessTools.getNameListFromName(_filename);
                    if (processNameList != null && processNameList.size() > 0) {
                        logger.debug(_filename + " client thread has already start.");
                        continue;
                    }
                    logger.debug("start " + _filename + " client thread.");
                    //start every process
                    logger.debug("start process cmd is " + "java -jar " + PropsFiles.getValue("rootPath") + "/test123.jar " + _filename);
                    if (ProcessTools.startProcess("java -jar " + PropsFiles.getValue("rootPath") + "/test123.jar " + _filename)) {
                        logger.debug("start process success " + _filename);
                    } else {
                        logger.debug("start process error " + _filename);
                    }
                    logger.debug("start " + _filename + " client thread is ok.");
                }
            } catch (InterruptedException ex) {
                logger.debug("clients start error1. " + "|"+ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            } catch (Exception ex) {
                logger.debug("clients start error2. " + "|"+ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                logger.debug("clients start sleep is error. " + "|"+ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            }
        }
    }

    public Clients(String name) {
        this.name = name;
    }
}
