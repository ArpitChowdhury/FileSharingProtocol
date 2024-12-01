package com.arpit.chowdhury.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Gui extends JFrame implements IAppObserver {
    private JList<String> files;
    private JList<String> downloadList;
    private JButton downloadButton;
    private JButton reloadButton;
    private JLabel downloadStatus;
    private JPanel contentPane;
    private JButton uploadButton;
    private JTextArea filePath;
    private final DefaultListModel<String> downloadModel = new DefaultListModel<>();
    private final DefaultListModel<String> filesModel = new DefaultListModel<>();
    IEventObserver observer;

    public void setObserver(IEventObserver observer) {
        this.observer = observer;
    }

    public Gui(String title) {
        super(title);

        downloadList.setModel(downloadModel);
        files.setModel(filesModel);


        downloadButton.addActionListener(_ -> {
            int file = downloadList.getSelectedIndex();
            observer.requestDownload(file);
        });
        reloadButton.addActionListener(_ -> observer.requestAvailableFiles());
        setContentPane(contentPane);
        pack();
        setVisible(true);
        uploadButton.addActionListener(_ -> observer.requestUpload(filePath.getText()));
    }

    @Override
    public void updateAvailableFiles(String[] files) {
        filesModel.addAll(List.of(files));
    }

    @Override
    public void updateDownloadStatus(int ok, String file) {
        switch (ok) {
            case 0 -> {
                downloadStatus.setText("Download OK");
                filesModel.addElement(file);
            }
            case 1 -> downloadStatus.setText("Downloading....");
            case 2 -> downloadStatus.setText("Upload \"" + file + "\" Already Exists");
            case 3 -> downloadStatus.setText("Upload \" " + file + "\" Not Found");
            case 4 -> downloadStatus.setText("Upload \" " + file + "\" Failed: File Name Too Big");
            case 5 -> downloadStatus.setText("Upload Ok");
            default -> downloadStatus.setText("Download Failed");
        }
    }
}
