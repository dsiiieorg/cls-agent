/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.tools.MdssTools ;
import cn.ac.iie.cls.agent.tools.XmlTools;
import cn.ac.iie.cls.agent.tools.PropsFiles;
import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.tools.MdssTools;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author zmc
 */
public class SubMDSSThread implements Runnable {

    private static Logger logger = Logger.getLogger(SubMDSSThread.class);
    String name;
    String xml;
    
    // TODO zmc  SubMDSSThread
    @Override
    public void run() {
        String type = "";
        String hdfsPath = "";
        String processjobinstanceId = "";
        String operator_id = "";
        String database_type = "";
        String sql = "";

        try {
            database_type = XmlTools.getValueFromStrDGText(this.xml, "database_type");
            sql = XmlTools.getValueFromStrDGText(this.xml, "sql");
            type = XmlTools.getOperatorAttribute(this.xml, "class");
            hdfsPath = XmlTools.getValueFromStrDGText(this.xml, "hdfsPath");
            processjobinstanceId = XmlTools.getElValueFromStr(this.xml, "processJobInstanceId");
            operator_id = XmlTools.getOperatorAttribute(this.xml, "name");
        } catch (Exception ex) {
            logger.debug(name + "read xml err!" + ex);
        }


        if (database_type.equals("")
                || processjobinstanceId.equals("") || operator_id.equals("") || type.equals("") || hdfsPath.equals("")||sql.equals("")) {
            logger.debug(name + " instance or sql or database or type or ftpip or port or username or password or hdfsPath is empty!");
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
        logger.debug(name + " database download start!");
        try {
            Controller.sendBuffer.put(Controller.getObjectFromContent(name + " database download start!", ""));
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }
        
        
        // TODO MDSS file download
        boolean flag = MdssTools.downFile(name, localPath, sql, database_type);
        if (!flag) {
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent(name + " database download false!", ""));
            } catch (InterruptedException ex) {
                logger.debug("Controller put err!" + ex);
            }
        }
        
        
        logger.debug(name + "hdfs upload start!");
        new HdfsUpload().run(name, localPath, type, hdfsPath, processjobinstanceId, operator_id, xml);

    }

    public SubMDSSThread(String name, String xml) {
        this.name = name;
        this.xml = xml;
    }
}
