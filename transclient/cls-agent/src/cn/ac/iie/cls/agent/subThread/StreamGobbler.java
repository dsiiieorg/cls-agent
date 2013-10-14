/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.subThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class StreamGobbler implements Runnable {

    InputStream is;
    String type;
    private static Logger logger = Logger.getLogger(StreamGobbler.class);

    StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                logger.debug(type + ">" + line);
            }
            //System.out.println(type + ">" + line);    
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
