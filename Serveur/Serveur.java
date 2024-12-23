package Serveur;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
    public static void main(String[] args) {
        int port = 1234;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur en attente de connexions...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connecté : " + clientSocket.getInetAddress());

                // Créer un thread pour gérer la connexion du client
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
             DataInputStream dis = new DataInputStream(clientSocket.getInputStream())) {

            // Envoi de la playlist
            File folder = new File("C:\\S3\\Streaming\\Serveur\\playlist");
            File[] files = folder.listFiles();
            if (files == null || files.length == 0) {
                System.out.println("Aucun fichier trouvé dans le dossier 'playlist'.");
                dos.writeInt(0);
                return;
            }

            dos.writeInt(files.length);
            for (File file : files) {
                dos.writeUTF(file.getName());
            }

            // Recevoir le choix du client
            String chosenFile = dis.readUTF();
            System.out.println("Le client a choisi : " + chosenFile);

            // Envoi du fichier choisi
            boolean fileFound = false;
            for (File file : files) {
                if (file.getName().equals(chosenFile)) {
                    sendFile(clientSocket, file);
                    fileFound = true;
                    break;
                }
            }

            if (!fileFound) {
                System.out.println("Le fichier demandé n'a pas été trouvé : " + chosenFile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFile(Socket clientSocket, File file) {
        try (FileInputStream fis = new FileInputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream())) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                System.out.println("Bytes envoyés : " + bytesRead);
            }

            bos.flush();
            System.out.println("Fichier envoyé avec succès : " + file.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
