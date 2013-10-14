/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class PropsFiles {

    public static String getValue(String key) throws IOException {
        Properties propeties = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream("/usr/iie/transclient/properties/cls_agent.properties"));
        propeties.load(in);
        String value = propeties.getProperty(key);
        in.close();
        return value;
    }

    public static void update(String key, String value) throws FileNotFoundException, IOException {
        File file = new File("/usr/iie/transclient/properties/cls_agent.properties");
        FileReader fr = new FileReader(file);//创建文件输入流
        BufferedReader in = new BufferedReader(fr);//包装文件输入流，可整行读取
        String line = "";
        while ((line = in.readLine()) != null) {
            String oldkey = line.split("=")[0];
            if (oldkey.equals(key)) {
                break;
            }
        }
        if (!line.equals("")) {
            replace(line, key + "=" + value);
        }
//        if (key.contains(":")) {
//            key = key.replace(":", "\\:");
//        } else if (key.contains("=")) {
//            key = key.replace(":", "\\=");
//        }
//        if (value.contains(":")) {
//            value = value.replace(":", "\\:");
//        } else if (value.contains("=")) {
//            value = value.replace(":", "\\=");
//        }
//        Properties propeties = new Properties();
//        OutputStream out;
//        InputStream in = new BufferedInputStream(new FileInputStream("/usr/iie/transclient/properties/controller.properties"));
//        propeties.load(in);
//        in.close();
//
//        out = new BufferedOutputStream(new FileOutputStream("/usr/iie/transclient/properties/controller.properties"));
//        propeties.setProperty(key, value);
//        propeties.store(out, new Date().toString());
//        out.close();
    }

    public static void replace(String oldStr, String replaceStr) throws FileNotFoundException, IOException {
        String temp = "";

        File file = new File("/usr/iie/transclient/properties/cls_agent.properties");
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer buf = new StringBuffer();

        // 保存该行前面的内容
        for (int j = 1; (temp = br.readLine()) != null
                && !temp.equals(oldStr); j++) {
            buf = buf.append(temp);
            buf = buf.append(System.getProperty("line.separator"));
        }

        // 将内容插入
        buf = buf.append(replaceStr);

        // 保存该行后面的内容
        while ((temp = br.readLine()) != null) {
            buf = buf.append(System.getProperty("line.separator"));
            buf = buf.append(temp);
        }

        br.close();
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter pw = new PrintWriter(fos);
        pw.write(buf.toString().toCharArray());
        pw.flush();
        pw.close();

    }
}
