/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author root
 */
public class XmlTools {

    private static Logger logger = Logger.getLogger(XmlTools.class);
    public static String value = "";//final value for key

    //
    public static String getOperatorAttribute(String xml, String key) {
        value = "";
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException ex) {
            logger.debug(xml + "is err!please check!");
            return "";
        }
        Element rootElt = doc.getRootElement();
        //Element el = rootElt.element("operator");
        String str = "";
        //str=el.attributeValue(key);
        //return str;
        //Iterator iter = rootElt.elementIterator("operator");
        //String ipStr = "";
//        while (iter.hasNext()) {
//            Element bsElement = (Element) iter.next();
//            ipStr = bsElement.attributeValue(key);
//        }
        List<Element> childrenList = rootElt.elements();
        for (int i = 0; i < childrenList.size(); i++) {
            if(childrenList.get(i).element("operator")!=null){
                return childrenList.get(i).element("operator").attributeValue(key);
            }
       // getElValueDGTextReturnValue(rootElt, key);
        }
        return str;
        //return value;
    }

    private static void getElValueDGTextReturnValue(Element rootEl, String key) {
        //logger.debug("########## xml:" + rootEl.asXML());
        if (rootEl.elements().size() > 0) {
            List<Element> childrenList = rootEl.elements();
            for (int i = 0; i < childrenList.size(); i++) {
                getElValueDGTextReturnValue(childrenList.get(i), key);
            }
        }
        String _name = rootEl.attributeValue(key);
        logger.debug("xml'name: " + _name);
        if (_name != null && !_name.equals("")) {
            if (!value.equals("")) {
                value = value + "|" + _name;
            } else {
                value = _name;
            }
        }
    }

    public static String getValueFromStrDGText(String xml, String name_key) {
        value = "";
        Document doc = null;
        SAXReader reader = new SAXReader();
        try {
            //String code = xmlPath.get
            InputStream in = new ByteArrayInputStream(xml.getBytes("utf-8"));
            doc = reader.read(in);
        } catch (Exception ex) {
            logger.debug("read xml file error! " + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            return "";
        }
        if (doc != null) {
            Element rootEl = doc.getRootElement();
            getElValueDGText(rootEl, name_key);

            if (value != null && !value.equals("")) {
                return value;
            }
        }
        return "";
    }

    private static void getElValueDGText(Element rootEl, String key) {
        if (rootEl.elements().size() > 0) {
            List<Element> childrenList = rootEl.elements();
            for (int i = 0; i < childrenList.size(); i++) {
                getElValueDGText(childrenList.get(i), key);
            }
        }
        String _name = rootEl.attributeValue("name");
        //logger.debug("xml'name: " + _name);
        if (_name != null && _name.equals(key)) {
            if (!value.equals("")) {
                value = value + "|" + rootEl.getText();
            } else {
                value = rootEl.getText();
            }
        }
    }
    //

    public static String getValueFromStrDG(String xml, String name_key) {
        value = "";
        Document doc = null;
        SAXReader reader = new SAXReader();
        try {
            //String code = xmlPath.get
            InputStream in = new ByteArrayInputStream(xml.getBytes("utf-8"));
            doc = reader.read(in);
        } catch (Exception ex) {
            logger.debug("read xml file error! " + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            return "";
        }
        if (doc != null) {
            Element rootEl = doc.getRootElement();
            getElValueDG(rootEl, name_key);

            if (value != null && !value.equals("")) {
                return value;
            }
        }
        return "";
    }

    private static void getElValueDG(Element rootEl, String key) {
        if (rootEl.elements().size() > 0) {
            List<Element> childrenList = rootEl.elements();
            for (int i = 0; i < childrenList.size(); i++) {
                getElValueDG(childrenList.get(i), key);
            }
        }
        String _name = rootEl.attributeValue("name");
        logger.debug("xml'name: " + _name);
//        if (_name != null && _name.equals(key)) {
//            value = value + "|" + rootEl.attributeValue("value");
//        }
        if (_name != null && _name.equals(key)) {
            if (!value.equals("")) {
                value = value + "|" + rootEl.attributeValue("value");
            } else {
                value = rootEl.attributeValue("value");
            }
        }
    }

    public static String getValueFromStr(String xml, String name_key) {
        value = "";
        Document doc = null;
        SAXReader reader = new SAXReader();
        try {
            //String code = xmlPath.get
            InputStream in = new ByteArrayInputStream(xml.getBytes("utf-8"));
            doc = reader.read(in);
        } catch (Exception ex) {
            logger.debug("read xml file error! " + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            return "";
        }
        if (doc != null) {
            Element rootEl = doc.getRootElement();
            getElValue(rootEl, name_key);

            if (value != null && !value.equals("")) {
                return value;
            }
        }
        return "";
    }

    public static String getValue(String xmlPath, String name_key) {
        Document doc = null;
        SAXReader reader = new SAXReader();
        try {
            //String code = xmlPath.get
            doc = reader.read(new File(xmlPath));
        } catch (DocumentException ex) {
            logger.debug("read xml file error! " + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            return "";
        }
        if (doc != null) {
            Element rootEl = doc.getRootElement();
            getElValue(rootEl, name_key);

            if (value != null && !value.equals("")) {
                return value;
            }
        }
        return "";
    }

    private static void getElValue(Element rootEl, String key) {
        logger.debug("########### xml:" + rootEl.asXML());
        if (rootEl.elements().size() > 0) {
            List<Element> childrenList = rootEl.elements();
            for (int i = 0; i < childrenList.size(); i++) {
                getElValue(childrenList.get(i), key);
            }
        }
//        List<Element> childrenList = rootEl.elements();
//        if (childrenList != null && childrenList.size() > 0) {
//            for (int i = 0; i < childrenList.size(); i++) {
//                getElValue(childrenList.get(i), key);
//            }
//        }
        String _name = rootEl.attributeValue("name");
        logger.debug(rootEl.element(key).asXML());
//        if (_name != null && _name.equals(key)) {
//            value = rootEl.getText();
//        }
        if (_name != null && _name.equals(key)) {
            if (!value.equals("")) {
                value = value + "|" + rootEl.getText();
            } else {
                value = rootEl.getText();
            }
        }
    }

    public static String getElValueFromStr(String xml, String key) {
        //logger.debug("########### xml:" + rootEl.asXML());
        Document doc = null;
        SAXReader reader = new SAXReader();
        try {
            //String code = xmlPath.get
            InputStream in = new ByteArrayInputStream(xml.getBytes("utf-8"));
            doc = reader.read(in);
        } catch (Exception ex) {
            logger.debug("read xml file error! " + ex.getLocalizedMessage() + "|" + ex.getMessage() + "|" + ex.getStackTrace());
            return "";
        }
        if (doc != null) {
            Element rootEl = doc.getRootElement();
            Element el = rootEl.element(key);
            String str = "";
            str = el.getText();
            return str;
        }
        return "";
    }

    public static int getTimeOut(String configFileName) {
        logger.debug("get timeout file is " + configFileName);
        return 23;
    }
}
