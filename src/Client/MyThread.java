package client;

import java.io.File;
import javax.swing.*;
import java.awt.BorderLayout;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class MyThread extends Thread {
    private final File videoFile;

    public MyThread(File videoFile) {
        this.videoFile = videoFile;
    }

    @Override
    public void run() {
        try {
            // Création de l'interface pour jouer la vidéo
            if(videoFile.length()<1024*1024){
                Thread.sleep(100);
            }
            JFrame frame = new JFrame("Lecture Vidéo");
            EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());
            frame.add(mediaPlayerComponent, BorderLayout.CENTER);
            frame.setVisible(true);

            // Lecture du fichier vidéo
            System.out.println("Lecture du fichier : " + videoFile.getAbsolutePath());
            mediaPlayerComponent.mediaPlayer().media().play(videoFile.getAbsolutePath());

            // Suppression après fermeture
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    mediaPlayerComponent.release();
                    if (videoFile.delete()) {
                        System.out.println("Fichier supprimé : " + videoFile.getName());
                    }
                    System.exit(0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}