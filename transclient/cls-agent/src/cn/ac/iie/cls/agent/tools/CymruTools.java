/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.subThread.SubFtpThread;
import java.io.*;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class CymruTools {

    private static Logger logger = Logger.getLogger(CymruTools.class);
    public static List<String> infect_List = new ArrayList<String>();

    public boolean downFile(String httpFilePath, String outputFilePath, final String username, final String password, String dateTime, String regEx) {
        //get file list
        infect_List.clear();
        String httpRootPath = "";
        httpRootPath = httpFilePath.substring(0, httpFilePath.lastIndexOf("/")) + "/";

        String outputRootPath = "";
        outputRootPath = outputFilePath.substring(0, outputFilePath.lastIndexOf("/")) + "/" + "list.html";

        //get file
        boolean flag = false;
        try {
            URL url = new URL(httpFilePath);
            URL urlroot = new URL(httpRootPath);
            //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            HttpURLConnection connroot = (HttpURLConnection) urlroot.openConnection();
            Authenticator.setDefault(new Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });
            //DataInputStream in = new DataInputStream(conn.getInputStream());
            DataInputStream inroot = new DataInputStream(connroot.getInputStream());
            //DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFilePath));
            DataOutputStream outroot = new DataOutputStream(new FileOutputStream(outputRootPath));

            //download list.html
            int bytereadroot = 0;
            byte[] bufferroot = new byte[10240];
            while ((bytereadroot = inroot.read(bufferroot)) != -1) {
                outroot.write(bufferroot, 0, bytereadroot);
            }

            //time compare
            //String regEx = ".*ddos-rsv2.txt</a> *([^ ]*) *([^ ]*).*$";
            File list = new File(outputRootPath);
            if (compareTime(list, dateTime, regEx, outputFilePath)) {
                ;
            } else {
                logger.debug(httpFilePath + " compareTime() false!");
                return false;
            }
            inroot.close();
            outroot.close();

            if (httpFilePath.contains("infected_")) {
                for (int i = 0; i < infect_List.size(); i++) {
                    url = new URL(httpRootPath + infect_List.get(i));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    DataInputStream in = new DataInputStream(conn.getInputStream());
                    String fileName = outputFilePath.substring(0, outputFilePath.lastIndexOf("/") + 1) + infect_List.get(i);
                    DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
                    int byteread = 0;
                    byte[] buffer = new byte[10240];
                    while ((byteread = in.read(buffer)) != -1) {
                        out.write(buffer, 0, byteread);
                    }
                    out.close();
                    in.close();



                    //clear #line
                    File file = new File(outputFilePath);
                    clear(file);
                    File okfile = new File(fileName + ".ok");
                    try {
                        okfile.createNewFile();
                    } catch (IOException ex) {
                        logger.debug(fileName + " create ok file err!" + ex);
                    }

                    logger.debug(httpRootPath + infect_List.get(i) + " download success!");
                }
                return true;
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            DataInputStream in = new DataInputStream(conn.getInputStream());
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFilePath));
            int byteread = 0;
            byte[] buffer = new byte[10240];
            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            out.close();
            in.close();



            //clear #line
            File file = new File(outputFilePath);
            clear(file);
            flag = true;
        } catch (FileNotFoundException e) {
            flag = false;
        } catch (IOException ex) {
            logger.debug(httpFilePath + "The file read and  writen error");
            flag = false;
        } finally {
        }
        return flag;
    }
    //time matcha

    public boolean compareTime(File list, String dateTime, String regEx, String outputFilePath) {
        if (list == null || !list.exists()) {
            logger.debug("no list create.");
            return false;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(list));
            String temp = null;
            StringBuffer sb = new StringBuffer();
            temp = br.readLine();
            Pattern p = Pattern.compile(regEx);
            List<String> _tmpList = new ArrayList<String>();
            String line = "";
            while (temp != null) {

                Matcher mt = p.matcher(temp);
                if (mt.find()) {
                    line = mt.group();
                    if (regEx.contains("infected_")) {
                        _tmpList.add(line);
                    } else {
                        break;//System.out.println(line);
                    }

                }
                //sb.append(temp);
                temp = br.readLine();
            }
            br.close();
            if (regEx.contains("infected_")) {
                List<Integer> dateNumList = new ArrayList<Integer>();
                for (int i = 0; i < _tmpList.size(); i++) {
                    line = _tmpList.get(i);
                    if (!line.equals("")) {
                        String[] dateList = line.split(" ");
                        List<String> _dateList = new ArrayList<String>();
                        for (int ii = 0; ii < dateList.length; ii++) {
                            if (!dateList[ii].equals("")) {
                                _dateList.add(dateList[ii]);
                            }
                        }
                        if (_dateList.size() > 2) {
                            String _date = _dateList.get(_dateList.size() - 4);
                            String infected_name = "";
                            int indexbegin = _date.indexOf("\"");
                            int indexend = _date.lastIndexOf("\"");
                            infected_name = _date.substring(indexbegin + 1, indexend);
                            int dateNum = 0;
                            try {
                                dateNum = Integer.parseInt(infected_name.substring(9, 17));
                            } catch (Exception ex) {
                                continue;
                            }
                            //System.out.println(_date);
                            if (dateNum <= Integer.parseInt(dateTime)) {
                                logger.debug("match ok!don't downlod!");
                                continue;
                            } else {
                                logger.debug("don't match!downlod!");
                                infect_List.add(infected_name);
                                dateNumList.add(dateNum);
                            }
                        } else {
                            logger.debug("match err!length<=2!");
                            continue;
                        }
                    } else {
                        logger.debug("match err!");
                        continue;
                    }
                }
                if (infect_List.size() > 0) {
                    try {
                        File file = new File(outputFilePath + ".tm");
                        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                        int max = 0;
                        for (int j = 0; j < dateNumList.size(); j++) {
                            if (dateNumList.get(j) > max) {
                                max = dateNumList.get(j);
                            }

                        }
                        bw.write("" + max);
                        bw.close();
                    } catch (Exception ex) {
                        logger.debug("timeFile create err! " + ex);
                    }
                    return true;
                } else {
                    return false;
                }

            }
            if (!line.equals("")) {
                String[] dateList = line.split(" ");
                List<String> _dateList = new ArrayList<String>();
                for (int i = 0; i < dateList.length; i++) {
                    if (!dateList[i].equals("")) {
                        _dateList.add(dateList[i]);
                    }
                }
                if (_dateList.size() > 2) {
                    String _date = _dateList.get(_dateList.size() - 3);
                    //System.out.println(_date);
                    if (_date.equals(dateTime)) {
                        logger.debug("match ok!don't downlod!");
                        return false;
                    } else {
                        File file = new File(outputFilePath + ".tm");
                        if (!file.exists()) {
                            ;
                        } else {
                            try {
                                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                                bw.write(_date);
                                bw.close();
                            } catch (Exception ex) {
                                logger.debug("timeFile create err! " + ex);
                            }
                        }

                        logger.debug("don't match!downlod!");
                        return true;
                    }
                } else {
                    logger.debug("match err!length<=2!");
                    return false;
                }
            } else {
                logger.debug("match err!");
                return false;
            }
        } catch (Exception ex) {
            logger.debug("readLine err!" + ex);
        }
        return false;
    }

    public void clear(File file) {
        if (file == null || !file.exists()) {
            logger.debug("no file create.");
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String temp = null;
            StringBuffer sb = new StringBuffer();
            temp = br.readLine();
            while (temp != null) {
                if (temp.startsWith("#")) {
                    temp = br.readLine();
                    continue;
                }
                sb.append(temp + "\r\n");
                temp = br.readLine();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(sb.toString());
            br.close();
            bw.close();
        } catch (Exception ex) {
            logger.debug("readLine err!" + ex);
        }

    }
}
