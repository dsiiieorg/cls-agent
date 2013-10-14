/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;
import java.io.File;
import java.io.IOException;
import java.net.URL;
 
import org.apache.avro.Protocol;
import org.apache.log4j.Logger;
/**
 *
 * @author root
 */
public class AvroUtils {
    private static Logger logger = Logger.getLogger(AvroUtils.class);
     public static Protocol getProtocol(String avprFile) {
        Protocol protocol = null;
        try {
            //URL url = AvroUtils.class.getClassLoader().getResource(avprFile);
            //String s = url.getPath();
            //logger.debug("url:"+s);
            protocol = Protocol.parse(new File(avprFile));
        } catch (IOException ex) {
            logger.debug("protocol err!"+ex.getLocalizedMessage()+"!"+ex.getMessage());
            ;
        }
        return protocol;
    }
}
/*{
    "namespace":"avro",
    "protocol":"messageProtocol",
    "doc":"This is a message",
    "name":"Message",

    "types":[
        {"name":"message","type":"record",
            "fields":[
                {"name":"name","type":"string"},
                {"name":"type","type":"int"},
                {"name":"valid","type":"boolean"},
                {"name":"content","type":"bytes"},
            ]}
            ],
      "messages":{
        "sendMessage":{
            "doc":"test",
            "request":[{"name":"message","type":"message"}],
            "response":"message"
        }
    }  
}*/