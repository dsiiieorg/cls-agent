/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class ProcFile {

    private static Logger logger = Logger.getLogger(ProcFile.class);

    public ProcFile() {
    }

    public static boolean splitFile(String path, int lineLength, String fileEncoding) {
        if (path.contains(".part")) {
            logger.debug("this is a part file,don't need split! " + path);
            return true;
        }
        boolean flag = false;
        int index = path.lastIndexOf(".");

        File file = new File(path);
        FileInputStream fis = null;
        BufferedReader reader = null;
        DataInputStream dis = null;
        boolean inFlag = false;
        try {
            fis = new FileInputStream(file);
            dis = new DataInputStream(fis);
            reader = new BufferedReader(new InputStreamReader(dis, fileEncoding), 5 * 1024);
            int readLineNum = 0;
            int processCount = 0;
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                //readLineNum++;
                //sb.append(line + "\r\n");
                if (readLineNum == 0) {
                    ;
                } else {
                    sb.append("\r\n" + line);
                }
                readLineNum++;
                if (readLineNum % lineLength == 0) {
                    processCount++;
                    String filename = path.substring(0, index) + ".part" + processCount + "." + path.substring(index + 1, path.length());
                    File file1 = new File(filename);
                    if (!file1.exists()) {
                        file1.createNewFile();
                    }
                    Writer output = new OutputStreamWriter(new FileOutputStream(filename), "utf-8");
                    // BufferedWriter output = new BufferedWriter(new FileWriter(filename));
                    output.write(sb.toString());
                    output.close();
                    file1 = new File(filename + ".ok");
                    if (!file1.exists()) {
                        file1.createNewFile();
                    }
                    sb.delete(0, sb.length());
                    System.gc();
                    readLineNum = 0;
                    inFlag = true;
                }
            }

            if (!inFlag) {
                logger.debug(path + " don't need split!");
                return true;
            } else {
                processCount++;
                String filename = path.substring(0, index) + ".part" + processCount + "." + path.substring(index + 1, path.length());
                File file2 = new File(filename);
                if (!file2.exists()) {
                    file2.createNewFile();
                }
                Writer output = new OutputStreamWriter(new FileOutputStream(filename), "utf-8");
                //  BufferedWriter output = new BufferedWriter(new FileWriter(filename));
                output.write(sb.toString());
                output.close();
                file2 = new File(filename + ".ok");
                if (!file2.exists()) {
                    file2.createNewFile();
                }
                sb.delete(0, sb.length());
                System.gc();
                readLineNum = 0;
                reader.close();
                flag = true;

                //delete source file
                file.delete();
                File okFile = new File(path + ".ok");
                okFile.delete();
            }

        } catch (IOException e) {
            logger.debug(path + " split err!" + e);;//log
            flag = false;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    logger.debug(path + " fis close err!" + ex);;//log
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException ex) {
                    logger.debug(path + " dis close err!" + ex);;//log
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.debug(path + " read close err!" + e);;//log
                    //e.printStackTrace();
                }
            }

        }
        return flag;
    }
}
