/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.po.MessagePo;
import cn.ac.iie.cls.agent.po.TimePo;
import cn.ac.iie.cls.agent.subThread.Clients;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class TimeoutList {

    private static Logger logger = Logger.getLogger(TimeoutList.class);
    private List<TimePo> synList = Collections.synchronizedList(new ArrayList<TimePo>());

    public synchronized void add(TimePo timePo) {
        logger.debug("TimeoutList synList add! timePo.getName() = "+timePo.getName());
        synList.add(timePo);
        logger.debug("TimeoutList synList add finish timePo.getName() = "+timePo.getName());
    }

    public synchronized void update(TimePo timePo) {
        if (timePo != null) {
            logger.debug("TimeoutList synList update finish timePo.getName() = "+timePo.getName());
            //timePo.setReadFlag(true);
            logger.debug("TimeoutList synList update finish timePo.getName() = "+timePo.getName());
        }
    }

    public synchronized void remove(TimePo timePo) {
        logger.debug("TimeoutList synList Remove start timePo.getName() = "+timePo.getName());
        String name = "";
        Iterator<TimePo> itr = synList.iterator();
        while (itr.hasNext()) {
            TimePo tmpTimePo = itr.next();
            
            if (tmpTimePo.getName().equals(timePo.getName())) {
                name = timePo.getName();
                itr.remove();
                logger.debug("TimeoutList timepo has be removed! "+name+"| current time is "+new Date().toString());
            }
        }
        logger.debug("TimeoutList synList Remove finish!timePo.getName() = "+name);
    }
}
