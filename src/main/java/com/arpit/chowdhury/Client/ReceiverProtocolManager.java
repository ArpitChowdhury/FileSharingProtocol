package com.arpit.chowdhury.Client;

import edu.avo.udpiolibrary.IDataConsumer;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ReceiverProtocolManager implements IDataConsumer {
    private final ICommandConsumer commandConsumer;


    public ReceiverProtocolManager(ICommandConsumer commandConsumer) {
        this.commandConsumer = commandConsumer;
    }

    @Override
    public void consumeData(byte[] bytes, int length, InetAddress inetAddress, int port) {
        byte command = bytes[0];

        switch (command) {
            case 2 -> commandConsumer.updateAvailableFiles(bytes, length, inetAddress, port);
            case 3 -> commandConsumer.uploadFile(bytes, length, inetAddress, port);
        }
    }
}
