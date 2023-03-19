package org.helmo.reseau.grammar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Method;


public class ProtocolTest {

    private static Protocol protocol;
    @BeforeAll
    public static void before() throws Exception {
        protocol = new Protocol();
    }
    /**
     *
     * Method: buildHello(String domaine, String random22)
     *
     */
    @Test
    public void testBuildHello() throws Exception {
        String domain = "server1.godswila.guru";
        String random22 = "1234567890123456789012";
        String helloMessage = protocol.buildHello(domain, random22);

        Method method = Protocol.class.getDeclaredMethod("verifyHello", String.class);
        method.setAccessible(true);

        boolean result = method.invoke(protocol, helloMessage) != null;

        assert result;
    }

    /**
     *
     * Method: buildParam(String round, String salt)
     *
     */
    @Test
    public void testBuildParam() throws Exception {
        //PARAM 14 N3YjFRMbl44oVAlUvBxmuu
        String round = "14";
        String salt = "N3YjFRMbl44oVAlUvBxmuu";
        String paramMessage = protocol.buildParam(round, salt);

        Method method = Protocol.class.getDeclaredMethod("verifyParam", String.class);
        method.setAccessible(true);

        boolean result = method.invoke(protocol, paramMessage) != null;

        assert result;
    }

    /**
     *
     * Method: buildMsgs(String userAndDomain, String message)
     *
     */
    @Test
    public void testBuildMsgs() throws Exception {
        String userAndDomain = "tester@server1.godswila.guru";
        String message = "Hello world";
        String msgsMessage = protocol.buildMsgs(userAndDomain, message);

        Method method = Protocol.class.getDeclaredMethod("verifyMsgs", String.class);
        method.setAccessible(true);

        boolean result = method.invoke(protocol, msgsMessage) != null;

        assert result;
    }

    /**
     *
     * Method: buildOk(String message)
     *
     */
    @Test
    public void testBuildOk() throws Exception {
        String message = "OK";
        String okMessage = protocol.buildOk(message);

        Method method = Protocol.class.getDeclaredMethod("verifyOk", String.class);
        method.setAccessible(true);

        boolean result = method.invoke(protocol, okMessage) != null;

        assert result;

    }

    /**
     *
     * Method: buildError(String message)
     *
     */
    @Test
    public void testBuildError() throws Exception {
        String message = "ERROR";
        String errorMessage = protocol.buildError(message);

        Method method = Protocol.class.getDeclaredMethod("verifyError", String.class);
        method.setAccessible(true);

        boolean result = method.invoke(protocol, errorMessage) != null;

        assert result;
    }

    /**
     *
     * Method: buildEcho(String port, String domain)
     *
     */
    @Test
    public void testBuildEcho() throws Exception {
//TODO: Test goes here...
    }

    /**
     *
     * Method: buildSend(String idDomain, String domainName, String nameOrTagDomain, String internalMessage)
     *
     */
    @Test
    public void testBuildSend() throws Exception {
//TODO: Test goes here...
    }


    /**
     *
     * Method: verifyHello(String message)
     *
     */
    @Test
    public void testVerifyHello() throws Exception {
        String hello = "HELLO server1.godswila.guru 5djNxTJVZb5j&*EGMv&dJ7";
        String[] result = protocol.verifyMessage(hello);
        //Si le message est juste, il doit retourner un tableau avec en premier HELLO sinon -ERR

        assert result[0].equals("HELLO");
        assert result[1].equals("server1.godswila.guru");
        assert result[2].equals("5djNxTJVZb5j&*EGMv&dJ7");
    }

    /**
     *
     * Method: verifyConnect(String message)
     *
     */
    @Test
    public void testVerifyConnect() throws Exception {
        String connect = "CONNECT godswila";
        String[] result = protocol.verifyMessage(connect);

        assert result[0].equals("CONNECT");
        assert result[1].equals("godswila");
    }

    /**
     *
     * Method: verifyParam(String message)
     *
     */
    @Test
    public void testVerifyParam() throws Exception {
        String param = "PARAM 14 N3YjFRMbl44oVAlUvBxmuu";
        String[] result = protocol.verifyMessage(param);

        assert result[0].equals("PARAM");
        assert result[1].equals("14");
        assert result[2].equals("N3YjFRMbl44oVAlUvBxmuu");
    }

    /**
     *
     * Method: verifyConfirm(String message)
     *
     */
    @Test
    public void testVerifyConfirm() throws Exception {
        String confirm = "CONFIRM c19c79e62f94961940d696aa31a0b7b5b90e1a82d7c5d1ac3f81662f88e899ba";
        String[] result = protocol.verifyMessage(confirm);

        assert result[0].equals("CONFIRM");
        assert result[1].equals("c19c79e62f94961940d696aa31a0b7b5b90e1a82d7c5d1ac3f81662f88e899ba");
    }

    /**
     *
     * Method: verifyRegister(String message)
     *
     */
    @Test
    public void testVerifyRegister() throws Exception {
        String register = "REGISTER godswila 22 $2b$14$N3YjFRMbl44oVAlUvBxmuuk9vVOLb.tPeTEMpObQG/M1nSs8vTa0m";
        String[] result = protocol.verifyMessage(register);

        assert result[0].equals("REGISTER");
        assert result[1].equals("godswila");
        assert result[2].equals("22");
        assert result[3].equals("$2b$14$N3YjFRMbl44oVAlUvBxmuuk9vVOLb.tPeTEMpObQG/M1nSs8vTa0m");
    }

    /**
     *
     * Method: verifyFollow(String message)
     *
     */
    @Test
    public void testVerifyFollowName() throws Exception {
        String follow = "FOLLOW godswila@server1.godswila.guru";
        String secondFollow = "FOLLOW Mouchakk@server2.godswila.guru";
        String[] result = protocol.verifyMessage(follow);
        String[] secondResult = protocol.verifyMessage(secondFollow);

        assert result[0].equals("FOLLOW");
        assert result[1].equals("godswila@server1.godswila.guru");

        assert secondResult[0].equals("FOLLOW");
        assert secondResult[1].equals("Mouchakk@server2.godswila.guru");
    }

    /**
     *
     * Method: verifyFollow(String message)
     *
     */
    @Test
    public void testVerifyFollowTag() throws Exception {
        String follow = "FOLLOW #tagged@server1.godswila.guru";
        String[] result = protocol.verifyMessage(follow);
        System.out.println(result[0]);
        System.out.println(result[1]);
        assert result[0].equals("FOLLOW");
        assert result[1].equals("#tagged@server1.godswila.guru");
    }

    /**
     *
     * Method: verifyMsg(String message)
     *
     */
    @Test
    public void testVerifyMsg() throws Exception {
        String msg = "MSG Hey !";
        String[] result = protocol.verifyMessage(msg);

        assert result[0].equals("MSG");
        assert result[1].equals("Hey !");
    }

    /**
     *
     * Method: verifyMsgs(String message)
     *
     */
    @Test
    public void testVerifyMsgs() throws Exception {
        String msgs = "MSGS tester@server1.godswila.guru Hey !";
        String[] result = protocol.verifyMessage(msgs);

        assert result[0].equals("MSGS");
        assert result[1].equals("tester@server1.godswila.guru");
        assert result[2].equals("Hey !");
    }

    /**
     *
     * Method: verifyOk(String message)
     *
     */
    @Test
    public void testVerifyOk() throws Exception {
        String ok = "+OK Message";
        String[] result = protocol.verifyMessage(ok);

        assert result[0].equals("+OK");
        assert result[1].equals("Message");
    }

    /**
     *
     * Method: verifyError(String message)
     *
     */
    @Test
    public void testVerifyError() throws Exception {
        String error = "-ERR Message";
        String[] result = protocol.verifyMessage(error);

        assert result[0].equals("-ERR");
        assert result[1].equals("Message");
    }

    /**
     *
     * Method: verifyDisconnect(String message)
     *
     */
    @Test
    public void testVerifyDisconnect() throws Exception {
        String disconnect = "DISCONNECT";
        String[] result = protocol.verifyMessage(disconnect);

        assert result[0].equals("DISCONNECT");

    }

    /**
     *
     * Method: verifyEcho(String message)
     *
     */
    @Test
    public void testVerifyEcho() throws Exception {
//TODO: Test goes here...
    }

    /**
     *
     * Method: verifySend(String message)
     *
     */
    @Test
    public void testVerifySend() throws Exception {
        String send = "SEND 1@server1.godswila.guru Mouchakk@server1.godswila.guru godswila@server2.godswila.guru FOLLOW godswila@server2.godswila.guru";
        String Secondsend = "SEND 1@server1.godswila.guru Mouchakk@server1.godswila.guru #tagged@server2.godswila.guru FOLLOW #tagged@server2.godswila.guru";
        String[] result = protocol.verifyMessage(send);
        String[] secondResult = protocol.verifyMessage(Secondsend);

        assert result[0].equals("SEND");
        assert result[1].equals("1@server1.godswila.guru");
        assert result[2].equals("Mouchakk@server1.godswila.guru");
        assert result[3].equals("godswila@server2.godswila.guru");
        assert result[4].equals("FOLLOW godswila@server2.godswila.guru");

        assert secondResult[0].equals("SEND");
        assert secondResult[1].equals("1@server1.godswila.guru");
        assert secondResult[2].equals("#tagged@server1.godswila.guru");
        assert secondResult[3].equals("godswila@server2.godswila.guru");
        assert secondResult[4].equals("FOLLOW #tagged@server2.godswila.guru");
    }

}
