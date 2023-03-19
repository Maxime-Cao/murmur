package org.helmo.reseau.relay.multicast;

import org.helmo.reseau.domains.Server;
import org.helmo.reseau.grammar.Protocol;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class MulticastRunnable implements Runnable {

    private final Server server;
    private final NetworkInterface networkInterface;

    public MulticastRunnable(Server server, NetworkInterface networkInterface) {
        this.server = server;
        this.networkInterface = networkInterface;
    }

    @Override
    public void run() {
        Protocol p = new Protocol();
        try {
            String echoMessage = p.buildEcho(String.valueOf(server.getRelayPort()), server.getDomain());
            InetAddress multicastAddress = InetAddress.getByName(server.getMulticastAddress());
            InetSocketAddress group = new InetSocketAddress(multicastAddress, server.getMulticastPort());
            MulticastSocket socket = new MulticastSocket(server.getMulticastPort());

            // Joindre le groupe multicast en utilisant une adresse IP et un port
            socket.joinGroup(group, networkInterface);

            byte[] msgBytes = echoMessage.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, group);

            while (true) {
                socket.send(packet);
                Thread.sleep(15000);
            }
        } catch (Exception e) {
            System.out.println("[!] Erreur Multicast : " + e.getMessage());
        }
    }
}
