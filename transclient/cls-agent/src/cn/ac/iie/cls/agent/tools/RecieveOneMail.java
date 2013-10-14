
package cn.ac.iie.cls.agent.tools;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import org.apache.log4j.Logger;

/**
 *
 * @author zmc
 */
public class RecieveOneMail {

    private MimeMessage mimeMessage;
    private String saveAttachPath;
    private StringBuffer bodytext = new StringBuffer();
    static Logger logger = Logger.getLogger(RecieveOneMail.class);

    public RecieveOneMail(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }

    public void setMimeMessage(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }

    /**
     * 获取邮件正文内容
     */
    public String getBodyText() {

        return bodytext.toString();
    }

    /**
     * 解析邮件，将得到的邮件内容保存到一个stringBuffer对象中，解析邮件 主要根据MimeType的不同执行不同的操作，一步一步的解析
     */
    public void getMailContent(Part part) {
        try {
            String contentType = part.getContentType();
            if (part.isMimeType("text/plain")) {
                bodytext.append((String) part.getContent());
            } else if (part.isMimeType("text/html")) {
                bodytext.append((String) part.getContent());
            } else if (part.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) part.getContent();
                int count = multipart.getCount();
                for (int i = 0; i < count; i++) {
                    getMailContent(multipart.getBodyPart(i));
                }
            }
        } catch (Exception e) {
            logger.debug(e);
        }
    }

    /**
     * 获得此邮件的MessageID
     */
    public String getMessageId() throws MessagingException {
        return mimeMessage.getMessageID();
    }

    /**
     * 判断此邮件是否包含附件
     */
    public boolean isContainAttch(Part part) throws MessagingException, IOException {
        boolean flag = false;
        String contentType = part.getContentType();
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodypart = multipart.getBodyPart(i);
                String dispostion = bodypart.getDisposition();
                if ((dispostion != null) && (dispostion.equals(Part.ATTACHMENT) || dispostion.equals(Part.INLINE))) {
                    flag = true;
                } else if (bodypart.isMimeType("multipart/*")) {
                    flag = isContainAttch(bodypart);
                } else {
                    String conType = bodypart.getContentType();
                    if (conType.toLowerCase().indexOf("appliaction") != -1) {
                        flag = true;
                    }
                    if (conType.toLowerCase().indexOf("name") != -1) {
                        flag = true;
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttch((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 设置附件存放路径
     */
    public void setAttachPath(String attachPath) {
        this.saveAttachPath = attachPath;
    }

    /**
     * 获得附件存放路径
     */
    public String getAttachPath() {
        return saveAttachPath;
    }

    /**
     * 保存附件
     */
    public void saveAttchMent(Part part) throws Exception {
        String filename = "";
        try {
            if (part.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) part.getContent();

                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart mpart = mp.getBodyPart(i);
                    String disposition = mpart.getDisposition();
                    if (Part.ATTACHMENT.equalsIgnoreCase(disposition)) {
                        filename = mpart.getFileName();
                        if (filename.endsWith("?=")) {
                            filename = MimeUtility.decodeWord(filename);
                        }
                        if ((filename.indexOf("csv") != -1) || (filename.indexOf("zip") != -1)) {
                            saveFile(filename, mpart.getInputStream());
                        }

                    } else if (mpart.isMimeType("multipart/*")) {
                        saveAttchMent(mpart);
                    }
                }
            }
        } catch (Exception ex) {
            logger.debug(saveAttachPath+" download err!"+ex);
        }
    }
     public void saveAttchMentEmail(Part part) throws Exception {
        String filename = "";
        try {
            if (part.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) part.getContent();

                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart mpart = mp.getBodyPart(i);
                    String disposition = mpart.getDisposition();
                    if (Part.ATTACHMENT.equalsIgnoreCase(disposition)) {
                        filename = mpart.getFileName();
                        if (filename.endsWith("?=")) {
                            filename = MimeUtility.decodeWord(filename);
                        }
                        //if ((filename.indexOf("csv") != -1) || (filename.indexOf("zip") != -1)) {
                            saveFileEmail(filename, mpart.getInputStream());
                       // }

                    } else if (mpart.isMimeType("multipart/*")) {
                        saveAttchMent(mpart);
                    }
                }
            }
        } catch (Exception ex) {
            logger.debug(saveAttachPath+" download err!"+ex);
        }
    }

    /**
     * 保存附件到指定目录里
     */
    public void saveFile(String fileName, InputStream in) throws Exception {
        String storeDir = getAttachPath();
        File sDir = new File(storeDir);
        if(!sDir.exists()||!sDir.isDirectory()){
            sDir.mkdir();
        }
        File storeFile = new File(storeDir+"/" + fileName);
//        for (int i = 0; storeFile.exists(); i++) {
//            String newName = storeDir + i + "_" + fileName;
//            storeFile = new File(newName);
//        }
        if (!storeFile.exists()) {
            storeFile.createNewFile();
        }
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(storeFile));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
            if (storeFile.toString().contains("zip")) {
                unZip(storeFile, getAttachPath());
                storeFile.delete();
            } else {
                File storeFileOk = new File(storeDir +"/"+ fileName+".ok");
                if(!storeFileOk.exists()||storeFileOk.isDirectory()){
                    storeFileOk.createNewFile();
                }
                //logger.debug("csv path:" + storeFile.toString());
            }
        } catch (Exception ex) {
            logger.debug(fileName+" save err!"+ex);
        } finally {
            bos.close();
            bis.close();
        }
    }
 public void saveFileEmail(String fileName, InputStream in) throws Exception {
        String storeDir = getAttachPath();
        File sDir = new File(storeDir);
        if(!sDir.exists()||!sDir.isDirectory()){
            sDir.mkdir();
        }
        File storeFile = new File(storeDir+"/" + fileName);
//        for (int i = 0; storeFile.exists(); i++) {
//            String newName = storeDir + i + "_" + fileName;
//            storeFile = new File(newName);
//        }
        if (!storeFile.exists()) {
            storeFile.createNewFile();
        }
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(storeFile));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
//            if (storeFile.toString().contains("zip")) {
//                unZip(storeFile, getAttachPath());
//                storeFile.delete();
//            } else {
                File storeFileOk = new File(storeDir +"/"+ fileName+".ok");
                if(!storeFileOk.exists()||storeFileOk.isDirectory()){
                    storeFileOk.createNewFile();
                }
                //logger.debug("csv path:" + storeFile.toString());
          //  }
        } catch (Exception ex) {
            logger.debug(fileName+" save err!"+ex);
        } finally {
            bos.close();
            bis.close();
        }
    }
    //解压zip
    private static void unZip(File zipFile, String descDir) throws Exception {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + "/"+zipEntryName).replaceAll("\\*", "/");
            logger.debug("zip unzip path：" + outPath);
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (new File(outPath).isDirectory()) {
                continue;
            }
            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            
            //create .ok
            File okFile = new File(outPath+".ok");
            if(!okFile.exists()||okFile.isDirectory()){
                okFile.createNewFile();
            }
            in.close();
            out.close();
        }
    }
}
