package org.helmo.reseau.servers;

import javax.net.ssl.*;
import java.nio.file.Paths;

public class TLSSocketFactory {
    private final String certificatePath;
    private final String certificateName;
    private final String password;

    public TLSSocketFactory(String certificatePath, String certificateName,String password) {
        this.certificatePath = certificatePath;
        this.certificateName =  certificateName;
        this.password = password;
    }

    public SSLServerSocketFactory getServerSocketFactory(){
        String certificateAbsolutePath = Paths.get(certificatePath, certificateName).toAbsolutePath().toString();
        System.setProperty("javax.net.ssl.keyStore", certificateAbsolutePath);
        System.setProperty("javax.net.ssl.keyStorePassword", password);

        return (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
    }


}
