/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.tools.FtpTools;
import cn.ac.iie.cls.agent.tools.XmlTools;
import cn.ac.iie.cls.agent.tools.PropsFiles;
import cn.ac.iie.cls.agent.controller.Controller;
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
public class SubFtpThread implements Runnable {

    private static Logger logger = Logger.getLogger(SubFtpThread.class);
    String name;
    String xml;

    @Override
    public void run() {
        //download ftp file
        String ftpip = "";
        String port = "";
        String username = "";
        String password = "";
        String remotepath = "";
        String type = "";
        String hdfsPath = "";
        String processjobinstanceId = "";
        String operator_id = "";
        try {
            operator_id = XmlTools.getOperatorAttribute(this.xml, "name");
            ftpip = XmlTools.getValueFromStrDGText(this.xml, "ftpip");
            port = XmlTools.getValueFromStrDGText(this.xml, "port");
            username = XmlTools.getValueFromStrDGText(this.xml, "username");
            password = XmlTools.getValueFromStrDGText(this.xml, "password");
            remotepath = XmlTools.getValueFromStrDGText(this.xml, "remotepath");
            type = XmlTools.getOperatorAttribute(this.xml, "class");
            hdfsPath = XmlTools.getValueFromStrDGText(this.xml, "hdfsPath");
            processjobinstanceId = XmlTools.getElValueFromStr(this.xml, "processJobInstanceId");
        } catch (Exception ex) {
            logger.debug(name+"read xml err!"+ex,ex);
        }

        if (processjobinstanceId.equals("")||operator_id.equals("") || type.equals("") || ftpip.equals("") || username.equals("") || password.equals("") || hdfsPath.equals("") || port.equals("")||remotepath.equals("")) {
            logger.debug(name + " type or ftpip or port or username or password or hdfsPath is empty!");
            new HdfsUpload().removeThread(name, type);
            return;
        }
        int portInt = 0;
        try {
            portInt = Integer.valueOf(port);
        } catch (Exception ex) {
            logger.debug(name + " port is not a int");
            new HdfsUpload().removeThread(name, type);
            return;
        }
        if (portInt == 0) {
            logger.debug(name + " port is not a int");
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
        logger.debug(name + "ftp download start!");
        try {
            Controller.sendBuffer.put(Controller.getObjectFromContent(name + "ftp download start!", ""));
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }
        FtpTools.downFile(ftpip, portInt, username, password
                  , remotepath
                 , localPath);
        //transfer to HDFS
        logger.debug(name + "hdfs upload start!");
        new HdfsUpload().run(name, localPath, type, hdfsPath, processjobinstanceId, operator_id, xml);

    }

    public SubFtpThread(String name, String xml) {
        this.name = name;
        this.xml = xml;
    }
}
