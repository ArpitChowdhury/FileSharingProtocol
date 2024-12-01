package com.arpit.chowdhury.fileManager;

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



}
