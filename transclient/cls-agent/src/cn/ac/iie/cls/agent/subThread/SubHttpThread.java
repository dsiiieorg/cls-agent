/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.tools.HttpTools;
import cn.ac.iie.cls.agent.tools.XmlTools;
import cn.ac.iie.cls.agent.tools.PropsFiles;
import cn.ac.iie.cls.agent.controller.Controller;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class SubHttpThread implements Runnable {

    private static Logger logger = Logger.getLogger(SubHttpThread.class);
    String name;
    String xml;

    @Override
    public void run() {

        String username = "";
        String password = "";
        //String remotepath = "";
        String type = "";
        String hdfsPath = "";
        String processjobinstanceId = "";
        String operator_id = "";
        String filePath = "";
        //String dateTime = "";
        

        try {
            filePath = XmlTools.getValueFromStrDG(this.xml, "files");
            operator_id = XmlTools.getOperatorAttribute(this.xml, "name");
            username = XmlTools.getValueFromStrDGText(this.xml, "username");
            password = XmlTools.getValueFromStrDGText(this.xml, "password");
            //remotepath = XmlTools.getValueFromStr(this.xml, "_remotepath");
            type = XmlTools.getOperatorAttribute(this.xml, "class");
            hdfsPath = XmlTools.getValueFromStrDGText(this.xml, "hdfsPath");
            processjobinstanceId = XmlTools.getElValueFromStr(this.xml, "processJobInstanceId");
        } catch (Exception ex) {
            logger.debug(name+"read xml err!"+ex);
        }
        
        if (filePath.equals("") || processjobinstanceId.equals("") || operator_id.equals("") || type.equals("") || username.equals("") || password.equals("") || hdfsPath.equals("")) {
            logger.debug(name + "filePath or type or port or username or password or hdfsPath is empty!");
            new HdfsUpload().removeThread(name, type);
            return;
        }
        String localPath = "";
        try {
            localPath = PropsFiles.getValue("localPath") + "/download/" + name;
        } catch (IOException ex) {
            logger.debug(name + " localPath read err");
            new HdfsUpload().removeThread(name, type);
            return;
        }
        logger.debug(name + " http download start!");
        try {
            Controller.sendBuffer.put(Controller.getObjectFromContent(name + " http download start!", ""));
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }
        HttpTools ht = new HttpTools();

        String[] fileList = filePath.split("[|]");
        for (int i = 0; i < fileList.length; i++) {
            String[] tmplist = fileList[i].split("/");
            if (tmplist == null || tmplist.length < 1) {
                ;
            }
            //create directory if don't exsist
            File dirFile = new File(localPath);
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                dirFile.mkdir();
            }
            if (ht.downFile(fileList[i], localPath + "/" + tmplist[tmplist.length - 1], username, password)) {
                logger.debug(name + " " + fileList[i] + " download success!");
                File okfile = new File(localPath + "/" + tmplist[tmplist.length - 1] + ".ok");
                try {
                    okfile.createNewFile();
                } catch (IOException ex) {
                    logger.debug(name + " create ok file err!" + ex);
                }

            } else {
                logger.debug(name + " download err!");
            }
        }


        //transfer to HDFS
        logger.debug(name + "hdfs upload start!");
        new HdfsUpload().run(name, localPath, type, hdfsPath, processjobinstanceId, operator_id,xml);

    }

    public SubHttpThread(String name, String xml) {
        this.name = name;
        this.xml = xml;
    }
}
