/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.tools.XmlTools;
import cn.ac.iie.cls.agent.tools.HdfsTools;
import cn.ac.iie.cls.agent.controller.Controller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class SubHdfsThread implements Runnable {

    private static Logger logger = Logger.getLogger(SubHdfsThread.class);
    String name;
    String xml;

    @Override
    public void run() {
        //get source path
        String localPath = "";
        String type = "";
        String hdfsPath = "";
        String processjobinstanceId = "";
        String operator_id = "";
        
        try {
            localPath = XmlTools.getValueFromStrDGText(this.xml, "srcpath");
            type = XmlTools.getOperatorAttribute(this.xml, "class");
            hdfsPath = XmlTools.getValueFromStrDGText(this.xml, "hdfsPath");
            processjobinstanceId = XmlTools.getElValueFromStr(this.xml, "processJobInstanceId");
            operator_id = XmlTools.getOperatorAttribute(this.xml, "name");
        } catch (Exception ex) {
            logger.debug(name + "read xml err!" + ex);
        }
        
        
        
        
        File filedir = new File(localPath);
        if (!filedir.exists()) {
            logger.debug(localPath+" don't exist");
        }
        if (!filedir.isDirectory()) {
            logger.debug(localPath+" isn't a directory");
        }

        if (processjobinstanceId == null || processjobinstanceId.equals("") || operator_id == null || operator_id.equals("") || localPath == null || localPath.equals("") || hdfsPath == null || hdfsPath.equals("") || type == null || type.equals("")) {
            //Transclient_sub_hippo.sendBuffer.add(name +" path err!");
            logger.debug(name + " task_id or operator_id or path or hdfsPath or type is empty!");
            new HdfsUpload().removeThread(name, type);
            return;
        }
        //get files from hdfs
        HdfsTools hdfsTools = new HdfsTools();
        boolean flag = hdfsTools.getAndUploadDld(name,processjobinstanceId,operator_id,hdfsPath, localPath);
        new HdfsUpload().removeThread(name, type);
//        if (flag) {
//            try {
//                Controller.sendBuffer.put(Controller.getObjectFromContent("name=" + name + ": get success!", task_id + "|" + operator_id + "|output|1"));
//            } catch (InterruptedException ex) {
//                logger.debug("Controller put err!" + ex);
//            }
//            logger.debug("name=" + name + ": get success!");
//        } else {
//            try {
//                Controller.sendBuffer.put(Controller.getObjectFromContent("name=" + name + ": put err!", ""));
//            } catch (InterruptedException ex) {
//                logger.debug("Controller put err!" + ex);
//            }
//            logger.debug("name=" + name + ": get err!");
//        }
        //new HdfsUpload().run(name,path,type,hdfsPath,task_id,operator_id);


    }

    public SubHdfsThread(String name, String xml) {
        this.name = name;
        this.xml = xml;
    }
}
