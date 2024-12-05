package com.arpit.chowdhury.Server;


import com.arpit.chowdhury.fileManager.FileManager;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Application implements ICommandConsumer {

    private SenderProtocolManager sender;
    private Server server;

    public Application(SenderProtocolManager sender, Server server) {
        this.sender = sender;
        this.server = server;
        server.loadSavedFiles();
    }


    @Override
    public void sendDownload(byte[] bytes, int length, InetAddress inetAddress, int port) {

    }

    @Override
    public void sendUploadRequestAck(byte[] bytes, int length, InetAddress inetAddress, int port) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(1);
        byte[] fileName = new byte[buffer.remaining()];
        buffer.get(fileName);
        String fileNameString = new String(fileName);
        String fileFullPath = Server.SAVE_PATH + fileNameString;
        boolean result = FileManager.createFile(fileFullPath);

        buffer.clear();

        int fileId;
        if (result) {
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


    public void sendMaxSegmentReached(ByteBuffer buffer, int fileId, String file, InetAddress inetAddress, int port) {
        buffer.put((byte) 3);
        buffer.put((byte) -2);
        buffer.putInt(fileId);

        buffer.put(file.getBytes());
        cleanRestOfBuffer(buffer);

        sender.send(buffer.array(), inetAddress, port);

    }


    @Override
    public void sendUploadingAck(byte[] bytes, int length, InetAddress inetAddress, int port) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(1);
        byte segmentId = buffer.get();
        byte done = buffer.get();
        int fileId = buffer.getInt();
        int fileSize = buffer.remaining();
        byte[] fileData = new byte[fileSize];
        buffer.get(fileData);

        String fileNameString = Server.SAVE_PATH + server.getFile(fileId);
        FileManager.writeFile(fileNameString, fileSize * segmentId, fileData);

        buffer.clear();

        if (segmentId == 255) {
            sendMaxSegmentReached(buffer, fileId, fileNameString, inetAddress, port);
        }
        if (done == 0) askForSegment(buffer, (byte) (segmentId + 1), fileId, inetAddress, port);
    }

    private void askForSegment(ByteBuffer buffer, byte segmentId, int fileId, InetAddress inetAddress, int port) {
        buffer.put((byte) 3);
        buffer.put(segmentId);
        buffer.putInt(fileId);
        buffer.put(FileManager.getFileName(server.getFile(fileId).trim()).getBytes());
        cleanRestOfBuffer(buffer);

        sender.send(buffer.array(), inetAddress, port);

    }

    private void cleanRestOfBuffer(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.put((byte) 0);
        }
    }

    @Override
    public void sendReloadAvailableFiles(byte[] bytes, int length, InetAddress inetAddress, int port) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(1);
        int lastFileIndex = buffer.getInt();
        buffer.clear();
        buffer.put((byte) 2);
        if (lastFileIndex < 0) buffer.put((byte) 1);
        else buffer.put((byte) 0);

        try {
            buffer.putInt(lastFileIndex + 1);
            buffer.put(server.getFile(lastFileIndex + 1).trim().getBytes());
        } catch (IndexOutOfBoundsException e) {
            cleanRestOfBuffer(buffer);
        }

        sender.send(buffer.array(), inetAddress, port);
    }
}
