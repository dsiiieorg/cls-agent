/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.subThread.LocalThreadPoolTask;
import cn.ac.iie.cls.agent.subThread.SubLocalThread;
import cn.ac.iie.cls.agent.tools.ProcFile;
import cn.ac.iie.cls.agent.tools.XmlTools;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class HdfsUploadShadowserver {

    private static Logger logger = Logger.getLogger(HdfsUploadShadowserver.class);

    public void run(String name, String path, String type, String hdfsPath, String task_id, String operator_id, String xml, String filetype) {
        //get source files
        logger.debug(name + " path is " + path);
        File fileRoot = new File(path);
        if (!fileRoot.isDirectory()) {
            //Transclient_sub_hippo.sendBuffer.add(name +" fileRoot err!");
            logger.debug(name + " " + path + "is not a dir!");
            removeThread(name, type);
            return;
        }
        String[] allFileList = fileRoot.list();

        //match file
        List<String> fileList = new ArrayList<String>();
        List<String> okFileList = new ArrayList<String>();
        List<String> finalFileList = new ArrayList<String>();
        for (int i = 0; i < allFileList.length; i++) {
            if (!allFileList[i].contains(filetype)) {
                logger.debug("######allFileList[i]: "+allFileList[i] +" : "+filetype);
                continue;
            }
            if (allFileList[i].endsWith(".ok")) {
                okFileList.add(allFileList[i]);
            } else {
                fileList.add(allFileList[i]);
            }
        }
        for (int i = 0; i < fileList.size(); i++) {
            for (int j = 0; j < okFileList.size(); j++) {
                if ((fileList.get(i) + ".ok").equals(okFileList.get(j))) {
                    finalFileList.add(fileList.get(i));
                }
            }
        }
        for (int i = 0; i < finalFileList.size(); i++) {
            logger.debug(name + "big finalFileList [" + i + "] = " + finalFileList.get(i));
        }
        //split
        String splitFlag = "false";
        splitFlag = XmlTools.getValueFromStrDG(xml, "exe");
        String num = XmlTools.getValueFromStrDG(xml, "num");
        String fileEncoding = XmlTools.getValueFromStrDG(xml, "fileEncoding");
        int tmpNum = 0;
        try {
            tmpNum = Integer.parseInt(num);
        } catch (Exception ex) {
            logger.debug(name + " split num err!" + ex);
            splitFlag = "false";
        }
        if (tmpNum <= 0) {
            splitFlag = "false";
        }
        if (splitFlag.equals("true")) {
            for (int i = 0; i < finalFileList.size(); i++) {
                //File tmpfile = new File(finalFileList.get(i));
                if (finalFileList.get(i).endsWith("txt") || finalFileList.get(i).endsWith("csv") || finalFileList.get(i).endsWith("TXT") || finalFileList.get(i).endsWith("CSV")) {
                    boolean flag = ProcFile.splitFile(path + "/" + finalFileList.get(i), tmpNum,fileEncoding);
                    logger.debug(name + " " + finalFileList.get(i) + " has been split!" + flag);
                }

            }

            //reset finalFileList because split
            String[] allFileList1 = fileRoot.list();
            fileList.clear();
            okFileList.clear();
            finalFileList.clear();

            for (int i = 0; i < allFileList1.length; i++) {
                if (!allFileList1[i].contains(filetype)) {
                    continue;
                }
                if (allFileList1[i].endsWith(".ok")) {
                    okFileList.add(allFileList1[i]);
                } else {
                    fileList.add(allFileList1[i]);
                }
            }
            for (int i = 0; i < fileList.size(); i++) {
                for (int j = 0; j < okFileList.size(); j++) {
                    if ((fileList.get(i) + ".ok").equals(okFileList.get(j))) {
                        finalFileList.add(fileList.get(i));
                    }
                }
            }
//            String totalName = "";
//            for (int i = 0; i < finalFileList.size(); i++) {
//                logger.debug(name + "small finalFileList [" + i + "] = " + finalFileList.get(i));
//                if (i != finalFileList.size() - 1) {
//                    totalName = totalName + finalFileList.get(i) + "|";
//                } else {
//                    totalName = totalName + finalFileList.get(i);
//                }
//            }

//            try {
//                Controller.sendBuffer.put(Controller.getObjectFromContent("name:" + name + " total files is " + finalFileList.size(), totalName));
//            } catch (InterruptedException ex) {
//                logger.debug("Controller put err!" + ex);
//            }

        }
        String totalName = "";
        for (int i = 0; i < finalFileList.size(); i++) {
            //logger.debug(name + "small finalFileList [" + i + "] = " + finalFileList.get(i));
            if (i != finalFileList.size() - 1) {
                totalName = totalName + hdfsPath + "/" + name + "/" + finalFileList.get(i) + "|";
            } else {
                totalName = totalName + hdfsPath + "/" + name + "/" + finalFileList.get(i);
            }
        }
        try {
            //Controller.sendBuffer.put(Controller.getObjectFromContent("name:" + name + " total files is " + finalFileList.size(), task_id + "|" + operator_id + "|input|" + finalFileList.size()));
            Controller.sendBuffer.put(Controller.getObjectFromContent("name:" + name + " total files is " + finalFileList.size(), "all" + "|" + type + "|" + task_id + "|" + operator_id + "|input|" + totalName));
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }


//        String cmd = null;
//
//        cmd = "nohup java -jar "
//                + "/usr/iie/transclient/TestPrint/dist/TestPrint.jar" + " &";
//
//        try {
//            //ProcessTools.startProcess(cmd);
//            //Process prc=new ProcessBuilder(cmd).start();
//            Process prc=Runtime.getRuntime().exec(cmd);
//            int rv =-1;
//            try {
//                
//                rv = prc.waitFor();
//            } catch (InterruptedException ex) {
//                //java.util.logging.Logger.getLogger(SubLocalThread.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (IOException ex) {
//            //java.util.logging.Logger.getLogger(SubDatabaseThread.class.getName()).log(Level.SEVERE, null, ex);
//        }
        //transfer to HDFS
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 4, 3,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000),
                new ThreadPoolExecutor.DiscardOldestPolicy());

        logger.debug(name + " finalFileList.size() = " + finalFileList.size());
        for (int i = 0; i < finalFileList.size(); i++) {
            threadPool.execute(new LocalThreadPoolTask(name, path + "/" + finalFileList.get(i), hdfsPath, task_id, operator_id, type));
            //threadPool.execute(new LocalThreadPoolTask(name, path + "/" + finalFileList.get(i)), hdfsPath);
            logger.debug(name + "finalFileList [" + i + "] = " + finalFileList.get(i));

        }

        while (true) {
            int activeThread = threadPool.getActiveCount();
            if (activeThread == 0) {
                break;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    logger.debug("sleep err!" + ex);
                }
            }
        }
        removeThread(name, type);
        threadPool.shutdown();
    }

    public void removeThread(String name, String type) {
        logger.debug(name + " is finish! bye!");
        Iterator<String> itr = Controller.subThreadList.iterator();
        while (itr.hasNext()) {
            if (itr.next().equals(name + "|" + type)) {
                itr.remove();
            }
        }
        try {
            Controller.sendBuffer.put(Controller.getObjectFromContent(name + " is finish! bye!", ""));
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }
    }
}
