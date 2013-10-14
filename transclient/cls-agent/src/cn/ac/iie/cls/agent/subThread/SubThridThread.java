/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.tools.XmlTools;
import cn.ac.iie.cls.agent.controller.Controller;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class SubThridThread implements Runnable {

    private static Logger logger = Logger.getLogger(SubThridThread.class);
    String name;
    String xml;

    @Override
    public void run() {
        //start third process
        String cmd = "";
        //String sourcePath = "";
        String resultFilePath = "";
        String type = "";
        String hdfsPath = "";
        String operator_id = "";
        String processjobinstanceId = "";

        try {
            resultFilePath = XmlTools.getValueFromStrDGText(this.xml, "resultFilePath");
            cmd = XmlTools.getValueFromStrDGText(this.xml, "cmd");
            type = XmlTools.getOperatorAttribute(this.xml, "class");
            hdfsPath = XmlTools.getValueFromStrDGText(this.xml, "hdfsPath");
            processjobinstanceId = XmlTools.getElValueFromStr(this.xml, "processJobInstanceId");
            operator_id = XmlTools.getOperatorAttribute(this.xml, "name");
        } catch (Exception ex) {
            logger.debug(name + "read xml err!" + ex);
        }


        if (processjobinstanceId.equals("")||operator_id.equals("") || type.equals("") || cmd.equals("") || resultFilePath.equals("") || hdfsPath.equals("")) {
            logger.debug(name + " type or ftpip or port or username or password or hdfsPath is empty!");
            new HdfsUpload().removeThread(name, type);
            return;
        }
        //start
        try {
            String[] cmdList = cmd.split(" ");
            ProcessBuilder prc = new ProcessBuilder(cmdList);
            //prc.directory(new File(sourcePath));
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent(name + " thrid process has start! cmd:" + cmd, ""));
            } catch (InterruptedException ex) {
                logger.debug("Controller put err!" + ex);
            }
            Process ps = prc.start();
            new StreamGobbler(ps.getInputStream(), "INFO").run();
            new StreamGobbler(ps.getErrorStream(), "ERROR").run();
            int i = ps.waitFor();
            if (i == 0) {
                try {
                    Controller.sendBuffer.put(Controller.getObjectFromContent(name + " thrid process end!", ""));
                } catch (InterruptedException ex) {
                    logger.debug("Controller put err!" + ex);
                }
                logger.debug(name + " thrid process end!");
            } else {
                ;
            }
        } catch (Exception ex) {
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent(name + "third process err!" + ex, ""));
            } catch (InterruptedException ex1) {
                logger.debug("Controller put err!" + ex1);
            }
            logger.debug(name + "third process err!" + ex);
        }

        //transfer to HDFS
        logger.debug(name + "hdfs upload start!");
        new HdfsUpload().run(name, resultFilePath, type, hdfsPath, processjobinstanceId, operator_id, xml);

    }

    public SubThridThread(String name, String xml) {
        this.name = name;
        this.xml = xml;
    }
}