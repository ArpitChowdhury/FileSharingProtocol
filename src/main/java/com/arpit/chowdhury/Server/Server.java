package com.arpit.chowdhury.Server;

import edu.avo.udpiolibrary.Receiver;
import edu.avo.udpiolibrary.Sender;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static final int BUFFER_SIZE = 1024; // 1KiB
    public static final String SAVE_PATH = "C:\\Users\\Arpit\\IdeaProjects\\EsercitazioneC\\src\\main\\resources\\serverFiles\\";

    private final List<String> savedFilesName;
    private int nSavedFiles;

    public Server() {
        this.savedFilesName = new ArrayList<>();
        this.nSavedFiles = 0;
    }

    public void loadSavedFiles() {
        File dir = new File(SAVE_PATH);
        File[] files = dir.listFiles();

        if (files == null) {
            return;
        }
        for (File file : files) {
            savedFilesName.add(file.getName());
        }
    }


    public int getNSavedFiles() {
        return nSavedFiles;
    }


    public int addFile(String fileName) {
        int prev = nSavedFiles++;
        savedFilesName.add(fileName);
        return prev;
    }


    public String getFile(int fileId) {
        return savedFilesName.get(fileId);
    }

    public static void main(String[] args) throws SocketException {
        DatagramSocket ds = new DatagramSocket(60000);
        Sender s = new Sender(ds);
        SenderProtocolManager spm = new SenderProtocolManager(s);
        Server server = new Server();
        Application app = new Application(spm, server);
        Receiver receiver = new Receiver(ds, BUFFER_SIZE);
        ReceiverProtocolManager rpm = new ReceiverProtocolManager(app);
        receiver.setConsumer(rpm);
    }
}
