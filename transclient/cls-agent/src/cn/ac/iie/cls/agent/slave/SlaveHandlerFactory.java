/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.slave;

import cn.ac.iie.cls.agent.slave.datacollect.CLSAgentDataCollectHandler;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author alexmu
 */
public class SlaveHandlerFactory {

    private static Logger logger = Logger.getLogger(SlaveHandlerFactory.class);
    private static Map<String, Class> slaveClassSet = new HashMap<String, Class>() {

        {
            //clsagent
            put("/clsagent/execmd", CLSAgentDataCollectHandler.class);
        }
    };
    private static Map<String, SlaveHandler> slaveObjectSet = new HashMap<String, SlaveHandler>();

    public static SlaveHandler getSlaveHandler(String pRequestPath) throws Exception {
        SlaveHandler slaveHandler = null;

        slaveHandler = slaveObjectSet.get(pRequestPath);

        if (slaveHandler == null) {
            Class slaveHandlerClass = slaveClassSet.get(pRequestPath);
            if (slaveHandlerClass != null) {
                try {
                    slaveHandler = (SlaveHandler) (slaveHandlerClass.newInstance());
                    slaveObjectSet.put(pRequestPath, slaveHandler);
                } catch (Exception ex) {
                    slaveHandler = null;
                    throw new Exception("initializing slave handler for " + pRequestPath + " is failed for " + ex.getMessage(), ex);
                }
            } else {
                throw new Exception("no slave handler for " + pRequestPath + " is found ");
            }
        }
        return slaveHandler;
    }
}
