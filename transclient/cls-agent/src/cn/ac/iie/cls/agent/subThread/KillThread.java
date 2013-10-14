/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.controller.Controller;

import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.ac.iie.cls.agent.tools.ProcessTools;
import cn.ac.iie.cls.agent.tools.PropsFiles;
import cn.ac.iie.cls.agent.tools.XmlTools;
import java.util.ArrayList;

/**
 *
 * @author root
 */
public class KillThread implements Runnable {

    private static Logger logger = Logger.getLogger(KillThread.class);

    @Override
    public void run() {
        while (true) {
            //get all subtrans* thread
            List<String> pidList = new ArrayList<String>();
            try {
                pidList = ProcessTools.getIDList(PropsFiles.getValue("subThreadNameMatch"));
            } catch (IOException ex) {
                logger.debug("read property file error!" + "|"+ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            }

            //check overtime and kill it
            for (int i = 0; i < pidList.size(); i++) {
                List<String> configFileNameList = ProcessTools.getConfigFileNameListFromId(pidList.get(i));
                if (configFileNameList == null || configFileNameList.size() == 0) {
                    continue;
                }
                String configName0 = configFileNameList.get(0);
                if (configName0 != null && !configName0.equals("")) {
                    ;
                } else {
                    continue;
                }

                if (configName0.endsWith("swp")) {
                    logger.debug("has swp file" + configName0);
                    continue;
                }
                int time = 0;
                time = ProcessTools.getProcessTime(pidList.get(i));
                int timeout = 0;
                //timeout = XmlTools.getTimeOut(null);
                //get timeout from xml
                try {
                    timeout = Integer.parseInt(XmlTools.getValue(PropsFiles.getValue("configPath") + "/" + configName0, "_timeout"));
                    //timeout = XmlTools.getTimeOut(configFileNameList.get(0));
                } catch (Exception ex) {
                    try {
                        timeout = Integer.parseInt(PropsFiles.getValue("timeout"));
                    } catch (IOException ex1) {
                        logger.debug("timeout error! read properties file error!");
                    }
                    logger.debug("getTimeOut error" + pidList.get(i) + "|" + "|"+ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
                }

                if (time > timeout) {
                    logger.debug(time);
                    try {
                        if (ProcessTools.killProcess(pidList.get(i))) {
                            logger.debug("overtime process is killed! " + pidList.get(i));
                        } else {
                            logger.debug("overtime process not be killed! " + pidList.get(i));
                        }

                    } catch (IOException ex) {
                        logger.debug("kill process error!" + pidList.get(i) + "|" + "|"+ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
                    }
                }
            }


            try {
                Thread.sleep(3000);
            } catch (Exception ex) {
                logger.debug("killthread sleep error. " + ex.getMessage() + "|" + ex.getStackTrace() + "|" + ex.getLocalizedMessage() + "|"+ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            }
        }
    }
}
