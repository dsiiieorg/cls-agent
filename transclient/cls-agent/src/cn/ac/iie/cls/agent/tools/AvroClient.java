/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

/**
 *
 * @author root
 */
import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.po.MessagePo;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.apache.avro.Protocol;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.ipc.HttpTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.generic.GenericRequestor;
import org.apache.log4j.Logger;

public class AvroClient {

    /**
     * @param args the command line arguments
     */
    private static Logger logger = Logger.getLogger(AvroClient.class);
    public Protocol protocol = null;
    public String host = null;
    public int port = 0;
    public int size = 0;
    public int count = 0;

    public AvroClient(Protocol protocol, String host, int port, int size, int count) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.size = size;
        this.count = count;
    }
    public void sendMessage() throws Exception {


        //send heartbeat signal
        //requestDataHeartBeat.put("id", "3"); 
        //requestDataHeartBeat.put("xml", "xml3"); 
        //timePorequestDataHeartBeat.put("content", "heartbeat");
        
        //request.put("message", requestDataHeartBeat);
        
        //try {
        //    requestor.request("sendMessage", request);
        //} catch (Exception e) {
        //    logger.debug("requestor.request error sendheartbeat error!" + LogPrint.getExLog(e));
        //}
        
        //send log
        
        Iterator<MessagePo> itr1 = Controller.sendBuffer.iterator();
        logger.debug("synList.size() is "+Controller.sendBuffer.size());
        while (itr1.hasNext()) {
            //logger.debug("@@@@@synList.size() is "+synList.size());
            MessagePo mesPo = itr1.next();
            if (!mesPo.getReadFlag()) {
                Controller.requestData.put("id", mesPo.getId());
                Controller.requestData.put("xml", mesPo.getXml());
                Controller.requestData.put("content", mesPo.getContent());
                Controller.request.put("message", Controller.requestData);
                try {
                    Controller.requestor.request("sendMessage", Controller.request);
                } catch (Exception ex) {
                    try {
                        Controller.requestor.request("sendMessage", Controller.request);
                    } catch (Exception e) {
                        logger.debug("requestor.request error two times error!"+e);//logger.debug("requestor.request error two times error!" + LogPrint.getExLog(e));
                    }
                    logger.debug("requestor.request error!"+ex);
                    logger.debug("send message to server err! please check server!"+ex);
                    
                }
                Thread.sleep(1000);

                mesPo.setReadFlag(true);
            }
        }
        Iterator<MessagePo> itr = Controller.sendBuffer.iterator();
        while (itr.hasNext()) {
            MessagePo mesPo = itr.next();
            if (mesPo.getReadFlag()) {
                itr.remove();
            }
        }
    }
    
    public void sendMessage(List synList) throws Exception {


        //send heartbeat signal
        //requestDataHeartBeat.put("id", "3"); 
        //requestDataHeartBeat.put("xml", "xml3"); 
        //timePorequestDataHeartBeat.put("content", "heartbeat");
        
        //request.put("message", requestDataHeartBeat);
        
        //try {
        //    requestor.request("sendMessage", request);
        //} catch (Exception e) {
        //    logger.debug("requestor.request error sendheartbeat error!" + LogPrint.getExLog(e));
        //}
        
        //send log
        
        Iterator<MessagePo> itr1 = synList.iterator();
        logger.debug("synList.size() is "+synList.size());
        while (itr1.hasNext()) {
            //logger.debug("@@@@@synList.size() is "+synList.size());
            MessagePo mesPo = itr1.next();
            if (!mesPo.getReadFlag()) {
                Controller.requestData.put("id", mesPo.getId());
                Controller.requestData.put("xml", mesPo.getXml());
                Controller.requestData.put("content", mesPo.getContent());
                Controller.request.put("message", Controller.requestData);
                try {
                    Controller.requestor.request("sendMessage", Controller.request);
                } catch (Exception ex) {
                    try {
                        Controller.requestor.request("sendMessage", Controller.request);
                    } catch (Exception e) {
                        logger.debug("requestor.request error two times error!");//logger.debug("requestor.request error two times error!" + LogPrint.getExLog(e));
                    }
                    logger.debug("requestor.request error!");
                    logger.debug("send message to server err! please check server!");
                    
                }
                Thread.sleep(1000);

                mesPo.setReadFlag(true);
            }
        }
        Iterator<MessagePo> itr = synList.iterator();
        while (itr.hasNext()) {
            MessagePo mesPo = itr.next();
            if (mesPo.getReadFlag()) {
                itr.remove();
            }
        }
    }

    public void run(List synList) {
        try {
            sendMessage(synList);
        } catch (Exception ex) {
            logger.debug("avroClient sendMessage error!" + "|"+ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            //logger.debug("avroClient sendMessage error!");
        }
    }
    public void run() {
        try {
            sendMessage();
        } catch (Exception ex) {
            logger.debug("avroClient sendMessage error!" + "|"+ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            //logger.debug("avroClient sendMessage error!");
        }
    }
}
