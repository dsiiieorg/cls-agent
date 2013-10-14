/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.master;

import cn.ac.iie.cls.agent.controller.Controller;
import cn.ac.iie.cls.agent.slave.SlaveHandler;
import cn.ac.iie.cls.agent.slave.SlaveHandlerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author mwm
 */
public class CCHandler extends AbstractHandler {

    private static CCHandler ccHandler = null;
    private static Logger logger = Logger.getLogger(CCHandler.class);

    public static CCHandler getCCHandler() {
        if (ccHandler != null) {
            return ccHandler;
        }
        ccHandler = new CCHandler();
        return ccHandler;
    }

    @Override
    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
       
        baseRequest.setHandled(true);

        String requestPath = baseRequest.getPathInfo().toLowerCase();
        logger.debug(requestPath);

        ServletInputStream servletInputStream = baseRequest.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[4096];
        int i = 0;
        while ((i = servletInputStream.read(b, 0, 4096)) > 0) {
            out.write(b, 0, i);
        }
        String requestContent = new String(out.toByteArray(), "UTF-8");
        logger.debug(requestContent);

        try {
            SlaveHandler slaveHandler = SlaveHandlerFactory.getSlaveHandler(requestPath);
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(slaveHandler.execute(requestContent));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            
        }
    }
}
