/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.tools.HdfsTools;

import java.io.Serializable;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class LocalThreadPoolTask implements Runnable, Serializable {

    private static final long serialVersionUID = 0;
    private String name;
    private String sysName;
    private String hdfsPath;
    private String task_id;
    private String operator_id;
    private String type;
    private static Logger logger = Logger.getLogger(LocalThreadPoolTask.class);

    LocalThreadPoolTask(String sysName, String name, String hdfsPath, String task_id, String operator_id,String type) {
        this.type=type;
        this.sysName = sysName;
        this.name = name;
        this.hdfsPath = hdfsPath;
        this.task_id = task_id;
        this.operator_id = operator_id;
    }

    public void run() {
        logger.debug(sysName + ": name= " + name+": type "+type);
        HdfsTools hdfsTools = new HdfsTools();
        boolean flag = hdfsTools.put(name, sysName, hdfsPath);
        String tmpname = "";
        String[] listtmpname = name.split("/");
        if (listtmpname.length < 2) {
            tmpname = "err";
        } else {
            tmpname = hdfsPath + "/" + sysName + "/" + listtmpname[listtmpname.length - 1];

        }
        if (flag) {
            try {
                //Controller.sendBuffer.put(Controller.getObjectFromContent(sysName + ": name=" + name + ": put success!",task_id+"|"+operator_id+"|output|1"));

                Controller.sendBuffer.put(Controller.getObjectFromContent(sysName + ": name=" + name + ": put success!", "one|"+type +"|" + task_id + "|" + operator_id + "|output|" + tmpname));
            } catch (InterruptedException ex) {
                logger.debug("Controller put err!" + ex);
            }
            logger.debug(sysName + ": name=" + name + ": put success!");
        } else {
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent(sysName + ": name=" + name + ": put err!", "err|"+type +"|"+ task_id + "|" + operator_id + "|output|" + tmpname));
            } catch (InterruptedException ex) {
                logger.debug("Controller put err!" + ex);
            }
            logger.debug(sysName + ": name=" + name + ": put err!");
        }
        /*
         * while(true){ try { Thread.sleep(10000); } catch (InterruptedException
         * ex) {
         * ;//java.util.logging.Logger.getLogger(LocalThreadPoolTask.class.getName()).log(Level.SEVERE,
         * null, ex); }
         }
         */
    }

    /**
     * @return the hdfsPath
     */
    public String getHdfsPath() {
        return hdfsPath;
    }

    /**
     * @param hdfsPath the hdfsPath to set
     */
    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }
}
