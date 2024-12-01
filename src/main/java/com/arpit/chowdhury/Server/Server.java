package com.arpit.chowdhury.Server;

import edu.avo.udpiolibrary.Receiver;
import edu.avo.udpiolibrary.Sender;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static final int BUFFER_SIZE = 1024 * 2; // 2KiB
    public static final String SAVE_PATH = "C:\\Users\\Arpit\\IdeaProjects\\EsercitazioneC\\src\\main\\resources\\serverFiles\\";

    private final List<String> savedFilesName;
    private int nSavedFiles;

    public Server() {
        this.savedFilesName = new ArrayList<>();
        this.nSavedFiles = 0;
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

    public static void main(String[] args) throws SocketException, UnknownHostException {
        DatagramSocket ds = new DatagramSocket(6969);
        Sender s = new Sender(ds);
        SenderProtocolManager spm = new SenderProtocolManager(s);
        Server server = new Server();
        Application app = new Application(spm, server, InetAddress.getByName("localhost"), 6969);
        Receiver receiver = new Receiver(ds, BUFFER_SIZE);
        ReceiverProtocolManager rpm = new ReceiverProtocolManager(app);
        receiver.setConsumer(rpm);
    }
}
