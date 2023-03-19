package org.helmo.reseau.servers;

import org.helmo.reseau.domains.Server;
import org.helmo.reseau.infrastructures.ServerRepositories;
import org.helmo.reseau.infrastructures.mapper.ServerMapper;
import org.helmo.reseau.relay.RelayManager;
import org.helmo.reseau.repositories.IServerRepositories;
import org.helmo.reseau.tasks.TaskManager;

import java.net.NetworkInterface;
import java.nio.file.Paths;

public class ServerFactory {
    private ServerManager serverManager;
    private RelayManager relayManager;

    public ServerFactory(String configFileName, String certificateFileName, String certificatePassword, NetworkInterface selectedInterface) {
        String resourceFolder;
        if (Paths.get("").toAbsolutePath().toString().contains("app")) {
            resourceFolder = Paths.get("", "src", "main", "resources").toAbsolutePath().toString();
        } else {
            resourceFolder = Paths.get("", "app", "src", "main", "resources").toAbsolutePath().toString();
        }

        String configPath = Paths.get(resourceFolder, "config").toString();
        IServerRepositories repositories = new ServerRepositories(configPath, configFileName, new ServerMapper());

        Server server = repositories.getServer();
        if (server == null) {
            System.out.println("[!] Error: server is null");
            return;
        }

        String certificatePath = Paths.get(resourceFolder, "certificate").toString();
        TLSSocketFactory tlsSocketFactory = new TLSSocketFactory(certificatePath, certificateFileName, certificatePassword);

        TaskManager taskManager = new TaskManager();

        relayManager = new RelayManager(repositories, taskManager, selectedInterface);
        serverManager = new ServerManager(repositories, tlsSocketFactory, taskManager, relayManager);


    }

    public void start() {
        if (serverManager != null) {
            (new Thread(serverManager)).start();
            (new Thread(relayManager)).start();

        } else {
            System.out.println("[!] Error: serverManager is null");
        }
    }
}