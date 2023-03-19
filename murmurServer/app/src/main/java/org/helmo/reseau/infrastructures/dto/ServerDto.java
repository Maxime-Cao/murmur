package org.helmo.reseau.infrastructures.dto;

import java.util.List;

public class ServerDto {
    private final String currentDomain;
    private final int saltSizeInBytes;
    private final String multicastAddress;
    private final int multicastPort;
    private final int unicastPort;
    private final int relayPort;
    private final String base64AES;
    private final boolean tls;
    private final List<UserDto> users;
    private final List<TagDto> tags;

    public ServerDto(String domain, int saltSizeInBytes, String multicastAddress, int multicastPort, int unicastPort, int relayPort, String base64AES, boolean tls, List<UserDto> users, List<TagDto> tags) {
        this.currentDomain = domain;
        this.saltSizeInBytes = saltSizeInBytes;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.unicastPort = unicastPort;
        this.relayPort = relayPort;
        this.base64AES = base64AES;
        this.tls = tls;
        this.users = users;
        this.tags = tags;
    }

    public String getDomain() {
        return currentDomain;
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

    public int getUnicastPort() {
        return unicastPort;
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

    public List<UserDto> getUserList() {
        return users;
    }

    public List<TagDto> getTagsList() {
        return tags;
    }
}
