package org.helmo.reseau;

import org.helmo.reseau.servers.ServerFactory;
import org.helmo.reseau.utils.NetChooser;

import java.net.NetworkInterface;

public class App {
    private static String CONFIG_FILE_NAME = "server1.json"; //-c
    private static String CERTIFICATE_FILE_NAME = "star.godswila.guru.p12"; //-f
    private static String CERTIFICATE_PASSWORD = "labo2023"; //-p

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                switch (args[i].charAt(1)) {
                    case 'c' -> {
                        CONFIG_FILE_NAME = args[i + 1];
                        System.out.println("-c");
                    }
                    case 'f' -> {
                        CERTIFICATE_FILE_NAME = args[i + 1];
                        System.out.println("-f");
                    }
                    case 'p' -> {
                        CERTIFICATE_PASSWORD = args[i + 1];
                        System.out.println("-p");
                    }
                }
            }

        }
        NetChooser netChooser = new NetChooser();
        NetworkInterface selectedInterface = netChooser.getSelectedInterface();
        System.out.println("[*] Program started");
        ServerFactory serverFactory = new ServerFactory(CONFIG_FILE_NAME, CERTIFICATE_FILE_NAME, CERTIFICATE_PASSWORD, selectedInterface);
        serverFactory.start();
    }
}
