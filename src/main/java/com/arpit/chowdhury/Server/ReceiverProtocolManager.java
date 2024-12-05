package com.arpit.chowdhury.Server;

import edu.avo.udpiolibrary.IDataConsumer;

import java.net.InetAddress;

public class ReceiverProtocolManager implements IDataConsumer {
    private ICommandConsumer commandConsumer;


    public ReceiverProtocolManager(ICommandConsumer commandConsumer) {
        this.commandConsumer = commandConsumer;
    }

    @Override
    public void consumeData(byte[] bytes, int length, InetAddress inetAddress, int port) {
        byte command = bytes[0];
        switch (command) {
            case 1 -> commandConsumer.sendDownload(bytes, length, inetAddress, port);
            case 2 -> commandConsumer.sendReloadAvailableFiles(bytes, length, inetAddress, port);
            case 3 -> commandConsumer.sendUploadRequestAck(bytes, length, inetAddress, port);
            case 4 -> commandConsumer.sendUploadingAck(bytes, length, inetAddress, port);
        }
    }
}
