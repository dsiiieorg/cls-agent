/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class ShadowserverTools {

    private static Logger logger = Logger.getLogger(ShadowserverTools.class);

    public static boolean downFile(String name, String protocol, String address, String username, String password, String localPath) {
        boolean success = false;
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", protocol);
        props.setProperty("mail.pop3.host", address);
        Session session = Session.getDefaultInstance(props);
        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore("pop3");
            //store.connect("shadowserver@cert.org.cn", "woaicert123");
            store.connect(username, password);
            //store.connect(username, password);
            logger.debug(name + " connect success!");
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            //获取总邮件数量
            int totalNum = folder.getMessageCount();
            logger.info("邮件总数：" + totalNum);
            logger.debug(name + " total mails: " + totalNum);
            //读取本地mailnumber中存储的，已经下载的邮件的序号，即已经下载了到第几封邮件
            int mailNum = readNum(localPath);
            logger.debug(name + " has download num: " + mailNum);

            if (mailNum == totalNum) {
                logger.debug(name + " none download!");
            } else if (mailNum > totalNum) {
                addNum(0, localPath);
            } else {
                Message[] messages = folder.getMessages(mailNum + 1, totalNum);
                for (int i = 0; i < messages.length; i++) {
                    MimeMessage mimeMessage = (MimeMessage) messages[i];
                    if(!mimeMessage.getFolder().isOpen()){
                        mimeMessage.getFolder().open(Folder.READ_WRITE);
                    }
                    RecieveOneMail rom = new RecieveOneMail(mimeMessage);

                    //判断是否含有附件
                    boolean flag = rom.isContainAttch(mimeMessage);
                    if (flag) {
                        //下载邮件附件 
                        rom.setAttachPath(localPath);
                        rom.saveAttchMent(mimeMessage);
                    } else {
                        //获得邮件正文内容
                        rom.getMailContent(mimeMessage);
                        String content = rom.getBodyText();
                        if (content.indexOf("http://dl") != -1) {
                            String[] strs = content.split("\r");
                            String url = strs[1].trim();
                            logger.debug(name + " email content's url: " + url);
                            writeURL(url, localPath, name);

                        }
                    }
                    totalNum = mailNum + i + 1;
                    addNum(totalNum, localPath);
                }
            }
            //判断邮件的数量是否超过900，超过-1
            if (totalNum > 900) {
                Message message = folder.getMessage(1);
                message.setFlag(Flags.Flag.DELETED, true);
                message.saveChanges();
                totalNum--;
                addNum(totalNum, localPath);
            }
            success = true;
        } catch (Exception ex) {
            logger.debug(name + " deal email err：  " + ex);
        } finally {
            try {
                if (folder != null) {
                    folder.close(true);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException ex) {
                logger.debug("closed error" + ex.getMessage());
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

    private static void addNum(int num, String localPath) throws IOException {
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

//    private static void writeURL(String url,String localPath,String name) throws IOException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mmss");
//        String times = sdf.format(new Date());
//        File localDir = new File(localPath);
//        if(!localDir.exists()||!localDir.isDirectory()){
//            localDir.mkdir();
//        }
//        String path = localPath + "/" + name +"-"+ times + "-httpContent-http-dl.txt";
//        File file = new File(path);
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        FileOutputStream out = new FileOutputStream(file, true);
//        StringBuffer sb = new StringBuffer();
//        sb.append(url + "\n");
//        out.write(sb.toString().getBytes());
//        out.close();
//        File okFile = new File(path+".ok");
//        if(!okFile.exists()||okFile.isDirectory()){
//            okFile.createNewFile();
//        }
//    }
    private static void writeURL(String urlStr, String localPath, String name) throws IOException {
        String fileName = "";
        Pattern p = Pattern.compile("filename=(.*)");
        URL url = new URL(urlStr);
        URLConnection uc = url.openConnection();
        Map<String, List<String>> map = uc.getHeaderFields();
        List list = map.get("Content-Disposition");
        for (int i = 0; i < list.size(); i++) {
            Matcher mt = p.matcher(list.get(i).toString().trim());
            while (mt.find()) {
                fileName = mt.group(0);
                break;
            }
        }
        logger.debug(" email content's url fileName: " + fileName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mmss");
        String times = sdf.format(new Date());
        File localDir = new File(localPath);
        if (!localDir.exists() || !localDir.isDirectory()) {
            localDir.mkdir();
        }
        fileName = fileName.substring(9);
        String path = localPath + "/" + name + "-" + times + "-" + fileName;
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }


        InputStream ins = uc.getInputStream();
        FileOutputStream fos = new FileOutputStream(path);
        byte[] buffer = new byte[1024];
        int length=0;
        while((length=ins.read(buffer))!=-1){
            fos.write(buffer, 0, length);
        }
        
        ins.close();
        fos.flush();
        fos.close();

        File fileOk = new File(path + ".ok");
        if (!fileOk.exists()) {
            fileOk.createNewFile();
        }
    }
}
