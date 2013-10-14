/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.server;

import cn.ac.iie.cls.agent.commons.RuntimeEnv;
import cn.ac.iie.cls.agent.config.Configuration;
import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.master.CCHandler;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

/**
 *
 * @author alexmu
 */
public class CLSAgentServer {

    static Server server = null;
    private static Logger logger = Logger.getLogger(CLSAgentServer.class);

    public static void showUsage() {
        logger.debug("Usage:java -jar ");
    }

    /**
     * @param args the command line arguments
     */
    public static void run() {
        try {
            init();
            startup();
        } catch (Exception ex) {
            logger.error("starting cls-agent agenter server is failed for " + ex.getMessage(), ex);
            try {
                Controller.sendBuffer.put(Controller.getObjectFromContent("starting cls-agent agenter server is failed for " + ex.getMessage(), ""));
            } catch (InterruptedException ex1) {
                logger.debug("Controller put err!" + ex1);
            }
        }
    }

    private static void startup() throws Exception {
        logger.info("starting cls agent server...");
        server.start();
        logger.info("start cls agent server successfully");
        server.join();
    }

    
    private static void init() throws Exception {
        String configurationFileName = "cls-agent.properties";
        logger.info("initializing cls agent server...");
        logger.info("getting configuration from configuration file " + configurationFileName);
        Configuration conf = Configuration.getConfiguration(configurationFileName);
        if (conf == null) {
            throw new Exception("reading " + configurationFileName + " is failed.");
        }

        logger.info("initializng runtime enviroment...");
        if (!RuntimeEnv.initialize(conf)) {
            throw new Exception("initializng runtime enviroment is failed");
        }
        logger.info("initialize runtime enviroment successfully");

        String serverIP = conf.getString("jettyServerIP", "");
        if (serverIP.isEmpty()) {
            throw new Exception("definition jettyServerIP is not found in " + configurationFileName);
        }

        int serverPort = conf.getInt("jettyServerPort", -1);
        if (serverPort == -1) {
            throw new Exception("definition jettyServerPort is not found in " + configurationFileName);
        }

        Connector connector = new SelectChannelConnector();
        connector.setHost(serverIP);
        connector.setPort(serverPort);

        server = new Server();

        server.setConnectors(new Connector[]{connector});

        ContextHandler ccContext = new ContextHandler("/resources");
        CCHandler ccHandler = CCHandler.getCCHandler();
        if (ccHandler == null) {
            throw new Exception("initializing dataDispatchHandler is failed");
        }

        ccContext.setHandler(ccHandler);

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{ccContext});
        server.setHandler(contexts);
        logger.info("intialize cls agent server successfully");
    }
}
