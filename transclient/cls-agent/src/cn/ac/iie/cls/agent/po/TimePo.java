/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.po;

/**
 *
 * @author root
 */
public class TimePo {
    private String name;
    private int timeout;
    private String beginTime;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the beginTime
     */
    public String getBeginTime() {
        return beginTime;
    }

    /**
     * @param beginTime the beginTime to set
     */
    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }
}
