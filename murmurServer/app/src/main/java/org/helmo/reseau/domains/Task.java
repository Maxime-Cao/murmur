package org.helmo.reseau.domains;

import org.helmo.reseau.clients.ClientRunnable;

public class Task {
    private int id;
    private final String type;
    private final ClientRunnable source;
    private final String[] command;
    private StatusOfTask status;
    public Task(int id, String type, ClientRunnable source, String[] command, StatusOfTask status) {
        this.id = id;
        this.type = type;
        this.source = source;
        this.command = command;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public ClientRunnable getSource() {
        return source;
    }

    public String[] getCommand() {
        return command;
    }

    public void setStatus(StatusOfTask status) {
        this.status = status;
    }
}
