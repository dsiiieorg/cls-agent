/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import cn.ac.iie.cls.agent.controller.Controller;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class HdfsTools {

    private static Logger logger = Logger.getLogger(HdfsTools.class);

    public boolean put(String srcFile, String dstFile, String hdfsPath) {
        String fileName = "";
        String[] tmplist = srcFile.split("/");
        fileName = tmplist[tmplist.length - 1];
        if (fileName.equals("")) {
            logger.debug(dstFile + " fileName err!");
            return false;
        }

        //String hdfsIP = "";
        //       String hdfsRootPath = "";
//        try {
//            //hdfsIP = PropsFiles.getValue("hdfsIP");
//            //hdfsRootPath = PropsFiles.getValue("hdfsRootPath");
//            hdfsRootPath = hdfsPath;
//        } catch (IOException ex) {
//            try {
//                Controller.sendBuffer.put(Controller.getObjectFromContent("Client Controller has finished!"));
//            } catch (InterruptedException ex1) {
//                logger.debug("Controller put err!" + ex1);
//            }
//            logger.debug(srcFile + " " + dstFile + " hdfsIP or hdfsRootPath get error!");
//            return false;
//        }
        //logger.debug("bbbbbbbbbbbbbbbbbbbbbbbbb");
        //String dst = "hdfs://" + hdfsIP + hdfsRootPath;
        String dst = hdfsPath;
        //Configuration conf = new Configuration();
        try {
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(URI.create(dst), conf);
            Path srcPath = new Path(srcFile);
            //Path dstPath = new Path(hdfsRootPath + "/" + dstFile);
            Path dstPath = new Path(hdfsPath + "/" + dstFile);
            //check hdfsPath if is a Directory
            if (fs.isDirectory(dstPath)) {
                //if (fs.exists(dstPath)) {
                ;
            } else {
                fs.mkdirs(dstPath);
            }


            fs.copyFromLocalFile(srcPath, dstPath);
            //文件原地址 
            File oldFile = new File(srcFile);
            File oldFileOk = new File(srcFile + ".ok");
            //文件新（目标）地址 
            String newPath = "";
            newPath = srcFile.substring(0, srcFile.indexOf(fileName));
//            for (int ii = 0; ii < tmplist.length - 1; ii++) {
//                if(ii==0){
//                
//                }
//                newPath = newPath + tmplist[ii];
//            }
            newPath = newPath + "upload_sucess/";
            //String newPath = "c:/test/"; 
            //new一个新文件夹 
            File fnewpath = new File(newPath);
            //判断文件夹是否存在 
            if (!fnewpath.exists()) {
                fnewpath.mkdirs();
            }
            //将文件移到新文件里 
            File fnew = new File(newPath + oldFile.getName());
            File fnewOk = new File(newPath + oldFileOk.getName());
            oldFile.renameTo(fnew);
            oldFileOk.renameTo(fnewOk);

        } catch (Exception ex) {
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent("Client Controller has finished!", ""));
            } catch (InterruptedException ex1) {
                logger.debug("Controller put err!" + ex1);
            }
            logger.debug(srcFile + " " + dstFile + " copyFromLocalFile err!" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace() + ex);
            return false;
        }
        return true;
    }

    public boolean getAndUploadDld(String name, String task_id, String operator_id, String hdfsPath, String localPath) {
        String dst = hdfsPath;
        try {
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(URI.create(dst), conf);
            FileStatus[] fileStatusList = fs.listStatus(new Path(hdfsPath));

            List<String> txtList = new ArrayList<String>();
            List<String> okList = new ArrayList<String>();
            List<String> dldList = new ArrayList<String>();
            for (int i = 0; i < fileStatusList.length; i++) {
                if (fileStatusList[i].isDir()) {
                    continue;
                }
                String fileName = fileStatusList[i].getPath().getName();
                if (fileName != null) {
                    if (fileName.endsWith(".ok")) {
                        okList.add(fileName);
                    } else if (fileName.endsWith(".dld")) {
                        dldList.add(fileName);
                    } else {
                        txtList.add(fileName);
                    }
                }
            }

            List<String> finalList = new ArrayList<String>();
            for (int i = 0; i < txtList.size(); i++) {
                String tmpOkFileName = txtList.get(i) + ".ok";
                for (int j = 0; j < okList.size(); j++) {
                    if (tmpOkFileName.equals(okList.get(j))) {
                        String tmpdldFileName = txtList.get(i) + ".dld";
                        boolean dldflag = false;
                        for (int k = 0; k < dldList.size(); k++) {
                            if (tmpdldFileName.equals(dldList.get(k))) {
                                dldflag = true;//
                                break;
                            }
                        }
                        if (dldflag) {
                            ;//don't download
                        } else {
                            finalList.add(txtList.get(i));
                        }
                        break;
                    }
                }

            }
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent("name:" + name + " total files is " + finalList.size(), task_id + "|" + operator_id + "|input|" + finalList.size()));
            } catch (InterruptedException ex) {
                logger.debug("Controller put err!" + ex);
            }
            for (int i = 0; i < finalList.size(); i++) {
                logger.debug(finalList.get(i) + " begin download!");
                boolean downloadflag = get(hdfsPath, hdfsPath + "/" + finalList.get(i), localPath + "/" + finalList.get(i));//download
                if (downloadflag) {
                    File localokfile = new File(localPath + "/" + finalList.get(i) + ".ok");
                    localokfile.createNewFile();
                    boolean createFileFlag = createDld(hdfsPath, hdfsPath + "/" + finalList.get(i));
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent("name=" + name+" filename =" + finalList.get(i) + ": get success!", task_id + "|" + operator_id + "|output|1"));
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                    logger.debug("name=" + name + ": get success!");

                } else {
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent("name = "+name+" filename =" + finalList.get(i) + ": put err!", ""));
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                    logger.debug("name=" + name + ": get err!");
                }
            }


        } catch (Exception ex) {
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent("Client Controller has finished!", ""));
            } catch (InterruptedException ex1) {
                logger.debug("Controller put err!" + ex1);
            }
            logger.debug(hdfsPath + "getAndUploadDld err!" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace() + ex);
            return false;
        }
        return true;
    }

    public boolean get(String hdfsPath, String srcFile, String dstFile) {
        String dst = hdfsPath;
        try {
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(URI.create(dst), conf);
            fs.copyToLocalFile(new Path(srcFile), new Path(dstFile));
        } catch (Exception ex) {
            logger.debug(srcFile + " download err!" + ex);
            return false;
        }

        return true;
    }

    public boolean createDld(String hdfsPath, String srcFile) {
        String dst = hdfsPath;
        try {
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(URI.create(dst), conf);
            fs.createNewFile(new Path(srcFile + ".dld"));
        } catch (Exception ex) {
            logger.debug(srcFile + " createDld err!" + ex);
            return false;
        }

        return true;
    }
}
