package com.aor.bouncy;

import java.io.*;
import  java.net.*;

public class ServerClient {

    private String OUT_SENTENCE;
    private String IN_SENTENCE;

    private Socket connectionSocket;
    private ServerSocket serverSocket;

    private BufferedReader bufferedReader;
    private DataOutputStream dataOutputStream;

    private static int PORT = 6789;


    public ServerClient(boolean IS_SERVER, String ipAdress) throws IOException {

        if (IS_SERVER) {
            serverSocket = new ServerSocket(PORT);
            connectionSocket = serverSocket.accept();

        }
        else {
            serverSocket = null;
            connectionSocket = new Socket(ipAdress, PORT);
        }

        bufferedReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());
    }

    public void update() throws IOException {
        IN_SENTENCE = bufferedReader.readLine();
        dataOutputStream.writeBytes(OUT_SENTENCE);
    }

    public String getIN_SENTENCE() {
        return IN_SENTENCE;
    }

    public void setOUT_SENTENCE(String OUT_SENTENCE) {
        this.OUT_SENTENCE = OUT_SENTENCE;
    }
}
