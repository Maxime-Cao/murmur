package org.helmo.reseau.grammar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Protocol {
    // Définitions standards :
    private final String RX_CHIFFRE = "0-9";
    private final String RX_LETTRE = "a-zA-Z";
    private final String RX_LETTRE_CHIFFRE = String.format("%s%s", RX_LETTRE, RX_CHIFFRE);
    private final String RX_CARACTERE_IMPRIMABLE = "[\\x20-\\xff]";
    private final String RX_CRLF = "\\x0d\\x0a"; // OU => \\r\\n
    private final String RX_SYMBOLE = "[\\x21-\\x2f\\x3a-\\x40\\x5b-\\x60]";
    private final String RX_ESP = "\\x20";
    private final String RX_DOMAINE = String.format("[%s%s]{5,200}", RX_LETTRE_CHIFFRE, "\\x2e");
    private final String RX_PORT = String.format("[%s]{1,5}", RX_CHIFFRE);
    private final String RX_ROUND = String.format("[%s]{2}", RX_CHIFFRE);
    private final String RX_BCRYPT_HASH = String.format("\\$2b\\$%s\\$[%s%s]{1,70}", RX_ROUND, RX_LETTRE_CHIFFRE, RX_SYMBOLE);
    private final String RX_SHA3_HEX = String.format("[%s]{30,200}", RX_LETTRE_CHIFFRE);
    private final String RX_SALT_SIZE = String.format("[%s]{2}", RX_CHIFFRE);
    private final String RX_RANDOM22 = String.format("[%s%s]{22}", RX_LETTRE_CHIFFRE, RX_SYMBOLE);
    private final String RX_SALT = RX_RANDOM22;
    private final String RX_MESSAGE = String.format("%s{1,200}", RX_CARACTERE_IMPRIMABLE);
    private final String RX_MESSAGE_INTERNE = String.format("%s{1,500}", RX_CARACTERE_IMPRIMABLE);
    private final String RX_NOM_UTILISATEUR = String.format("[%s]{5,20}", RX_LETTRE_CHIFFRE);
    private final String RX_TAG = String.format("#([%s]{5,20})", RX_LETTRE_CHIFFRE);
    private final String RX_NOM_DOMAINE = String.format("%s@%s", RX_NOM_UTILISATEUR, RX_DOMAINE);
    private final String RX_TAG_DOMAINE = String.format("%s@%s", RX_TAG, RX_DOMAINE);
    private final String RX_ID_DOMAINE = String.format("[%s]{1,5}@%s", RX_CHIFFRE, RX_DOMAINE);

    // Echanges entre le client et le Murmur Server :
    private final String RX_HELLO = String.format("(HELLO)%s(%s)%s(%s)(%s){0,1}", RX_ESP, RX_DOMAINE, RX_ESP, RX_RANDOM22, RX_CRLF);
    private final String RX_CONNECT = String.format("(CONNECT)%s(%s)(%s){0,1}", RX_ESP, RX_NOM_UTILISATEUR, RX_CRLF);
    private final String RX_PARAM = String.format("(PARAM)%s(%s)%s(%s)(%s){0,1}", RX_ESP, RX_ROUND, RX_ESP, RX_SALT, RX_CRLF);
    private final String RX_CONFIRM = String.format("(CONFIRM)%s(%s)(%s){0,1}", RX_ESP, RX_SHA3_HEX, RX_CRLF);
    private final String RX_REGISTER = String.format("(REGISTER)%s(%s)%s(%s)%s(%s)(%s){0,1}", RX_ESP, RX_NOM_UTILISATEUR, RX_ESP, RX_SALT_SIZE, RX_ESP, RX_BCRYPT_HASH, RX_CRLF);
    private final String RX_FOLLOW = String.format("(FOLLOW)%s(%s|%s)(%s){0,1}", RX_ESP, RX_NOM_DOMAINE, RX_TAG_DOMAINE, RX_CRLF);
    private final String RX_MSG = String.format("(MSG)%s(%s)(%s){0,1}", RX_ESP, RX_MESSAGE, RX_CRLF);
    private final String RX_MSGS = String.format("(MSGS)%s(%s)%s(%s)(%s){0,1}", RX_ESP, RX_NOM_DOMAINE, RX_ESP, RX_MESSAGE, RX_CRLF);
    private final String RX_OK = String.format("(\\+OK)%s(%s)(%s){0,1}", RX_ESP, RX_MESSAGE, RX_CRLF);
    private final String RX_ERROR = String.format("(\\-ERR)%s(%s)(%s){0,1}", RX_ESP, RX_MESSAGE, RX_CRLF);
    private final String RX_DISCONNECT = String.format("(DISCONNECT)(%s){0,1}", RX_CRLF);

    // Echanges multicast entre Murmur Server et Murmur Relay :
    private final String RX_ECHO = String.format("(ECHO)%s(%s)%s(%s)(%s){0,1}", RX_ESP, RX_PORT, RX_ESP, RX_DOMAINE, RX_CRLF);

    // Echanges unicast entre Murmur Server et Murmur Relay :
    private final String RX_SEND = String.format("(SEND)%s(%s)%s(%s|%s)%s(%s|%s)%s(%s)(%s){0,1}", RX_ESP, RX_ID_DOMAINE, RX_ESP, RX_NOM_DOMAINE, RX_TAG_DOMAINE, RX_ESP, RX_NOM_DOMAINE, RX_TAG_DOMAINE, RX_ESP, RX_MESSAGE_INTERNE, RX_CRLF);
    // Messages pour la construction :
    private final String HELLO = "HELLO <domaine> <random22>";
    private final String CONNECT = "CONNECT <nom-utilisateur>";
    private final String PARAM = "PARAM <round> <salt>";
    private final String CONFIRM = "CONFIRM <sha3>";
    private final String REGISTER = "REGISTER <nom-utilisateur> <salt-size> <bcrypt-hash>";
    private final String FOLLOW = "FOLLOW <nom-ou-tag-domaine>";
    private final String MSG = "MSG <message>";
    private final String MSGS = "MSGS <nom-domaine> <message>";
    private final String OK = "+OK <message>";
    private final String ERROR = "-ERR <message>";
    private final String DISCONNECT = "DISCONNECT";
    private final String ECHO = "ECHO <port> <domaine>";
    private final String SEND = "SEND <id-domaine> <nom-domaine> <nom-ou-tag-domaine> <message-interne>";

    public String buildHello(String domaine, String random22) {
        String helloMessage = HELLO.replaceAll("<domaine>", domaine).replaceAll("<random22>", random22);
        if (helloMessage.matches(RX_HELLO)) {
            return helloMessage;
        } else {
            throw new IllegalArgumentException("Please provide correct arguments for the construction of the HELLO message");
        }
    }


    public String buildParam(String round, String salt) {
        String paramMessage = PARAM.replaceAll("<round>", round).replaceAll("<salt>", salt);
        if (paramMessage.matches(RX_PARAM)) {
            return paramMessage;
        } else {
            throw new IllegalArgumentException("Please provide correct arguments for the construction of the PARAM message");
        }
    }


    public String buildMsgs(String domainName, String message) {
        String msgsMessage = MSGS.replaceAll("<nom-domaine>", domainName).replaceAll("<message>", message);
        if (msgsMessage.matches(RX_MSGS)) {
            return msgsMessage;
        } else {
            throw new IllegalArgumentException("Please provide correct arguments for the construction of the MSGS message");
        }
    }

    public String buildOk(String message) {
        String okMessage = OK.replaceAll("<message>", message);
        if (okMessage.matches(RX_OK)) {
            return okMessage;
        } else {
            throw new IllegalArgumentException("Please provide correct argument for the construction of the OK message");
        }
    }

    public String buildError(String message) {
        String errorMessage = ERROR.replaceAll("<message>", message);
        if (errorMessage.matches(RX_ERROR)) {
            return errorMessage;
        } else {
            throw new IllegalArgumentException("Please provide correct argument for the construction of the ERROR message");
        }
    }


    public String buildEcho(String port, String domain) {
        String echoMessage = ECHO.replace("<port>", port).replaceAll("<domaine>", domain);
        if (echoMessage.matches(RX_ECHO)) {
            return echoMessage;
        } else {
            throw new IllegalArgumentException("Please provide correct arguments for the construction of the ECHO message");
        }
    }

    public String buildSend(String idDomain, String domainName, String nameOrTagDomain, String internalMessage) {
        String sendMessage = SEND.replaceAll("<id-domaine>", idDomain).replaceAll("<nom-domaine>", domainName).replaceAll("<nom-ou-tag-domaine>", nameOrTagDomain).replaceAll("<message-interne>", internalMessage);
        if (sendMessage.matches(RX_SEND)) {
            return sendMessage;
        } else {
            throw new IllegalArgumentException("Please provide correct arguments for the construction of the SEND message");
        }
    }

    public String[] verifyMessage(String message) {
        if (message != null) {
            switch (message.split(" ")[0].trim()) {
                case "HELLO" -> {
                    return verifyHello(message);
                }
                case "CONNECT" -> {
                    return verifyConnect(message);
                }
                case "PARAM" -> {
                    return verifyParam(message);
                }
                case "CONFIRM" -> {
                    return verifyConfirm(message);
                }
                case "REGISTER" -> {
                    return verifyRegister(message);
                }
                case "FOLLOW" -> {
                    return verifyFollow(message);
                }
                case "MSG" -> {
                    return verifyMsg(message);
                }
                case "MSGS" -> {
                    return verifyMsgs(message);
                }
                case "+OK" -> {
                    return verifyOk(message);
                }
                case "-ERR" -> {
                    return verifyError(message);
                }
                case "DISCONNECT" -> {
                    return verifyDisconnect(message);
                }
                case "ECHO" -> {
                    return verifyEcho(message);
                }
                case "SEND" -> {
                    return verifySend(message);
                }
                default -> {
                    return new String[]{"-ERR", "The message does not exist in this protocol"};
                }
            }
        } else {
            return new String[]{"-ERR", "The message must be NOT NULL"};
        }
    }

    private String[] verifyHello(String message) {
        Pattern p = Pattern.compile(RX_HELLO);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2), m.group(3)};
        }
        return new String[]{"-ERR", "This HELLO message is not correctly formatted"};
    }

    private String[] verifyConnect(String message) {
        Pattern p = Pattern.compile(RX_CONNECT);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2)};
        }
        return new String[]{"-ERR", "This CONNECT message is not correctly formatted"};
    }

    private String[] verifyParam(String message) {
        Pattern p = Pattern.compile(RX_PARAM);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2), m.group(3)};
        }
        return new String[]{"-ERR", "This PARAM message is not correctly formatted"};
    }

    private String[] verifyConfirm(String message) {
        Pattern p = Pattern.compile(RX_CONFIRM);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2)};
        }
        return new String[]{"-ERR", "This CONFIRM message is not correctly formatted"};
    }

    private String[] verifyRegister(String message) {
        Pattern p = Pattern.compile(RX_REGISTER);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2), m.group(3), m.group(4)};
        }
        return new String[]{"-ERR", "This REGISTER message is not correctly formatted"};
    }

    private String[] verifyFollow(String message) {
        Pattern p = Pattern.compile(RX_FOLLOW);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2)};
        }
        return new String[]{"-ERR", "This FOLLOW message is not correctly formatted"};
    }

    private String[] verifyMsg(String message) {
        Pattern p = Pattern.compile(RX_MSG);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2)};
        }
        return new String[]{"-ERR", "This MSG message is not correctly formatted"};
    }

    private String[] verifyMsgs(String message) {
        Pattern p = Pattern.compile(RX_MSGS);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2), m.group(3)};
        }
        return new String[]{"-ERR", "This MSGS message is not correctly formatted"};
    }

    private String[] verifyOk(String message) {
        Pattern p = Pattern.compile(RX_OK);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2)};
        }
        return new String[]{"-ERR", "This OK message is not correctly formatted"};
    }

    private String[] verifyError(String message) {
        Pattern p = Pattern.compile(RX_ERROR);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2)};
        }
        return new String[]{"-ERR", "This ERROR message is not correctly formatted"};
    }

    private String[] verifyDisconnect(String message) {
        Pattern p = Pattern.compile(RX_DISCONNECT);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1)};
        }
        return new String[]{"-ERR", "This DISCONNECT message is not correctly formatted"};
    }

    private String[] verifyEcho(String message) {
        Pattern p = Pattern.compile(RX_ECHO);
        Matcher m = p.matcher(message);

        if (m.matches()) {
            return new String[]{m.group(1), m.group(2), m.group(3)};
        }
        return new String[]{"-ERR", "This ECHO message is not correctly formatted"};
    }

    private String[] verifySend(String message) {
        Pattern p = Pattern.compile(RX_SEND);
        Matcher m = p.matcher(message);
        if (m.matches()) {
            return new String[]{m.group(1), m.group(2), m.group(3), m.group(5), m.group(7)};
        }
        return new String[]{"-ERR", "This SEND message is not correctly formatted"};
    }
}