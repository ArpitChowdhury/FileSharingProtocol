package com.arpit.chowdhury.Server;

import java.net.InetAddress;

public interface ICommandConsumer {
    void sendDownload(byte[] bytes, int length, InetAddress inetAddress, int port);

    void sendUploadRequestAck(byte[] bytes, int length, InetAddress inetAddress, int port);

    void sendUploadingAck(byte[] bytes, int length, InetAddress inetAddress, int port);

    void sendReloadAvailableFiles(byte[] bytes, int length, InetAddress inetAddress, int port);
}
