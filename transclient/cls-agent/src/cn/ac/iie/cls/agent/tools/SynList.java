/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.po.MessagePo;
import cn.ac.iie.cls.agent.subThread.Clients;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class SynList {

    private static Logger logger = Logger.getLogger(SynList.class);
    private List<MessagePo> synList = Collections.synchronizedList(new ArrayList<MessagePo>());

    public synchronized void add(String content) {
        String localIP = "";
        try {
            localIP = PropsFiles.getValue("localIP");
        } catch (IOException ex) {
            ;
        }

        logger.debug(localIP+ " SynList synList add content!content:"+content);
        MessagePo mesPo = new MessagePo();
        mesPo.setId("2");
        mesPo.setContent(new Date().toString()+" "+localIP+" "+content);
        mesPo.setReadFlag(false);
        mesPo.setXml(localIP+" "+content);
        synList.add(mesPo);
        logger.debug(localIP+" SynList synList add finish content!content:"+content);
    }

    public synchronized void add(MessagePo mesPo) {
        logger.debug("SynList synList add mesPo!");
        synList.add(mesPo);
        logger.debug("SynList synList add mesPo finish!");
    }

    public synchronized void update(MessagePo mesPo) {
        if (mesPo != null) {
            logger.debug("SynList synList update finish!");
            mesPo.setReadFlag(true);
            logger.debug("SynList synList update finish!");
        }
    }

    public synchronized void sendAndRemove() {
        

        try {
            Controller.avc.run(synList);
        } catch (Exception ex) {
            logger.debug("sendAndRemove err !" + "|"+ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
        }

        logger.debug("synList sendAndRemove finish!");
    }
}
