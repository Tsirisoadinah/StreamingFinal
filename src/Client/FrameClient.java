package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrameClient extends JFrame {
    private int choix = -1; // Choix de l'utilisateur
    private JList<String> list; // Liste des fichiers
    private JButton playButton;

    public FrameClient(String[] playlist) {
        setTitle("Playlist Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Liste pour afficher les titres
        list = new JList<>(playlist);
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);

        // Bouton pour confirmer le choix
        playButton = new JButton("Lire le fichier sélectionné");
        add(playButton, BorderLayout.SOUTH);

        // Gestion de l'événement du bouton
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex != -1) {
                    choix = selectedIndex; // Mise à jour du choix
                    JOptionPane.showMessageDialog(null,
                            "Vous avez choisi : " + playlist[selectedIndex]);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Veuillez sélectionner un fichier !");
                }
            }
        });

        setVisible(true); // Rendre la fenêtre visible
    }

    // Méthode pour récupérer le choix de l'utilisateur
    public int getChoix() {
        return choix;
    }
}

