/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.cls.agent.tools;

/**
 *
 * @author root
 */
import org.apache.avro.Protocol;
import org.apache.avro.Protocol.Message;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.ipc.HttpServer;
import org.apache.avro.ipc.generic.GenericResponder;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.nio.SelectChannelConnector;

public class Server extends GenericResponder {

    private Protocol protocol = null;
    private int port;

    public Server(Protocol protocol, int port) {
        super(protocol);
        this.protocol = protocol;
        this.port = port;
    }

    public Object respond(Message message, Object request) throws Exception {
        GenericRecord req = (GenericRecord) request;
        GenericRecord msg = (GenericRecord) (req.get("message"));
        String s = msg.get("id").toString();
        if(s.equals("2")){
             int i =0;
             ;//status
        }else if(s.equals("3")){
            int i=0;
            ;//heartbeat
        }
        // process the request
        // ....
        return msg;
    }

    public void run() {
        try {
            HttpServer server = new HttpServer(this, port);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


}
