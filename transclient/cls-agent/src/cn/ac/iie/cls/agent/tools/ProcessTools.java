/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.subThread.KillThread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class ProcessTools {

    private static Logger logger = Logger.getLogger(ProcessTools.class);

    //kill process
    public static boolean kill(String name) {
        boolean flag = false;
        Process process = null;
        BufferedReader br = null;
        try {
            process = Runtime.getRuntime().exec("ps -ef");
            br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()), 1024);

            String line = null;
            Pattern pattern = Pattern.compile("\\S*\\s*([0-9]*).*");
            Matcher matcher = null;
            while ((line = br.readLine()) != null) {
                logger.debug("all process " + line);
                if (!line.contains(name)) {
                    continue;
                }
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    flag = true;
                    Runtime.getRuntime().exec("kill -9 " + matcher.group(1));
                    //pidList.add(matcher.group(1));
                    logger.debug(name + " process pid! " + matcher.group(1) + " has been killed");
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " thread overtime! kill it! ",""));
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                    //Controller.sendBuffer.add(name+" thread overtime! kill it! " );
                }
            }
            if (!flag) {
                logger.debug(name + " process pid! " + matcher.group(1) + " has been killed");
            }
        } catch (IOException e) {
            logger.debug("getID error! " + e.getLocalizedMessage() + "|" + e.getMessage() + "|" + e.getStackTrace());
            return false;
        } finally {
        }
        return true;
    }

    //get process name from Id
    public static List<String> getConfigFileNameListFromId(String id) {
        List<String> configFileNameList = new ArrayList<String>();
        Process process = null;
        BufferedReader br = null;
        try {
            process = Runtime.getRuntime().exec("ps -ef");
            br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()), 1024);

            String line = null;
            Pattern pattern = Pattern.compile("\\S*\\s*([0-9]*).*");
            Matcher matcher = null;
            while ((line = br.readLine()) != null) {
                logger.debug("all process " + line);
                if (!line.contains(id)) {
                    continue;
                }
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String[] tmpNameArray = line.split(" ");
                    configFileNameList.add(tmpNameArray[tmpNameArray.length - 1]);
                    logger.debug(" process configFileName ! " + tmpNameArray[tmpNameArray.length - 1]);
                }
            }
        } catch (IOException e) {
            logger.debug("getName error! " + e.getLocalizedMessage() + "|" + e.getMessage() + "|" + e.getStackTrace());
        } finally {
        }
        return configFileNameList;
    }

    //get process name from name
    public static List<String> getNameListFromName(String name) {
        List<String> nameList = new ArrayList<String>();
        Process process = null;
        BufferedReader br = null;
        try {
            process = Runtime.getRuntime().exec("ps -ef");
            br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()), 1024);

            String line = null;
            Pattern pattern = Pattern.compile("\\S*\\s*([0-9]*).*");
            Matcher matcher = null;
            while ((line = br.readLine()) != null) {
                logger.debug("all process " + line);
                if (!line.contains(name)) {
                    continue;
                }
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    nameList.add(matcher.group(1));
                    logger.debug(name + " process name! " + matcher.group(1));
                }
            }
        } catch (IOException e) {
            logger.debug("getName error! " + e.getLocalizedMessage() + "|" + e.getMessage() + "|" + e.getStackTrace());
        } finally {
        }
        return nameList;
    }

    //get process id list
    public static List<String> getIDList(String keyWord) {
        List<String> pidList = new ArrayList<String>();
        Process process = null;
        BufferedReader br = null;
        try {
            process = Runtime.getRuntime().exec("ps -ef");
            br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()), 1024);

            String line = null;
            Pattern pattern = Pattern.compile("\\S*\\s*([0-9]*).*");
            Matcher matcher = null;
            while ((line = br.readLine()) != null) {
                logger.debug("all process " + line);
                if (!line.contains(keyWord)) {
                    continue;
                }
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    pidList.add(matcher.group(1));
                    logger.debug(keyWord + " process pid! " + matcher.group(1));
                }
            }
        } catch (IOException e) {
            logger.debug("getID error! " + e.getLocalizedMessage() + "|" + e.getMessage() + "|" + e.getStackTrace());
        } finally {
        }
        return pidList;

    }
    //get process time 

    public static int getProcessTime(String pid) {
        BufferedReader in = null;
        String str = null;
        int time = 0;
        String etime = "";
        String[] strArray = null;
        int count = 0;
        String[] timeArray = null;
        Runtime rt = Runtime.getRuntime();
        Process p;

        try {
            p = rt.exec("ps -o pid,etime -p " + pid);
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((str = in.readLine()) != null) {
                if (count == 1) {
                    strArray = str.trim().split("\\s+");
                    etime = strArray[1];
                    timeArray = etime.trim().split(":");
                    if (timeArray.length == 3) {
                        time = Integer.parseInt(timeArray[0]) * 3600 + Integer.parseInt(timeArray[1]) * 60 + Integer.parseInt(timeArray[2]);
                    } else if (timeArray.length == 2) {
                        time = Integer.parseInt(timeArray[0]) * 60 + Integer.parseInt(timeArray[1]);
                    } else {
                        time = Integer.parseInt(timeArray[0]);
                    }
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return time;
    }
    //start process

    public static boolean startProcess(String str) throws IOException {
        Runtime.getRuntime().exec(str);
        return true;
    }
    //kill process

    public static boolean killProcess(String id) throws IOException {
        Runtime.getRuntime().exec("kill -9 " + id);
        return true;
    }
}
