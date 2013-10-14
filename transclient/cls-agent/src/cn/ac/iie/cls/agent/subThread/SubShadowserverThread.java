 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.tools.XmlTools;
import cn.ac.iie.cls.agent.tools.ShadowserverTools;
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
public class SubShadowserverThread implements Runnable {

    private static Logger logger = Logger.getLogger(SubShadowserverThread.class);
    String name;
    String xml;

    @Override
    public void run() {
        //download ftp file
        //String ftpip = "";
        //String port = "";
        String username = "";
        String password = "";
        //String remotepath = "";
        String type = "";
        String hdfsPath = "";
        String processjobinstanceId = "";
        String operator_id = "";
        String protocol="";
        String address="";   
        String filetype="";
        
        try {
            protocol = XmlTools.getValueFromStrDGText(this.xml, "protocol");
            address = XmlTools.getValueFromStrDGText(this.xml, "address");
            operator_id = XmlTools.getOperatorAttribute(this.xml, "name");
            username = XmlTools.getValueFromStrDGText(this.xml, "username");
            password = XmlTools.getValueFromStrDGText(this.xml, "password");
            filetype = XmlTools.getValueFromStrDGText(this.xml, "filetype");
            type = XmlTools.getOperatorAttribute(this.xml, "class");
            hdfsPath = XmlTools.getValueFromStrDGText(this.xml, "hdfsPath");
            processjobinstanceId = XmlTools.getElValueFromStr(this.xml, "processJobInstanceId");
        } catch (Exception ex) {
            logger.debug(name + "read xml err!" + ex);
        }
        
        

        if (filetype.equals("")||protocol.equals("")||address.equals("") ||processjobinstanceId.equals("")||operator_id.equals("")||type.equals("")||/*ftpip.equals("") ||*/ username.equals("") || password.equals("") || hdfsPath.equals("") /*|| port.equals("")*/) {
            logger.debug(name + " protocol or address or type or ftpip or port or username or password or hdfsPath is empty!");
            new HdfsUpload().removeThread(name, type);
            return;
        }
//        int portInt = 0;
//        try {
//            portInt = Integer.valueOf(port);
//        } catch (Exception ex) {
//            logger.debug(name + " port is not a int");
//            return;
//        }
//        if (portInt == 0) {
//            logger.debug(name + " port is not a int");
//            return;
//        }

        String localPath = "";
        try {
            localPath = PropsFiles.getValue("localPath") + "/download/"+name;
        } catch (IOException ex) {
            logger.debug(name + " localPath read err");
            new HdfsUpload().removeThread(name, type);
            return;
        }
        logger.debug(name+"shadowserver download start!");
        try {
            Controller.sendBuffer.put(Controller.getObjectFromContent(name+"shadowserver download start!",""));
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }
        ShadowserverTools.downFile(name,protocol,address,username, password, localPath);
        //transfer to HDFS
        logger.debug(name+"hdfs upload start!");
        new HdfsUploadShadowserver().run(name, localPath, type,hdfsPath,processjobinstanceId,operator_id,xml,filetype);

    }

    public SubShadowserverThread(String name, String xml) {
        this.name = name;
        this.xml = xml;
    }
}
