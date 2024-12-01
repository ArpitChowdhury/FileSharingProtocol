package com.arpit.chowdhury.Client;

import java.net.InetAddress;

public interface ICommandConsumer {
    void uploadFile(byte[] data, int length, InetAddress address, int port);
}
