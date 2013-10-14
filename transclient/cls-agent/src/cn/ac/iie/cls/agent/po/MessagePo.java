/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.po;

/**
 *
 * @author root
 */
public class MessagePo {
    private String id;
    private String xml;
    private String content;
    private boolean readFlag;

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the readFlag
     */
    public boolean getReadFlag() {
        return readFlag;
    }

    /**
     * @param readFlag the readFlag to set
     */
    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }

    /**
     * @return the xml
     */
    public String getXml() {
        return xml;
    }

    /**
     * @param xml the xml to set
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
