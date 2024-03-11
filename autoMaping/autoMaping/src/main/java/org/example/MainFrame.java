package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {

    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JButton executeButton;
    private JButton browseButton1;
    private JButton browseButton2;
    private JButton browseButton3; // Dodano nowy przycisk do wyboru pliku

    public MainFrame() {
        setTitle("Wczytaj plan kont");
        setSize(450, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        textField1 = new JTextField(10);
        textField2 = new JTextField(10);
        textField3 = new JTextField(10);
        executeButton = new JButton("Wykonaj");
        browseButton1 = new JButton("Wybierz plik z kontami");
        browseButton2 = new JButton("Wybierz plik ppk");
        browseButton3 = new JButton("Wybierz nowy plik"); // Inicjalizacja nowego przycisku

        add(new JLabel("Plik z kontami"));
        add(textField1);
        add(browseButton1);
        add(new JLabel("Plik pelny plan kont"));
        add(textField2);
        add(browseButton2);
        add(new JLabel("Plik ze wzorami kont"));
        add(textField3);
        add(new JLabel());
        add(browseButton3);
        add(executeButton);

        browseButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    textField3.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        browseButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    textField1.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        browseButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    textField2.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String konta = textField1.getText();
                String ppk = textField2.getText();
                String wzory = textField3.getText();
                Pojedynczy_rekord main = new Pojedynczy_rekord();
                try {
                    main.zaczytaj_wzory(wzory);
                    float result = main.zaczytaj_dane(konta, ppk);
                    String formattedResult = String.format("%.0f%%", result * 100);
                    JOptionPane.showMessageDialog(null, "Operacja zakończona pomyślnie z wynikiem: " + formattedResult);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Wystąpił błąd: " + ex.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}
