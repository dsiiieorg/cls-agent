/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.po.MessagePo;
import cn.ac.iie.cls.agent.tools.AvroClient;
import cn.ac.iie.cls.agent.tools.AvroUtils;

import cn.ac.iie.cls.agent.tools.PropsFiles;
import cn.ac.iie.cls.agent.tools.XmlTools;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.ipc.HttpTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.generic.GenericRequestor;
import org.apache.log4j.Logger;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Iterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author root
 */
public class JettyServerThread implements Runnable {

    private static Logger logger = Logger.getLogger(JettyServerThread.class);
    String name;
    private String xml;

    @Override
    public void run() {
        String flagStr = "1";
        //String xml = "";
        //xml = pRequestContent;
        if (xml.equals("")) {
            flagStr = "0";
            logger.debug("xml is empty!");
            //return flagStr;
            //return "xml is empty!";
        }
        String name = "";
        name = XmlTools.getValueFromStrDGText(xml, "name");
        int timeout = 0;
        timeout = Integer.parseInt(XmlTools.getValueFromStrDGText(xml, "timeout"));
        String type = "";
        type = XmlTools.getOperatorAttribute(xml, "class");

        if (type.equals("") || name.equals("") || xml.equals("") || timeout == 0) {
            logger.debug("client thread start err. xml=" + xml + "|name=" + name + "|type=" + type + "|timeout=" + timeout);
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent("receive xml formart is err! xml=" + xml + "|name=" + name + "|type=" + type + "|timeout=" + timeout, ""));
            } catch (InterruptedException ex) {
                logger.debug("Controller put err!" + ex);
            }
            //Controller.sendBuffer.add("receive xml formart is err! xml="+xml+"|name="+name+"|type="+type+"|jarname="+jarname+"|timeout="+timeout);
            logger.debug("xml parameter err!");
            flagStr = "0";
            //return flagStr;
            //return "xml parameter err!";
        }
        //Controller.sendBuffer.add("receive xml:"+xml);
        logger.debug("receive xml:" + xml);
        //check if already start
        String tmpstr = name + "|" + type;
        Iterator<String> itr = Controller.subThreadList.iterator();
        while (itr.hasNext()) {
            if (itr.next().equals(tmpstr)) {
                logger.debug(tmpstr + " has already start! return now!");
                try {
                    Controller.sendBuffer.put(Controller.getObjectFromContent(tmpstr + " has already start! return now!", ""));
                } catch (InterruptedException ex) {
                    logger.debug("Controller put err!" + ex);
                }
                logger.debug(tmpstr + " has already start! return now!");
                flagStr = "0";
                //return flagStr;
                //return tmpstr + " has already start! return now!";
            }
        }
        //start subThread
        if (type.equals("GetherDataFromLocalFS")) {//local
            try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubLocalThread stLocal = new SubLocalThread(name, xml);
                Thread stLocalThread = new Thread(stLocal);
                stLocalThread.start();
                //wait for kill
                stLocalThread.join(timeout * 1000);
                boolean flag = stLocalThread.isAlive();
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stLocalThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start subLocalThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());

            }

        } else if (type.equals("GetherDataFromFTP")) {//ftp
            try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubFtpThread stFtp = new SubFtpThread(name, xml);
                Thread stFtpThread = new Thread(stFtp);
                stFtpThread.start();
                //wait for kill
                stFtpThread.join(timeout * 1000);
                boolean flag = stFtpThread.isAlive();
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stFtpThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start subFtpThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());

            }

        } else if (type.equals("GetherDataFromThrid")) {//third
            try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubThridThread stThrid = new SubThridThread(name, xml);
                Thread stThridThread = new Thread(stThrid);
                stThridThread.start();
                //wait for kill
                stThridThread.join(timeout * 1000);
                boolean flag = stThridThread.isAlive();
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stThridThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start subThridThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());

            }
        } else if (type.equals("GetherDataFromCymru")) {//cymru
            try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubCymruThread stHttp = new SubCymruThread(name, xml);
                Thread stHttpThread = new Thread(stHttp);
                stHttpThread.start();
                //wait for kill
                stHttpThread.join(timeout * 1000);
                boolean flag = stHttpThread.isAlive();
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stHttpThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start subCymruThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());

            }

        } else if (type.equals("GetherDataFromHTTP")) {//http
            try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubHttpThread stHttp = new SubHttpThread(name, xml);
                Thread stHttpThread = new Thread(stHttp);
                stHttpThread.start();
                //wait for kill
                stHttpThread.join(timeout * 1000);
                boolean flag = stHttpThread.isAlive();
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stHttpThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start subhttpThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());

            }

        } else if (type.equals("GetherDataFromHdfs")) {//hdfs
            try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubHdfsThread stHdfs = new SubHdfsThread(name, xml);
                Thread stHdfsThread = new Thread(stHdfs);
                stHdfsThread.start();
                //wait for kill
                stHdfsThread.join(timeout * 1000);
                boolean flag = stHdfsThread.isAlive();
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stHdfsThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start subHdfsThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());

            }

        } else if (type.equals("GetherDataFromEQ")) {//equipment
            try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubEquipmentThread stHdfsquipmentE = new SubEquipmentThread(name, xml);
                Thread stEquipmentThread = new Thread(stHdfsquipmentE);
                stEquipmentThread.start();
                //wait for kill
                stEquipmentThread.join(timeout * 1000);
                boolean flag = stEquipmentThread.isAlive();
                
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stEquipmentThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                } else {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                }
                try {
                    Controller.sendBuffer.put(Controller.getObjectFromContent(name + " equipment process has finished!", ""));
                } catch (Exception ex) {
                    logger.debug("Controller put err!" + ex);
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start subEquipmentThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());

            }

        } else if (type.equals("GetherDataFromDatabase")) {//database
            try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubDatabaseThread stDatabase = new SubDatabaseThread(name, xml);
                Thread stDatabaseThread = new Thread(stDatabase);
                stDatabaseThread.start();
                //wait for kill
                stDatabaseThread.join(timeout * 1000);
                boolean flag = stDatabaseThread.isAlive();
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stDatabaseThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                } else {
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start subDatabaseThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());

            }

        } else if (type.equals("GetherDataFromShadowserver")) {//shadowserver
            try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubShadowserverThread stShadowserver = new SubShadowserverThread(name, xml);
                Thread stShadowserverThread = new Thread(stShadowserver);
                stShadowserverThread.start();
                //wait for kill
                stShadowserverThread.join(timeout * 1000);
                boolean flag = stShadowserverThread.isAlive();
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stShadowserverThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start subShadowserverThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            }

        } else if (type.equals("GetherDataFromEmail")) {//email
            try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubEmailThread stEmail = new SubEmailThread(name, xml);
                Thread stEmailThread = new Thread(stEmail);
                stEmailThread.start();
                //wait for kill
                stEmailThread.join(timeout * 1000);
                boolean flag = stEmailThread.isAlive();
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stEmailThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start subEmailThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            }
            // TODO zmc MDSS judge
        }else if(type.equals("GetherDataFromMDSS")){//MDSS
               try {
                logger.debug(name + " starting!");
                Controller.subThreadList.put(name + "|" + type);
                SubMDSSThread stMdss= new SubMDSSThread(name, xml);
                Thread stMdssThread = new Thread(stMdss);
                stMdssThread.start();
                //wait for kill
                stMdssThread.join(timeout * 1000);
                boolean flag = stMdssThread.isAlive();
                if (flag) {
                    Iterator<String> itr1 = Controller.subThreadList.iterator();
                    while (itr1.hasNext()) {
                        if (itr1.next().equals(name + "|" + type)) {
                            itr1.remove();
                        }
                    }
                    try {
                        Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!", ""));
                        stMdssThread.stop();
                    } catch (InterruptedException ex) {
                        logger.debug("Controller put err!" + ex);
                    }
                } else {
                }

            } catch (Exception ex) {
                flagStr = "0";
                logger.debug("start SubMdssThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            }
        }
        //return flagStr;

    }

    public JettyServerThread(String name, String xml) {
        this.name = name;
        this.xml = xml;
    }

    /**
     * @return the xml
     */
    public String getXml() {
        return xml;
    }

    /**
     * @param xml the xml to set
     */
    public void setXml(String xml) {
        this.xml = xml;
    }
}
