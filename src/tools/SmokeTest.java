package tools;

import server.TimeServerImpl;
import server.TimeServerInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class SmokeTest {
    public static void main(String[] args) throws Exception {
        int port = 2099; // test port to avoid conflicts
        System.out.println("Starting headless smoke test on port " + port);

        // Ensure hostname set for RMI stubs in this test
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");

        Registry registry = LocateRegistry.createRegistry(port);
        TimeServerImpl server = new TimeServerImpl();
        registry.rebind("TimeServer", server);

        System.out.println("Server bound. Looking up via registry...");
        TimeServerInterface stub = (TimeServerInterface) registry.lookup("TimeServer");

        String regIp = stub.registerClient("SmokeClient");
        System.out.println("Registered SmokeClient, server saw IP: " + regIp);

        String comp = stub.getTimeForZone("Etc/UTC");
        System.out.println("getTimeForZone returned: " + comp);

        Map<String, String> clients = stub.getConnectedClients();
        System.out.println("Connected clients: " + clients);

        // cleanup
        registry.unbind("TimeServer");
        UnicastRemoteObject.unexportObject(server, true);
        UnicastRemoteObject.unexportObject(registry, true);

        System.out.println("Smoke test finished successfully.");
    }
}
