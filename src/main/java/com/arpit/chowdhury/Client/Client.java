package com.arpit.chowdhury.Client;

import edu.avo.udpiolibrary.Receiver;
import edu.avo.udpiolibrary.Sender;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    public static final int BUFFER_SIZE = 1024 * 2; // 2KiB

    public static void main(String[] args) throws SocketException, UnknownHostException {
        DatagramSocket ds = new DatagramSocket();
        Sender s = new Sender(ds);
        SenderProtocolManager spm = new SenderProtocolManager(s);
        Application app = new Application(spm, InetAddress.getByName("localhost"), 6969);
        Receiver receiver = new Receiver(ds, BUFFER_SIZE);
        ReceiverProtocolManager rpm = new ReceiverProtocolManager(app);
        receiver.setConsumer(rpm);


    }
}
