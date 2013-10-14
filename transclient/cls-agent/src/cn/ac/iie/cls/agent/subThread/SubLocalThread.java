/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.tools.XmlTools;
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
public class SubLocalThread implements Runnable {

    private static Logger logger = Logger.getLogger(SubLocalThread.class);
    String name;
    String xml;

    @Override
    public void run() {
        //get source path
        String path = "";
        String type = "";
        String hdfsPath = "";
        String processjobinstanceId = "";
        String operator_id = "";
        try {
            path = XmlTools.getValueFromStrDGText(this.xml, "srcpath");
            type = XmlTools.getOperatorAttribute(this.xml, "class");
            hdfsPath = XmlTools.getValueFromStrDGText(this.xml, "hdfsPath");
            processjobinstanceId = XmlTools.getElValueFromStr(this.xml, "processJobInstanceId");
            operator_id = XmlTools.getOperatorAttribute(this.xml, "name");
        } catch (Exception ex) {
            logger.debug(name + "read xml err!" + ex);
        }

        if (processjobinstanceId == null || processjobinstanceId.equals("") || operator_id == null || operator_id.equals("") || path == null || path.equals("") || hdfsPath == null || hdfsPath.equals("") || type == null || type.equals("")) {
            //Transclient_sub_hippo.sendBuffer.add(name +" path err!");
            logger.debug(name + " processjobinstanceId or operator_id or path or hdfsPath or type is empty!");
            new HdfsUpload().removeThread(name, type);
            return;
        }
        new HdfsUpload().run(name, path, type, hdfsPath, processjobinstanceId, operator_id, xml);


    }

    public SubLocalThread(String name, String xml) {
        this.name = name;
        this.xml = xml;
    }
}
