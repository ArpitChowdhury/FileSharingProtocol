package com.arpit.chowdhury.Client;

import com.arpit.chowdhury.fileManager.FileManager;
import com.arpit.chowdhury.gui.Gui;
import com.arpit.chowdhury.gui.IEventObserver;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application implements IEventObserver, ICommandConsumer {
    private final Gui gui;
    private final SenderProtocolManager sender;
    private InetAddress ip;
    private int port;

    public Application(SenderProtocolManager sender, InetAddress ip, int port) {
        this.gui = new Gui("File Downloader");
        gui.setObserver(this);
        this.sender = sender;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void requestDownload(int fileId) {
        ByteBuffer buffer = ByteBuffer.allocate(4 + 1);
        buffer.put((byte) 0x01); // download
        buffer.putInt(fileId); // file name
        sender.send(buffer.array(), ip, port);
    }

    @Override
    public void requestAvailableFiles() {

    }

    @Override
    public void uploadFile(byte[] data, int length, InetAddress ip, int port) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.position(1);
        byte segmentId = buffer.get();
        int fileId = buffer.getInt();
        byte[] fileName = new byte[buffer.remaining()];
        buffer.get(fileName);
        String file = new String(fileName);
        Path path = Paths.get(file.trim());
        byte[] fileData = null;

        try {
            fileData = Files.readAllBytes(path);
        } catch (IOException e) {
            gui.updateDownloadStatus(6, file);
            return;
        }

        buffer.clear();
        buffer.put((byte) 0x04);
        buffer.put(segmentId);
        buffer.putInt(fileId);
        try {
            buffer.put(FileManager.getChuck(fileData, segmentId, Client.BUFFER_SIZE, 2 + 4)); // 2 bytes + 4 byte per int
            sender.send(buffer.array(), ip, port);
        } catch (IllegalArgumentException e) {
            gui.updateDownloadStatus(2, file);
        }


    }


    @Override
    public void requestUpload(String file) {
        ByteBuffer buffer = ByteBuffer.allocate(Client.BUFFER_SIZE);
        buffer.put((byte) 0x03); // uploading...
        String sFileName = FileManager.getFileName(file);
        if (sFileName.length() < 50) {
            byte[] fileName = FileManager.getFileName(file).getBytes();
            buffer.put(fileName); // file name;
            sender.send(buffer.array(), ip, port);

        } else {
            gui.updateDownloadStatus(4, file);
        }
    }

}
