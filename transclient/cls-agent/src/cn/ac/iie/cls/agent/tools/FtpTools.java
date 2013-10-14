/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.subThread.SubFtpThread;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class FtpTools {

    /**
     * Description: 从FTP服务器下载文件
     *
     * @Version1.0 Jul 27, 2008 5:32:36 PM by 崔红保（cuihongbao@d-heaven.com）创建
     * @param url FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName 要下载的文件名
     * @param localPath 下载后保存到本地的路径
     * @return
     */
    private static Logger logger = Logger.getLogger(FtpTools.class);

    public static boolean downFile(String url, int port, String username, String password
              , String remotePath
             , String localPath) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(url, port);
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);//登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                logger.debug(url + "ftp login err!username=" + username + ",password=" + password);
                try {
                    Controller.sendBuffer.put(Controller.getObjectFromContent(url + "ftp login err!username=" + username + ",password=" + password,""));
                } catch (InterruptedException ex) {
                    logger.debug("Controller put err!" + ex);
                }
                return success;
            }
            ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            logger.debug(url + " ftp list size is " + fs.length);
            for (int m = 0; m < fs.length; m++) {
                logger.debug(url + " ftp list[" + m + "]=" + fs[m].getName());
            }
            List<FTPFile> txtNameList = new ArrayList<FTPFile>();
            List<FTPFile> okList = new ArrayList<FTPFile>();
            List<FTPFile> dldList = new ArrayList<FTPFile>();
            List<FTPFile> finalList = new ArrayList<FTPFile>();

            for (FTPFile ff : fs) {
                if (ff.getName().endsWith(".dld")) {
                    dldList.add(ff);
                }
                if (ff.getName().endsWith(".ok")) {
                    okList.add(ff);
                } else {
                    txtNameList.add(ff);
                }
            }

            for (int i = 0; i < txtNameList.size(); i++) {
                String tmpname = txtNameList.get(i).getName() + ".ok";
                for (int j = 0; j < okList.size(); j++) {
                    if (tmpname.equals(okList.get(j).getName())) {
                        boolean flag = false;
                        for (int k = 0; k < dldList.size(); k++) {
                            String tmpdldname = txtNameList.get(i).getName() + ".dld";
                            if (tmpdldname.equals(dldList.get(k).getName())) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            finalList.add(txtNameList.get(i));
                        }

                    }
                }
            }


            for (FTPFile ff : finalList) {
                logger.debug(url + "finalList contains:" + ff.getName());
            }
            for (FTPFile ff : finalList) {
                //if (ff.getName().equals(fileName)) {
                try {
                    File rootDir = new File(localPath);
                    if (!rootDir.exists()) {
                        rootDir.mkdirs();
                    }
                    File localFile = new File(localPath + "/" + ff.getName());
                    OutputStream is = new FileOutputStream(localFile);
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();

                    File localFileOk = new File(localPath + "/" + ff.getName() + ".ok");
                    OutputStream isOk = new FileOutputStream(localFileOk);
                    ftp.retrieveFile(ff.getName() + ".ok", isOk);
                    isOk.close();

                    //upload dld file
                    InputStream dld = new ByteArrayInputStream("ok".getBytes("utf-8"));
                    ftp.storeFile(ff.getName() + ".dld", dld);
                    dld.close();
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(url + " " + ff.getName() + " download sucess!",""));
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                    logger.debug(url + " " + ff.getName() + " download sucess!");
                } catch (Exception ex) {
                    logger.debug(ff.getName() + " download err!" + ex);
                }
                //}
            }

            ftp.logout();
            success = true;
        } catch (IOException e) {
            logger.debug(url + " ftp download err " + e);
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent(url + " ftp download err " + e,""));
            } catch (InterruptedException ex) {
                logger.debug("Controller put err!" + ex);
            }
            //e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        logger.debug(url + "ftp download finish!");
        try {
            Controller.sendBuffer.put(Controller.getObjectFromContent(url + "ftp download finish!",""));
        } catch (InterruptedException ex) {
            logger.debug("Controller put err!" + ex);
        }
        return success;
    }
    /**
     * Description: 向FTP服务器上传文件
     *
     * @Version1.0 Jul 27, 2008 4:31:09 PM by 崔红保（cuihongbao@d-heaven.com）创建
     * @param url FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param path FTP服务器保存目录
     * @param filename 上传到FTP服务器上的文件名
     * @param input 输入流
     * @return 成功返回true，否则返回false
     */
    /*
     * public static boolean uploadFile(FTPClient ftp,String url,int port,String
     * username, String password, String path, String filename, InputStream
     * input) { boolean success = false; FTPClient ftp = new FTPClient(); try {
     * int reply; ftp.connect(url, port);//连接FTP服务器
     * //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器 ftp.login(username,
     * password);//登录 reply = ftp.getReplyCode(); if
     * (!FTPReply.isPositiveCompletion(reply)) { ftp.disconnect(); return
     * success; } ftp.changeWorkingDirectory(path); ftp.storeFile(filename,
     * input);	* input.close(); ftp.logout(); success = true; } catch
     * (IOException e) { e.printStackTrace(); } finally { if (ftp.isConnected())
     * { try { ftp.disconnect(); } catch (IOException ioe) { } } } return
     * success; }
     */
}
