package client;

import java.io.*;
import java.net.Socket;

import javax.swing.SwingUtilities;
import javazoom.jl.player.advanced.AdvancedPlayer;


public class Client {
    // static final ConfigLoader config = new ConfigLoader();
    // static final String PORT = config.get("port") ;
    // static final String HOST = config.get("server_host") ;

    private static InputStream copyStream(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return new ByteArrayInputStream(buffer.toByteArray());
    }
    

    public static void main(String[] args) {

        String host = "localhost";
        int port = 1234;

        //System.out.println("Le port" +PORT) ;
        //System.out.println("Le host" +HOST) ;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connecté au serveur...");

            // Flux d'entrée pour lire la playlist
            InputStream data = socket.getInputStream();
            DataInputStream dis = new DataInputStream(data);

            // Réception des fichiers de la playlist
            int listSize = dis.readInt();
            System.out.println("Vous avez " + listSize + " fichiers dans votre serveur.");
            String[] playlist = new String[listSize];
            for (int i = 0; i < listSize; i++) {
                playlist[i] = dis.readUTF();
                System.out.println((i + 1) + ". " + playlist[i]);
            }

            // Afficher l'interface graphique
            FrameClient FC = new FrameClient(playlist);
            int choix = -1;

            // Attendre que l'utilisateur choisisse
            while ((choix = FC.getChoix()) == -1) {
                Thread.sleep(100);
            }

            System.out.println("Vous avez choisi : " + playlist[choix]);

            // Envoyer le choix au serveur
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(playlist[choix]);

            // Lire et jouer le fichier si c'est un MP3
            if (playlist[choix].endsWith(".mp3")) {
                playMp3(data);
            } else if (playlist[choix].endsWith(".mp4")) {
                playMp4(data);
            } else {
                System.out.println("Format non pris en charge pour le moment.");
            }

            System.out.println("Transfert terminé.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void playMp3(InputStream data) {
        try {
            System.out.println("Lecture en cours...");
            AdvancedPlayer player = new AdvancedPlayer(data);
            player.play();
            System.out.println("Lecture terminée.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }         

    
    

    //Reçoit et joue la vidéo
private static void playMp4(InputStream data) {
        File tempFile = new File("temporary.mkv");

        try {
            // Crée un fichier temporaire
            if (tempFile.exists()) tempFile.delete();
            tempFile.createNewFile();
            System.out.println("Fichier temporaire créé : " + tempFile.getAbsolutePath());
           
            // Lire les données de l'InputStream et écrire dans le fichier temporaire
            try (DataInputStream stream = new DataInputStream(data);
                 FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = stream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    System.out.println(tempFile.length());
                }
                fos.flush();
                System.out.println("Transfert terminé : " + tempFile.getName());
            }

             // Démarrer un thread pour jouer la vidéo
            MyThread thread = new MyThread(tempFile);
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du transfert ou de la création du fichier.");
        }
    }
    }
