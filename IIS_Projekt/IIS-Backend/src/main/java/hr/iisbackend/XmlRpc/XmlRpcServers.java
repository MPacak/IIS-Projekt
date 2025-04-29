package hr.iisbackend.XmlRpc;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.webserver.WebServer;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component

public class XmlRpcServers implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        WebServer server = new WebServer(8088); // XML-RPC server na portu 8088
        XmlRpcServer rpcServer = server.getXmlRpcServer();
        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        phm.addHandler("WeatherService", WeatherServiceImpl.class);
        rpcServer.setHandlerMapping(phm);
        server.start();
        System.out.println("XML-RPC Server pokrenut na portu 8088...");
    }
}
