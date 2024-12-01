/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.arpit.chowdhury.gui;

/**
 * @author palma
 */
public interface IEventObserver {
    void requestDownload(int fileId);

    void requestAvailableFiles();

    void requestUpload(String file);
}
