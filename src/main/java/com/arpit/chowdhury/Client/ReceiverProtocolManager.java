package com.arpit.chowdhury.Client;

import edu.avo.udpiolibrary.IDataConsumer;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ReceiverProtocolManager implements IDataConsumer {
    private ICommandConsumer commandConsumer;


    public ReceiverProtocolManager(ICommandConsumer commandConsumer) {
        this.commandConsumer = commandConsumer;
    }

    @Override
    public void consumeData(byte[] bytes, int i, InetAddress inetAddress, int i1) {
        byte command = bytes[0];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(1);

        switch (command) {
            case 3 -> commandConsumer.uploadFile(bytes, i, inetAddress, i1);
        }
    }
}
