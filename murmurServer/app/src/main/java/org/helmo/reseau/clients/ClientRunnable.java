package org.helmo.reseau.clients;

import org.helmo.reseau.domains.User;
import org.helmo.reseau.grammar.Protocol;
import org.helmo.reseau.servers.ServerManager;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientRunnable implements Runnable, Closeable {
    private final SSLSocket clientSocket;
    private final ServerManager serverManager;
    private final Protocol protocol;
    private User user;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isOnServer;
    private boolean isAuthentified;
    private String randomString;

    public ClientRunnable(SSLSocket clientSocket, ServerManager serverManager, Protocol protocol) {
        this.clientSocket = clientSocket;
        this.serverManager = serverManager;
        this.protocol = protocol;
        this.isOnServer = true;
        this.isAuthentified = false;
        this.user = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            randomString = randomString(22);
            sendMessage(protocol.buildHello(serverManager.getServerDomain(), randomString));
        } catch (IOException e) {
            System.out.println("[!] Error ClientRunnable: " + Arrays.toString(e.getStackTrace()));
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
        if (msg.startsWith("-ERR")) {
            serverManager.closeClient(this);
        }
    }

    public List<String> getFollowers() {
        return user.getFollowers();
    }

    public String getUsername() {
        return user.getLogin();
    }

    private void handleMessage(String msg) {
        System.out.println("[C] Message: " + msg);
        String[] message = protocol.verifyMessage(msg);
        if (message[0].equals("DISCONNECT")) {
            sendMessage(protocol.buildOk("Disconnecting"));
            serverManager.closeClient(this);
        } else if (message[0].equals("-ERR") && !isAuthentified) {
            sendMessage(protocol.buildError(message[1]));
        } else if (isAuthentified) {
            serverManager.createTask(this, message);
        } else {
            authentication(message);
        }
    }

    private void authentication(String[] message) {
        switch (message[0]) {
            case "REGISTER" -> register(message);
            case "CONNECT" -> connect(message);
            case "CONFIRM" -> confirm(message);
            default ->
                    System.out.println("[!] Error ClientRunnable.authentification: " + message[0] + " is not a valid command");
        }
    }

    private void register(String[] message) {
        try {
            if (user != null) {
                sendMessage(protocol.buildError("An user is already connected"));
                return;
            }
            String name = message[1];
            int saltSize = Integer.parseInt(message[2]);
            String[] decryptedHash = message[3].split("\\$");

            if (decryptedHash.length != 4) {
                sendMessage(protocol.buildError("Wrong hash format"));
                return;
            }

            String salt = decryptedHash[3].substring(0, saltSize);
            String hash = decryptedHash[3].substring(saltSize);
            System.out.println("User: " + name + "  ");
            User user = new User(name, hash, Integer.parseInt(decryptedHash[2]), salt, new ArrayList<>(), new ArrayList<>(), 0);
            if (serverManager.registerUser(user)) {
                sendMessage(protocol.buildOk("User registered"));
                this.user = user;
                isAuthentified = true;
            } else {
                sendMessage(protocol.buildError("User already exists"));
            }
        } catch (Exception e) {
            System.out.println("[!] Error ClientRunnable.register: " + e.getMessage());
            sendMessage(protocol.buildError("An error occurred while registering the user"));
        }
    }

    private void connect(String[] message) {
        try {
            if (user != null) {
                sendMessage(protocol.buildError("An user is already connected"));
                return;
            }
            String name = message[1];
            user = serverManager.getUser(name);
            if (user == null) {
                sendMessage(protocol.buildError("User doesn't exist"));
            } else {
                sendMessage(protocol.buildParam(String.valueOf(user.getBcryptRound()), user.getBcryptSalt()));
            }
        } catch (Exception e) {
            System.out.println("[!] Error ClientRunnable.connect: " + e.getMessage());
            sendMessage(protocol.buildError("An error occurred while connecting the user"));
        }
    }

    private void confirm(String[] message) {
        try {
            if (user == null) {
                sendMessage(protocol.buildError("User doesn't exist"));
                return;
            }
            String sha3Hex = message[1];
            String sha3HexToCompare;
            MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            byte[] hash = digest.digest((randomString + "$2b$" + user.getBcryptRound() + "$" + user.getBcryptSalt() + user.getBcryptHash()).getBytes(StandardCharsets.UTF_8));
            sha3HexToCompare = bytesToHex(hash);
            if (sha3Hex.equals(sha3HexToCompare)) {
                sendMessage(protocol.buildOk("User connected"));
                isAuthentified = true;
            } else {
                sendMessage(protocol.buildError("Wrong password"));
            }
        } catch (Exception e) {
            System.out.println("[!] Error ClientRunnable.confirm: " + e.getMessage());
            sendMessage(protocol.buildError("An error occurred while connecting the user"));
        }
    }

    private String randomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#%&()_+-=[]";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public void run() {
        try {
            while (isOnServer) {
                String msg = in.readLine();
                if (msg != null) {
                    handleMessage(msg);
                }
            }
        } catch (Exception e) {
            System.out.println("[!] Error ClientRunnable.run: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            isOnServer = false;
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("[!] Error ClientRunnable.close: " + e.getMessage());
        }
    }

    public boolean isFollowed(String follow) {
        return user.isFollowed(follow);
    }

    public void addFollowedTag(String follow) {
        user.addFollowedTag(follow);
        serverManager.saveServer();
    }
}
