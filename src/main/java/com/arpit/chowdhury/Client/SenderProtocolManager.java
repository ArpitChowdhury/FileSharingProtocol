package com.arpit.chowdhury.Client;

import edu.avo.udpiolibrary.Sender;

import java.net.InetAddress;

public class SenderProtocolManager {
    private final Sender sender;

    public SenderProtocolManager(Sender sender) {
        this.sender = sender;
    }

    public void send(byte[] data, InetAddress address, int port) {
        sender.send(data, address, port);
    }

}
