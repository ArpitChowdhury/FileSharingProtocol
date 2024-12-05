package com.arpit.chowdhury.Client;

import com.arpit.chowdhury.fileManager.FileManager;
import com.arpit.chowdhury.gui.Gui;
import com.arpit.chowdhury.gui.IEventObserver;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        ByteBuffer buffer = ByteBuffer.allocate(Client.BUFFER_SIZE);
        buffer.put((byte) 0x02);
        buffer.putInt(-1);
        sender.send(buffer.array(), ip, port);
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

        if (segmentId == -1) {
            gui.updateDownloadStatus(2, file);
            return;
        } else if (segmentId == -2) {
            gui.updateDownloadStatus(5, file);
            return;
        }

        Path path = Paths.get((Client.SAVED_FILES_PATH + file).trim());
        int INFO_SIZE = 3 + 4;
        boolean done = false;
        buffer.clear();
        byte[] fileData = new byte[Client.BUFFER_SIZE - INFO_SIZE];
        try {
            FileManager.readFile(path, fileData, segmentId, Client.BUFFER_SIZE, INFO_SIZE);
        } catch (IndexOutOfBoundsException e) {
            done = true;
        }

        buffer.put((byte) 0x04);
        buffer.put(segmentId);
        buffer.put((byte) (done ? 1 : 0));
        buffer.putInt(fileId);
        try {
            buffer.put(fileData);
            sender.send(buffer.array(), ip, port);
        } catch (IllegalArgumentException e) {
            gui.updateDownloadStatus(2, file);
        }


    }

    @Override
    public void updateAvailableFiles(byte[] bytes, int length, InetAddress inetAddress, int port) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(1);
        byte cleanByte = buffer.get();
        int indexFile = buffer.getInt();
        byte[] fileName = new byte[buffer.remaining()];
        buffer.get(fileName);
        if (fileName[0] != 0) {
            String file = new String(fileName);
            gui.updateAvailableFiles(file.trim(), indexFile, cleanByte == 1);

            buffer.clear();
            buffer.put((byte) 2);
            buffer.putInt(indexFile );
            sender.send(buffer.array(), ip, port);
        }
    }


    @Override
    public void requestUpload(String file) {
        ByteBuffer buffer = ByteBuffer.allocate(Client.BUFFER_SIZE);
        buffer.put((byte) 0x03);
        String sFileName = FileManager.getFileName(file);
        try {
            Path src = Paths.get(file);
            Path dst = Paths.get(Client.SAVED_FILES_PATH + sFileName);
            Files.copy(src, dst);
            gui.updateDownloadStatus(5, file);
        } catch (IOException e) {
            gui.updateDownloadStatus(4, file);
            return;
        }
        if (sFileName.length() < 50) {
            byte[] fileName = FileManager.getFileName(file).getBytes();
            buffer.put(fileName); // file name;
            sender.send(buffer.array(), ip, port);
            gui.updateDownloadStatus(7, file);
        } else {
            gui.updateDownloadStatus(4, file);
        }
    }
}
