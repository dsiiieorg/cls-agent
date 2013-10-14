/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.subThread.SubFtpThread;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class EmailTools {

    private static Logger logger = Logger.getLogger(EmailTools.class);

    public static boolean downFile(String name, String protocol, String address, String username, String password, String localPath) {
        boolean success = false;
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", protocol);
        props.setProperty("mail.pop3.host", address);
        Session session = Session.getDefaultInstance(props);
        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore(protocol);
            store.connect(username, password);
            //logger.info("连接成功");
            logger.debug(name + " connect success!");
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            //获取总邮件数量
            int totalNum = folder.getMessageCount();
            //logger.info("邮件总数：" + totalNum);
            logger.debug(name + " total mails: " + totalNum);
            //读取本地mailnumber中存储的，已经下载的邮件的序号，即已经下载了到第几封邮件
            int mailNum = readNum(localPath);
            logger.debug(name + " has download num: " + mailNum);

            if (mailNum >= totalNum) {
                logger.debug(name + " none download!");
            } else {
                Message[] messages = folder.getMessages(mailNum + 1, totalNum);
                for (int i = 0; i < messages.length; i++) {
                    MimeMessage mimeMessage = (MimeMessage) messages[i];
                    RecieveOneMail rom = new RecieveOneMail(mimeMessage);

                    //判断是否含有附件
                    boolean flag = rom.isContainAttch(mimeMessage);
                    if (flag) {
                        //下载过滤附件 
                        rom.setAttachPath(localPath);
                        rom.saveAttchMentEmail(mimeMessage);
                    } else {
                        ;
                    }
                    addNum(mailNum + i + 1,localPath);
                }
            }
            success = true;
        } catch (Exception ex) {
            logger.debug(name +" deal email err：  " + ex);
        } finally {
            try {
                if (folder != null) {
                    folder.close(true);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException ex) {
                logger.debug("连接关闭异常" + ex.getMessage());
            }
        }
        return success;
    }

    private static int readNum(String localPath) throws IOException {
        File fileDir = new File(localPath);
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            fileDir.mkdir();
        }
        File file = new File(localPath + "/num.txt");
        if (!file.exists() || fileDir.isDirectory()) {
            file.createNewFile();
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String temp = null;
        StringBuffer sb = new StringBuffer();
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        br.close();
        String str = sb.toString();
        if (str == null || str.equals("")) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("0");
            str = "0";
            //return 0;
        }
        int total = 0;
        try {
            total = Integer.parseInt(str.toString());
        } catch (Exception ex) {
            logger.debug("total num is not a int! ");
            total = 0;
        }
        return total;
    }

    private static void addNum(int num,String localPath) throws IOException {
        File file = new File(localPath + "/num.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file);
        StringBuffer sb = new StringBuffer();
        sb.append(num);
        out.write(sb.toString().getBytes());
        out.close();
    }

    private static void writeURL(String url,String localPath,String name) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mmss");
        String times = sdf.format(new Date());
        File localDir = new File(localPath);
        if(!localDir.exists()||!localDir.isDirectory()){
            localDir.mkdir();
        }
        String path = localPath + "/" + name +"-"+ times + "-httpContent-http-dl.txt";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file, true);
        StringBuffer sb = new StringBuffer();
        sb.append(url + "\n");
        out.write(sb.toString().getBytes());
        out.close();
        File okFile = new File(path+".ok");
        if(!okFile.exists()||okFile.isDirectory()){
            okFile.mkdir();
        }
    }
}
