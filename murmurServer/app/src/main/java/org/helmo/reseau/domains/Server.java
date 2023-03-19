package org.helmo.reseau.domains;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private final String domain;
    private final int saltSizeInBytes;
    private final String multicastAddress;
    private final int multicastPort;
    private final int unicastPort;
    private final int relayPort;
    private final String base64AES;
    private final boolean tls;
    private final List<User> userList;
    private final List<Tag> tagsList;

    public Server(String domain, int saltSizeInBytes, String multicastAddress, int multicastPort, int unicastPort, int relayPort, String base64AES, boolean tls, List<User> userList, List<Tag> tagsList) {
        this.domain = domain;
        this.saltSizeInBytes = saltSizeInBytes;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.unicastPort = unicastPort;
        this.relayPort = relayPort;
        this.base64AES = base64AES;
        this.tls = tls;
        this.userList = userList;
        this.tagsList = tagsList;
    }

    public String getDomain() {
        return domain;
    }

    public void addUser(User user) {
        this.userList.add(user);
    }

    public void addTag(String name) {
        this.tagsList.add(new Tag(name, new ArrayList<>()));
    }

    public boolean hasUser(User user) {
        return this.userList.stream().anyMatch(u -> u.getLogin().equals(user.getLogin()));
    }

    public boolean hasTag(String name) {
        return this.tagsList.stream().anyMatch(t -> t.getName().equals(name));
    }

    public int getUnicastPort() {
        return unicastPort;
    }

    public int getSaltSizeInBytes() {
        return saltSizeInBytes;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public int getRelayPort() {
        return relayPort;
    }

    public String getBase64AES() {
        return base64AES;
    }

    public boolean isTls() {
        return tls;
    }

    public List<User> getUserList() {
        return userList;
    }

    public List<Tag> getTagsList() {
        return tagsList;
    }

    public User getUser(String name) {
        return this.userList.stream().filter(u -> u.getLogin().equals(name)).findFirst().orElse(null);
    }

    public List<String> getFollowers(String tag) {
        for (Tag t : this.tagsList) {
            if (t.getName().equals(tag)) {
                System.out.println("[+] Followers of " + tag + " : " + t.getUsers());
                return new ArrayList<>(t.getUsers());
            }
        }
        return new ArrayList<>();
    }

    public boolean addFollowedTag(String follow, String tag) {
        for (Tag t : this.tagsList) {
            if (t.getName().equals(tag) && !t.getUsers().contains(follow)) {
                t.addUser(follow);
                return true;
            }
        }
        return false;
    }

    public boolean addFollower(String destName, String sender) {
        for (User u : this.userList) {
            if (u.getLogin().equals(destName) && !u.getFollowers().contains(sender)) {
                u.addFollower(sender);
                return true;
            }
        }
        return false;
    }

    public List<String> getTags() {
        List<String> tags = new ArrayList<>();
        for (Tag t : this.tagsList) {
            tags.add(t.getName());
        }
        return tags;
    }
}
