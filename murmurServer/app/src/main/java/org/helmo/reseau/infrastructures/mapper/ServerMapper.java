package org.helmo.reseau.infrastructures.mapper;

import org.helmo.reseau.domains.Server;
import org.helmo.reseau.domains.Tag;
import org.helmo.reseau.domains.User;
import org.helmo.reseau.infrastructures.dto.ServerDto;
import org.helmo.reseau.infrastructures.dto.TagDto;
import org.helmo.reseau.infrastructures.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class ServerMapper {
    public ServerMapper() {
    }

    public Server getServer(ServerDto server) {
        List<User> users = server.getUserList().stream().map(u -> new User(u.getLogin(), u.getBCryptHash(), u.getBCryptRound(), u.getBCryptSalt(), u.getFollowers(), u.getUserTags(), u.getLockoutCounter())).collect(Collectors.toList());
        List<Tag> tags = server.getTagsList().stream().map(t -> new Tag(t.getName(), t.getUsers())).collect(Collectors.toList());
        return new Server(server.getDomain(), server.getSaltSizeInBytes(), server.getMulticastAddress(), server.getMulticastPort(), server.getUnicastPort(), server.getRelayPort(), server.getBase64AES(), server.isTls(), users, tags);
    }

    public ServerDto getServerDto(Server server) {
        List<UserDto> users = server.getUserList().stream().map(u -> new UserDto(u.getLogin(), u.getBcryptHash(), u.getBcryptRound(), u.getBcryptSalt(), u.getFollowers(), u.getUserTags(), u.getLockoutCounter())).collect(Collectors.toList());
        List<TagDto> tags = server.getTagsList().stream().map(t -> new TagDto(t.getName(), t.getUsers())).collect(Collectors.toList());

        return new ServerDto(server.getDomain(), server.getSaltSizeInBytes(), server.getMulticastAddress(), server.getMulticastPort(), server.getUnicastPort(), server.getRelayPort(), server.getBase64AES(), server.isTls(), users, tags);
    }
}
