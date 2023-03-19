package org.helmo.reseau.repositories;

import org.helmo.reseau.domains.Server;

public interface IServerRepositories {
    Server getServer();
    void writeServer(Server server);
}
