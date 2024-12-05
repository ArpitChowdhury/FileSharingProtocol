package com.arpit.chowdhury.fileManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class FileManager {
    public static byte[] getChuck(byte[] array, int index, int bufferSize, int infoSize) {
        int start = (bufferSize - infoSize) * index;
        int end = (bufferSize - infoSize) * (index + 1);

        if (start < 0) {
            throw new IllegalArgumentException("Indice di inizio non valido");
        }
        if (end > array.length) {
            end = array.length;
        }


        byte[] subArray = new byte[end - start];
        System.arraycopy(array, start, subArray, 0, subArray.length);
        return subArray;
    }

    public static String getFileName(String path) {
        String[] subPaths = path.split("[/\\\\]");
        return subPaths[subPaths.length - 1];
    }


    public static void writeFile(String path, int index, byte[] data) {
        try (RandomAccessFile raf = new RandomAccessFile(path.trim(), "rw")) {
            raf.seek((long) data.length * index);
            raf.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean createFile(String filePath) {
        File file = new File(filePath.trim());
        boolean fileSuccessful;
        try {
            fileSuccessful = file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileSuccessful;
    }


    public static void readFile(Path path, byte[] dst, byte segmentId, int bufferSize, int infoSize) {
        byte[] array = new byte[bufferSize - infoSize];
        boolean end = false;
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
            raf.seek((long) array.length * segmentId);
            int remaining = Math.toIntExact(raf.length() - raf.getFilePointer());
            if (remaining < array.length) {
                array = new byte[remaining];
                end = true;
            }

            raf.read(dst);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (end) throw new IndexOutOfBoundsException("END OF FILE");
    }
}
