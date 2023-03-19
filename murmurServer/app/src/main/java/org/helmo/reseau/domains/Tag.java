package org.helmo.reseau.domains;

import java.util.List;

public class Tag {
    private final String name;
    private final List<String> users;

    public Tag(String name, List<String> users) {
        this.name = name;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public List<String> getUsers() {
        return users;
    }

    public void addUser(String user) {
        this.users.add(user);
    }

}
