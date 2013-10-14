/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class Timeout extends TimerTask {

    private static Logger logger = Logger.getLogger(Timeout.class);
    private String name;

    public Timeout(String name) {
        this.name = name;
    }

    @Override
    public void run() {
       ProcessTools.kill(this.name + " ");
    }
}
