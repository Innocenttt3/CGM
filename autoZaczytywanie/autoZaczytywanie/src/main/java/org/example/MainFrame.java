package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.example.Wartosci_bazy;

public class MainFrame extends JFrame {

    private JTextField textField1;
    private JTextField textField2;
    private JButton executeButton;

    public MainFrame() {
        setTitle("Wczytaj plan kont");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        textField1 = new JTextField(10);
        textField2 = new JTextField(10);
        executeButton = new JButton("Wykonaj");

        add(new JLabel("Nazwa pliku zrodlowego"));
        add(textField1);
        add(new JLabel("Nazwa pliku docelowego:"));
        add(textField2);
        add(new JLabel());
        add(executeButton);

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String string1 = textField1.getText();
                String string2 = textField2.getText();

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
