package org.helmo.reseau.relay;

import org.helmo.reseau.grammar.Protocol;
import org.helmo.reseau.tasks.TaskManager;
import org.helmo.reseau.utils.AESCodec;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RelayRunnable implements Runnable {
    private BufferedReader in = null;
    private PrintWriter out = null;
    private final TaskManager taskManager;
    private final Protocol protocol;
    private Socket client;
    private final AESCodec aesEncryption;
    private boolean isConnected;
    private RelayManager relayManager;

    public RelayRunnable(TaskManager taskManager, Protocol protocol, Socket relaySocket, RelayManager relayManager, AESCodec aesEncryption) {
        this.taskManager = taskManager;
        this.protocol = protocol;
        this.aesEncryption = aesEncryption;
        try {
            client = relaySocket;
            in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true);
            isConnected = true;
            this.relayManager = relayManager;
        } catch (IOException e) {
            System.out.println("[!] Error RelayRunnable: " + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void run() {

        try {
            String msg;
            while (isConnected && (msg = in.readLine()) != null) {
                String msgDecrypt = aesEncryption.decrypt(msg);
                System.out.println("[R] Message: " + msgDecrypt);

                String[] message = protocol.verifyMessage(msgDecrypt);
                if (message[0].equals("SEND")) {
                    StringBuilder sb = new StringBuilder();
                    for (String s : message) {
                        sb.append(s).append(" ");
                    }
                    if (!relayManager.checkIfIdMessageExists(message[1])) {
                        relayManager.addIdMessage(message[1]);
                        taskManager.createTask(null, message);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[!] Error RelayRunnable: " + Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                out.close();
                client.close();
                isConnected = false;
            } catch (IOException e) {
                System.out.println("[!] Error RelayRunnable: " + Arrays.toString(e.getStackTrace()));
            }
        }

    }

    public void sendMessage(String message) {
        String msgEncrypt = aesEncryption.encrypt(message);
        out.println(msgEncrypt);
        out.flush();
    }

}
