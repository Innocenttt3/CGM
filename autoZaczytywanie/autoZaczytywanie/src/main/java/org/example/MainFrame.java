package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import org.example.Wartosci_bazy;

public class MainFrame extends JFrame {

    private JTextField textField1;
    private JTextField textField2;
    private JButton executeButton;
    private JButton browseButton1;
    private JButton browseButton2;

    public MainFrame() {
        setTitle("Wczytaj plan kont");
        setSize(550, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        textField1 = new JTextField(10);
        textField2 = new JTextField(10);
        executeButton = new JButton("Wykonaj");
        browseButton1 = new JButton("Wybierz plik źródłowy");
        browseButton2 = new JButton("Wybierz plik docelowy");

        add(new JLabel("Plik zrodlowy: (XLS/XLS)"));
        add(textField1);
        add(browseButton1);
        add(new JLabel("Plik docelowy: (CSV)"));
        add(textField2);
        add(browseButton2);
        add(new JLabel());
        add(executeButton);

        JTextArea descriptionArea = new JTextArea("W pierwszym polu należy wpisać nazwę pliku lub użyć przycisku obok aby wybrać konkretny plik XLSX/XLS, których chcemy wgrać.\n" +
                "\nW drugim polu należy wpisać nazwę pliku (bez lub z rozszerzeniem) lub użyć przycisku obok aby wybrać konkretny plik CSV, do którego zostaną zaczytane dane.\n" +
                "\nJeśli pliki nie zostaną wybrane a wpisane ręcznie, muszą znajdować się w tym samym folderze co aplikacja!!!!");
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        add(scrollPane);

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
                String string1 = textField1.getText();
                String string2 = textField2.getText();
                if (!string2.contains(".csv")) {
                    string2 += ".csv";
                }
                Wartosci_bazy main = new Wartosci_bazy();
                try {
                    main.zaczytaj_dane(string1, string2);
                    JOptionPane.showMessageDialog(null, "Operacja zakończona pomyślnie!");
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
