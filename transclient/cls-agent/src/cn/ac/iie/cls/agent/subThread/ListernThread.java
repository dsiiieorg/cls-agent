/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import cn.ac.iie.cls.agent.tools.AvroServer;
import cn.ac.iie.cls.agent.tools.AvroUtils;
import cn.ac.iie.cls.agent.tools.PropsFiles;
import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.po.MessagePo;
import cn.ac.iie.cls.agent.server.CLSAgentServer;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.avro.Protocol;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class ListernThread implements Runnable {

    private static Logger logger = Logger.getLogger(ListernThread.class);
    public String name = "";

    @Override
    public void run() {
        int port = 0;
//        //int portInner = 0;  
//        //int portOutter = 0;  
//        try {
//            port = Integer.parseInt(PropsFiles.getValue("avroServerPort"));
//            logger.debug("Listern Thread port=" + port);
//            //portInner = Integer.parseInt(PropsFiles.getValue("avroServerInnerPort"));
//            //portOutter = Integer.parseInt(PropsFiles.getValue("avroServerOutterPort"));
//        } catch (IOException ex) {
//            logger.debug("propertity file avroServerPort error!" + "|" + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
//            return;
//        }
//        //if(portInner==0 || portOutter==0){
//        if (port == 0) {
//            //logger.debug("avroPort error port = 0! innerport = "+portInner+" | outterport ="+portOutter);
//            logger.debug("avroPort error port = 0!");
//            return;
//        }
//        logger.debug("avro server start!");
//        //new Server(AvroUtils.getProtocol("message.avpr"),port).run();
//        Protocol protocol = null;
//        try {
//            protocol = AvroUtils.getProtocol(PropsFiles.getValue("configPath") + "/message.avpr");
//        } catch (IOException ex) {
//            ;
//        }
//        if (protocol == null) {
//            logger.debug("protocol err!check message.avpr file!");
//            try {
//                Controller.sendBuffer.put(Controller.getObjectFromContent("listernThread start err!protocol err!check message.avpr file!",""));
//            } catch (InterruptedException ex) {
//                logger.debug("Controller put err!" + ex);
//            }
//            //Controller.sendBuffer.add("listernThread start err!protocol err!check message.avpr file!");
//            return;
//        }
//        logger.debug("protocol:" + protocol);
//        new AvroServer(protocol, port).run();
        logger.debug("jetty server start!");
        new CLSAgentServer().run();
        
        
        //new AvroServer(AvroUtils.getProtocol("message.avpr"),port).run();
        //new AvroServer(AvroUtils.getProtocol("inner.avpr"),portInner).run();
        //new AvroServer(AvroUtils.getProtocol("outter.avpr"),portOutter).run();
        logger.debug("jetty start finish!");
    }

    public ListernThread(String name) {
        this.name = name;
    }
}
