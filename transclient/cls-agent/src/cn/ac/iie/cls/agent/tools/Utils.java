/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;
import java.io.File;
import java.io.IOException;
import java.net.URL;
 
import org.apache.avro.Protocol;
/**
 *
 * @author root
 */
public class Utils {
     public static Protocol getProtocol() {
        Protocol protocol = null;
        try {
            URL url = Utils.class.getClassLoader().getResource("message.avpr");
            //URL url = Utils.class.getClassLoader().getResource("/usr/iie/transclient/controller/build/classes/message.avpr");
            String s = url.getPath();
            protocol = Protocol.parse(new File(url.getPath()));
        } catch (IOException e) {
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