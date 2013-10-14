/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

/**
 *
 * @author root
 */
import cn.ac.iie.cls.agent.subThread.SubAvroServerThread;
import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.po.MessagePo;
import cn.ac.iie.cls.agent.po.TimePo;
import java.util.*;
import org.apache.avro.Protocol;
import org.apache.avro.Protocol.Message;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.ipc.HttpServer;
import org.apache.avro.ipc.generic.GenericResponder;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.nio.SelectChannelConnector;

public class AvroServer extends GenericResponder {

    //public static int iii=0;
    private static Logger logger = Logger.getLogger(AvroServer.class);
    private Protocol protocol = null;
    private int port;

    public AvroServer(Protocol protocol, int port) {
        super(protocol);
        logger.debug("avroServer port:" + port + " protocol:" + protocol.toString());
        this.protocol = protocol;
        this.port = port;
    }

    public Object respond(Message message, Object request) throws Exception {
        GenericRecord req = (GenericRecord) request;
        GenericRecord msg = (GenericRecord) (req.get("message"));

        String id = msg.get("id").toString();
        String xml = msg.get("xml").toString();
        String content = msg.get("content").toString();
        
        
        try {
            logger.debug("new subAvroServerThread thread.");

            SubAvroServerThread subAvroServer = new SubAvroServerThread("SubAvroServerThread",id,xml,content);
            Thread subAvroServerThread = new Thread(subAvroServer);
            subAvroServerThread.start();
            //logger.debug("listern thread start ok.");
        } catch (Exception ex) {
            logger.debug("start listern error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            return msg;
        }
        
        
//        if (id.equals("1")) {
//            String xml = "";
//            xml = msg.get("xml").toString();//sysController's xml
//
//            String name = "";
//            name = XmlTools.getValueFromStr(xml, "_name");
//            int timeout = 0;
//            timeout = Integer.parseInt(XmlTools.getValueFromStr(xml, "_timeout"));
//            String type = "";
//            type = XmlTools.getValueFromStr(xml, "_type");
//            String jarname = "";
//            //if(type.equals("local")){
//            jarname = "transclient_sub_hippo.jar";
//            //}else{
//            //
//            //}
//            if (jarname.equals("") || type.equals("") || name.equals("") || xml.equals("") || timeout == 0) {
//                logger.debug("client thread start err. xml=" + xml + "|name=" + name + "|type=" + type + "|jarname=" + jarname + "|timeout=" + timeout);
//                try {
//                    Controller.sendBuffer.put(Controller.getObjectFromContent("receive xml formart is err! xml=" + xml + "|name=" + name + "|type=" + type + "|jarname=" + jarname + "|timeout=" + timeout));
//                } catch (InterruptedException ex) {
//                    logger.debug("Controller put err!" + ex);
//                }
//                //Controller.sendBuffer.add("receive xml formart is err! xml="+xml+"|name="+name+"|type="+type+"|jarname="+jarname+"|timeout="+timeout);
//                return msg;
//            }
//            //Controller.sendBuffer.add("receive xml:"+xml);
//            logger.debug("receive xml:" + xml);
//            //check if already start
//            String tmpstr = name + "|" + type;
//            Iterator<String> itr = Controller.subThreadList.iterator();
//            while (itr.hasNext()) {
//                if (itr.next().equals(tmpstr)) {
//                    logger.debug(tmpstr + " has already start! return now!");
//                    try {
//                        Controller.sendBuffer.put(Controller.getObjectFromContent(tmpstr + " has already start! return now!"));
//                    } catch (InterruptedException ex) {
//                        logger.debug("Controller put err!" + ex);
//                    }
//                    return msg;
//                }
//            }
//            //start subThread
//            if (type.equals("local")) {//local
//                try {
//                    logger.debug(name + " starting!");
//                    Controller.subThreadList.put(name + "|" + type);
//                    SubLocalThread stLocal = new SubLocalThread(name, xml);
//                    Thread stLocalThread = new Thread(stLocal);
//                    stLocalThread.start();
//                    //wait for kill
//                    stLocalThread.join(timeout * 1000);
//                    boolean flag = stLocalThread.isAlive();
//                    if (flag) {
//                        Iterator<String> itr1 = Controller.subThreadList.iterator();
//                        while (itr1.hasNext()) {
//                            if (itr1.next().equals(name + "|" + type)) {
//                                itr1.remove();
//                            }
//                        }
//                        try {
//                            Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!"));
//                            stLocalThread.stop();
//                        } catch (InterruptedException ex) {
//                            logger.debug("Controller put err!" + ex);
//                        }
//                    }
//
//                } catch (Exception ex) {
//                    logger.debug("start subLocalThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
//
//                }
//
//            } else if (type.equals("database")) {
//                try {
//                    logger.debug(name + " starting!");
//                    Controller.subThreadList.put(name + "|" + type);
//                    SubDatabaseThread stDatabase = new SubDatabaseThread(name, xml);
//                    Thread stDatabaseThread = new Thread(stDatabase);
//                    stDatabaseThread.start();
//                    stDatabaseThread.join(timeout * 1000);
//                    boolean flag = stDatabaseThread.isAlive();
//                    if (flag) {
//                        Iterator<String> itr1 = Controller.subThreadList.iterator();
//                        while (itr1.hasNext()) {
//                            if (itr1.next().equals(name + "|" + type)) {
//                                itr1.remove();
//                            }
//                        }
//                        try {
//                            Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!"));
//                            stDatabaseThread.stop();
//                        } catch (InterruptedException ex) {
//                            logger.debug("Controller put err!" + ex);
//                        }
//                    }
//
//                } catch (Exception ex) {
//                    logger.debug("start SubDatabaseThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
//
//                };
//            } else if (type.equals("ftp")) {//ftp
//                try {
//                    logger.debug(name + " starting!");
//                    Controller.subThreadList.put(name + "|" + type);
//                    SubFtpThread stFtp = new SubFtpThread(name, xml);
//                    Thread stFtpThread = new Thread(stFtp);
//                    stFtpThread.start();
//                    //wait for kill
//                    stFtpThread.join(timeout * 1000);
//                    boolean flag = stFtpThread.isAlive();
//                    if (flag) {
//                        Iterator<String> itr1 = Controller.subThreadList.iterator();
//                        while (itr1.hasNext()) {
//                            if (itr1.next().equals(name + "|" + type)) {
//                                itr1.remove();
//                            }
//                        }
//                        try {
//                            Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!"));
//                            stFtpThread.stop();
//                        } catch (InterruptedException ex) {
//                            logger.debug("Controller put err!" + ex);
//                        }
//                    }
//
//                } catch (Exception ex) {
//                    logger.debug("start subFtpThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
//
//                }
//
//            } else if (type.equals("thrid")) {//ftp
//                try {
//                    logger.debug(name + " starting!");
//                    Controller.subThreadList.put(name + "|" + type);
//                    SubThridThread stThrid = new SubThridThread(name, xml);
//                    Thread stThridThread = new Thread(stThrid);
//                    stThridThread.start();
//                    //wait for kill
//                    stThridThread.join(timeout * 1000);
//                    boolean flag = stThridThread.isAlive();
//                    if (flag) {
//                        Iterator<String> itr1 = Controller.subThreadList.iterator();
//                        while (itr1.hasNext()) {
//                            if (itr1.next().equals(name + "|" + type)) {
//                                itr1.remove();
//                            }
//                        }
//                        try {
//                            Controller.sendBuffer.put(Controller.getObjectFromContent(name + " timeout! killed! bye!"));
//                            stThridThread.stop();
//                        } catch (InterruptedException ex) {
//                            logger.debug("Controller put err!" + ex);
//                        }
//                    }
//
//                } catch (Exception ex) {
//                    logger.debug("start subThridThread error. " + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
//
//                }
//            }
//
//
//
//
//
//
//            //List<String> processNameList = new ArrayList<String>();
//            //processNameList = ProcessTools.getNameListFromName(name + " ");
//            /*
//             * if (processNameList != null && processNameList.size() > 0) {
//             * logger.debug(name + " client thread has already start."); try {
//             * Controller.sendBuffer.put(Controller.getObjectFromContent(name +
//             * " client thread has already start.")); } catch
//             * (InterruptedException ex) { logger.debug("Controller put err!" +
//             * ex); } //Controller.sendBuffer.add(name + " client thread has
//             * already start."); return msg; } else { logger.debug("start " +
//             * name + " client thread."); //start process //xml =
//             * xml.replaceAll("\"", "tihuan"); //String cmd = "nohup java -jar "
//             * + PropsFiles.getValue("rootPath") + "/" + jarname + " " + name +
//             * " " + xml + " &"; //String cmd = "nohup java -jar " +
//             * PropsFiles.getValue("rootPath") + "/" + jarname+" &"; String cmd
//             * = "nohup /usr/iie/transclient/script/test.sh &";
//             * logger.debug("start process cmd is " + cmd);
//             * //Controller.sendBuffer.add("cmd: " +cmd); if
//             * (ProcessTools.startProcess(cmd)) { //if (true) {
//             * logger.debug("start process success " + name); try {
//             * Controller.sendBuffer.put(Controller.getObjectFromContent(name +
//             * " start process success!")); } catch (InterruptedException ex) {
//             * logger.debug("Controller put err!" + ex); } //
//             * Controller.sendBuffer.add(name+" start process success!" );
//             * logger.debug("new timeout " + name);
//             *
//             * Timer timer = new Timer(); timer.schedule(new Timeout(name),
//             * timeout * 1000);
//             *
//             *
//             * //TimePo timePo = new TimePo(); //timePo.setName(name);
//             * //timePo.setTimeout(timeout); //Date date = new Date();
//             * //timePo.setBeginTime(date.toString());
//             * //Controller.timePoList.add(timePo); } else { logger.debug("start
//             * process err!" + name); try {
//             * Controller.sendBuffer.put(Controller.getObjectFromContent(name +
//             * " start process err! ")); } catch (InterruptedException ex) {
//             * logger.debug("Controller put err!" + ex); }
//             * //Controller.sendBuffer.add(name+" start process err! " );
//             *
//             * }
//             * }
//             */
//
//
//
//
//        } else if (id.equals("2")) {
//            ;//inner singal
//            String content = msg.get("content").toString();
//            String xml = msg.get("xml").toString();
//            logger.debug("inner signal msg'xml=" + xml + "|content=" + content);
//            MessagePo mesPo = new MessagePo();
//            mesPo.setId(id);
//            mesPo.setContent(content);
//            mesPo.setXml(xml);
//            mesPo.setReadFlag(false);
//            Controller.sendBuffer.add(mesPo);
//            //iii++;
//            //logger.debug("iii = "+iii);
//        } else if (id.equals("3")) {
//            //properties update
//            String xml = "";
//            xml = msg.get("xml").toString();//sysController's xml
//
//            String key_value = "";
//            key_value = XmlTools.getValueFromStr(xml, "_key|value");
//            try {
//                String[] keyvaluelist = key_value.split("[|]");
//                String key = keyvaluelist[0];
//                String value = keyvaluelist[1];
//                PropsFiles.update(key, value);
//                logger.debug(key_value + " update success!");
//                try {
//                    Controller.sendBuffer.put(Controller.getObjectFromContent("update success!"));
//                } catch (InterruptedException ex) {
//                    logger.debug("Controller put err!" + ex);
//                }
//
//            } catch (Exception ex1) {
//                logger.debug(key_value + " update err!");
//                try {
//                    Controller.sendBuffer.put(Controller.getObjectFromContent("update err!"));
//                } catch (InterruptedException ex) {
//                    logger.debug("Controller put err!" + ex);
//                }
//            }
//        }

        return msg;
    }

    public void run() {
        try {
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent("listern server is beginning! pleae wait...",""));
            } catch (InterruptedException ex) {
                logger.debug("Controller put err!" + ex);
            }
            //Controller.sendBuffer.add("listern server is beginning! pleae wait...");
            logger.debug("listern server is beginning! pleae wait...");
            HttpServer server = new HttpServer(this, port);
            Connector cn = new SelectChannelConnector();
            cn.setHost(PropsFiles.getValue("localIP"));
            server.addConnector(cn);
            server.start();
            logger.debug("listern server has finished!");
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent("listern server has finished!",""));
            } catch (InterruptedException ex) {
                logger.debug("Controller put err!" + ex);
            }
            //Controller.sendBuffer.add("listern server has finished!");
        } catch (Exception ex) {
            logger.debug("avro server start error!" + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent("listern server start error! please check the local port: " + port,""));
            } catch (InterruptedException ex1) {
                logger.debug("Controller put err!" + ex1);
            }
            //Controller.sendBuffer.add("listern server start error! please check the local port: "+port );
        }
    }
    /*
     * public Object respond(Message message, Object request) throws Exception {
     * GenericRecord req = (GenericRecord) request; GenericRecord msg =
     * (GenericRecord) (req.get("message")); String s =
     * msg.get("id").toString(); if(s.equals("2")){ int i =0; ;//status }else
     * if(s.equals("3")){ int i=0; ;//heartbeat } // process the request // ....
     * return msg; }
     */
}
