package com.arpit.chowdhury.Server;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Application implements ICommandConsumer {

    private SenderProtocolManager sender;
    private Server server;

    public Application(SenderProtocolManager sender, Server server, InetAddress ip, int port) {
        this.sender = sender;
        this.server = server;
    }


    @Override
    public void sendDownload(byte[] bytes, int length, InetAddress inetAddress, int port) {

    }

    @Override
    public void sendUploadRequestAck(byte[] bytes, int length, InetAddress inetAddress, int port) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(1);
        byte[] fileName = new byte[buffer.remaining()];
        buffer.get(1, fileName);
        String fileNameString = new String(fileName);
        String fileFullPath = Server.SAVE_PATH + fileNameString;
        File file = new File(fileFullPath.trim());
        boolean fileSuccessful;
        try {
            fileSuccessful = file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        buffer.clear();

        int fileId;
        if (fileSuccessful) {
            fileId = server.addFile(fileNameString);
            buffer.put((byte) 3);
            buffer.put((byte) 0);
            buffer.putInt(fileId);
        } else {
            buffer.put((byte) 3);
            buffer.putInt(-1);
        }

        buffer.put(fileNameString.trim().getBytes());

        sender.send(buffer.array(), inetAddress, port);
    }

    @Override
    public void sendUploadingAck(byte[] bytes, int length, InetAddress inetAddress, int port) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte segmentId = buffer.get(1);
        int fileId = buffer.getInt();
        int fileSize = buffer.remaining();
        byte[] fileData = new byte[fileSize];
        buffer.get(fileData);

        String fileNameString = Server.SAVE_PATH + server.getFile(fileId);
        try (RandomAccessFile raf = new RandomAccessFile(fileNameString, "rw")) {
            raf.seek((long) fileSize * segmentId);
            raf.write(fileData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        buffer.clear();
        buffer.put((byte) 3);
        buffer.put((byte) (segmentId + 1));
        buffer.putInt(fileId);

        sender.send(buffer.array(), inetAddress, port);

    }
}
