package org.helmo.reseau.infrastructures.dto;

import java.util.List;

public class TagDto {
    private final String name;
    private final List<String> users;

    public TagDto(String name, List<String> users) {
        this.name = name;
        this.users = users;
    }
    public String getName() {
        return name;
    }
    public List<String> getUsers() {
        return users;
    }
}
