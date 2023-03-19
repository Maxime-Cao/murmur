package org.helmo.reseau.tasks;

import org.helmo.reseau.clients.ClientRunnable;
import org.helmo.reseau.domains.StatusOfTask;
import org.helmo.reseau.domains.Task;
import org.helmo.reseau.domains.User;
import org.helmo.reseau.grammar.Protocol;
import org.helmo.reseau.servers.ServerManager;


import java.util.*;

public class TaskExecutor implements Runnable {
    private final TaskManager taskManager;
    private final ServerManager serverManager;
    private final Protocol protocol;

    public TaskExecutor(TaskManager taskManager, ServerManager serverManager, Protocol protocol) {
        this.taskManager = taskManager;
        this.serverManager = serverManager;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("[!] Error TaskExecutor.run: " + e.getMessage());
            }
            Task currentTask = taskManager.getNextTask();
            if (currentTask != null) {
                runTask(currentTask);
            }
        }
    }

    private void runTask(Task currentTask) {
        System.out.println("[*] Run task: " + currentTask.getType());
        currentTask.setStatus(StatusOfTask.IN_PROGRESS);
        switch (currentTask.getType()) {
            case "FOLLOW" -> follow(currentTask);
            case "MSG" -> msg(currentTask);
            case "SEND" -> send(currentTask);
            case "-ERR" -> System.out.println("[!] TaskExecutor : An error occurred");
            default -> System.out.println("[!] TaskExecutor : Unknown task type");
        }

    }

    private void follow(Task currentTask) {
        try {
            String[] command = currentTask.getCommand();
            String tagOrUserDomain = command[1];
            String tagOrUser = tagOrUserDomain.substring(0, tagOrUserDomain.indexOf("@"));
            String domain = tagOrUserDomain.substring(tagOrUserDomain.indexOf("@") + 1);
            ClientRunnable source = currentTask.getSource();

            if (domain.equals(serverManager.getServerDomain())) {
                followDomain(tagOrUserDomain, tagOrUser, source);
            } else {
                followRelay(currentTask, command, tagOrUserDomain);
            }
            currentTask.setStatus(StatusOfTask.DONE);
        } catch (Exception e) {
            System.out.println("[!] TaskExecutor.follow: " + e.getMessage());
        }
    }

    private void followDomain(String tagOrUserDomain, String tagOrUser, ClientRunnable source) {
        try {
            String usernameDomain = String.format("%s@%s", source.getUsername(), serverManager.getServerDomain());
            if (tagOrUser.charAt(0) == '#') {
                if (!source.isFollowed(tagOrUserDomain)) {
                    serverManager.addTag(tagOrUser);
                    if (serverManager.addFollowedTag(usernameDomain, tagOrUser)) {
                        source.addFollowedTag(tagOrUserDomain);
                        source.sendMessage(protocol.buildOk("You are now following " + tagOrUserDomain));
                    }
                }
            } else {
                User userToFollow = serverManager.getUser(tagOrUser);
                if (userToFollow != null && !userToFollow.getLogin().equals(source.getUsername()) && !userToFollow.getFollowers().contains(usernameDomain)) {
                    userToFollow.addFollower(usernameDomain);
                    source.sendMessage(protocol.buildOk("You are now following " + tagOrUserDomain));
                }
            }
        } catch (Exception e) {
            System.out.println("[!] TaskExecutor.followDomain: " + e.getMessage());
        }
    }

    private void followRelay(Task currentTask, String[] command, String tagOrUserDomain) {
        try {
            String domainInfo = currentTask.getSource().getUsername() + "@" + serverManager.getServerDomain();
            String idMessage = String.format("%d@%s", taskManager.getId(), serverManager.getServerDomain());
            StringBuilder sb = new StringBuilder();
            for (String str : command) {
                sb.append(str).append(" ");
            }
            serverManager.sendMessageToRelay(protocol.buildSend(idMessage, domainInfo, tagOrUserDomain, sb.toString()));
        } catch (Exception e) {
            System.out.println("[!] TaskExecutor.followRelay: " + e.getMessage());
        }
    }

    private void msg(Task currentTask) {
        try {
            String[] command = currentTask.getCommand();
            String message = command[1];
            String serverDomain = serverManager.getServerDomain();
            String sender = String.format("%s@%s", currentTask.getSource().getUsername(), serverDomain);

            for (String recipient : getDestination(currentTask)) {
                String[] recipientParts = recipient.split("@");
                ClientRunnable clientRunnable = serverManager.getClient(recipientParts[0]);
                String messageToSend = protocol.buildMsgs(sender, message);
                if (clientRunnable != null) {
                    clientRunnable.sendMessage(messageToSend);
                } else {
                    String idMessage = String.format("%d@%s", taskManager.getId(), serverDomain);
                    serverManager.sendMessageToRelay(protocol.buildSend(idMessage, sender, recipient, messageToSend));
                }
            }
            currentTask.setStatus(StatusOfTask.DONE);
        } catch (Exception e) {
            System.out.println("[!] TaskExecutor.msg: " + e.getMessage());
        }
    }

    private void send(Task currentTask) {
        try {
            System.out.println("[*] TaskExecutor.send: " + Arrays.toString(currentTask.getCommand()));
            String[] command = currentTask.getCommand();
            String sender = command[2];
            String recipient = command[3];
            String messageToSend = command[4];
            String[] messageProtocol = protocol.verifyMessage(messageToSend.trim());
            switch (messageProtocol[0]) {
                case "MSGS" -> sendMsgs(sender, recipient, messageToSend, messageProtocol);
                case "FOLLOW" -> sendFollow(sender, recipient, messageProtocol);
                case "+OK" -> sendOk(recipient, messageToSend);
                default -> System.out.println("[!] TaskExecutor.send: Unknown message protocol");
            }
            currentTask.setStatus(StatusOfTask.DONE);
        } catch (Exception e) {
            System.out.println("[!] TaskExecutor.send: " + e.getMessage());
        }
    }

    private void sendMsgs(String sender, String recipient, String messageToSend, String[] messageProtocol) {
        try {
            String[] recipientParts = recipient.split("@");
            if (recipientParts[0].charAt(0) == '#') {
                Set<String> recipients = new HashSet<>();
                for (String tag : retrieveTags(messageToSend)) {
                    List<String> users = serverManager.getFollowers(tag);
                    if (users.contains(sender)) {
                        recipients.addAll(users);
                    }
                }
                recipients.remove(sender);

                for (String user : recipients) {
                    String[] userParts = user.split("@");
                    if (userParts[1].equals(serverManager.getServerDomain())) {
                        ClientRunnable clientRunnable = serverManager.getClient(userParts[0]);
                        if (clientRunnable != null) {
                            clientRunnable.sendMessage(protocol.buildMsgs(messageProtocol[1], messageProtocol[2]));
                        }
                    } else {
                        serverManager.sendMessageToRelay(protocol.buildSend(String.format("%d@%s", taskManager.getId(), serverManager.getServerDomain()), sender, user, messageToSend));
                    }
                }
            } else {
                ClientRunnable clientRunnable = serverManager.getClient(recipientParts[0]);
                if (clientRunnable != null) {
                    clientRunnable.sendMessage(protocol.buildMsgs(messageProtocol[1], messageProtocol[2]));
                }
            }
        } catch (Exception e) {
            System.out.println("[!] TaskExecutor.sendMsgs: " + e.getMessage());
        }
    }

    private void sendFollow(String sender, String recipient, String[] messageProtocol) {
        try {
            String[] tagOrUserDomain = messageProtocol[1].split("@");
            String reply = protocol.buildOk("You are now following " + messageProtocol[1]);
            boolean added = (tagOrUserDomain[0].charAt(0) == '#')
                    ? serverManager.addTag(tagOrUserDomain[0]) && serverManager.addFollowedTag(sender, tagOrUserDomain[0])
                    : serverManager.addFollower(recipient.substring(0, recipient.indexOf("@")), sender);
            if (added) {
                serverManager.sendMessageToRelay(protocol.buildSend(String.format("%d@%s", taskManager.getId(), serverManager.getServerDomain()), recipient, sender, reply));
            }
        } catch (Exception e) {
            System.out.println("[!] TaskExecutor.sendFollow: " + e.getMessage());
        }
    }

    private void sendOk(String recipient, String messageToSend) {
        try {
            String[] recipientParts = recipient.split("@");
            ClientRunnable clientRunnable = serverManager.getClient(recipientParts[0]);
            if (clientRunnable != null) {
                List<String> tags = retrieveTags(messageToSend);
                for (String tag : tags) {
                    clientRunnable.addFollowedTag(tag);
                }
                serverManager.saveServer();
                clientRunnable.sendMessage(messageToSend);
            }
        } catch (Exception e) {
            System.out.println("[!] TaskExecutor.sendOk: " + e.getMessage());
        }
    }

    private List<String> getDestination(Task currentTask) {
        Set<String> destinataires = new HashSet<>();
        try {
            ClientRunnable clientRunnable = currentTask.getSource();
            destinataires.addAll(clientRunnable.getFollowers());
            String username = clientRunnable.getUsername();
            for (String tag : retrieveTags(currentTask.getCommand()[1])) {
                if (serverManager.getTags().contains(tag)) {
                    destinataires.addAll(serverManager.getFollowers(tag));
                } else {
                    String messageToSend = protocol.buildMsgs(username + "@" + serverManager.getServerDomain(), currentTask.getCommand()[1]);
                    serverManager.sendMessageToRelay(protocol.buildSend(String.format("%d@%s", taskManager.getId(), serverManager.getServerDomain()), username + "@" + serverManager.getServerDomain(), tag + "@server2.godswila.guru", messageToSend));
                }
            }
            destinataires.remove(username + "@" + serverManager.getServerDomain());
        } catch (Exception e) {
            System.out.println("[!] TaskExecutor.getDestination: " + e.getMessage());
        }
        return new ArrayList<>(destinataires);
    }

    private List<String> retrieveTags(String message) {
        String[] words = message.split(" ");
        List<String> tags = new ArrayList<>();
        for (String word : words) {
            if (word.startsWith("#")) {
                tags.add(word);
            }
        }
        return tags;
    }
}