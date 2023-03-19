package org.helmo.reseau.infrastructures;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.helmo.reseau.domains.Server;
import org.helmo.reseau.infrastructures.dto.ServerDto;
import org.helmo.reseau.infrastructures.mapper.ServerMapper;
import org.helmo.reseau.repositories.IServerRepositories;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class ServerRepositories implements IServerRepositories {

    private final String path;
    private final String fileName;
    private final ServerMapper serverMapper;

    public ServerRepositories(String path, String fileName, ServerMapper serverMapper) {
        this.path = path;
        this.fileName = fileName;
        this.serverMapper = serverMapper;
    }

    @Override
    public Server getServer() {
        Gson gson = new Gson();
        String configFilePath = Paths.get(path, fileName).toAbsolutePath().toString();
        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath, StandardCharsets.UTF_8))) {
            ServerDto server = gson.fromJson(reader, ServerDto.class);
            return serverMapper.getServer(server);

        } catch (IOException e) {
            System.out.println("[!] Error ServerConfigMapper.getServer: " + e.getMessage());
            return null;
        } catch (JsonSyntaxException e) {
            System.out.println("[!] Error ServerConfigMapper.getServer JSON: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void writeServer(Server server) {
        Gson gson = new Gson();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(path, fileName).toAbsolutePath().toString(), StandardCharsets.UTF_8))) {

            ServerDto serverDto = serverMapper.getServerDto(server);
            gson.toJson(serverDto, writer);

        } catch (IOException e) {
            System.out.println("[!] Error ServerConfigMapper.writeServer: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.out.println("[!] Error ServerConfigMapper.writeServer JSON: " + e.getMessage());
        }
    }
}
