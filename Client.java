package client;

import java.awt.*;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
public class Client {
    JTextArea odbiorWiadomosci;
    JTextField wiadomosc;
    BufferedReader czytelnik;
    PrintWriter pisarz;
    Socket gniazdo;
    JLabel dane;

    public static void main(String[] args) {
        Client client = new Client();
        client.polaczMnie();
    }
    public void polaczMnie() {
        JFrame frame = new JFrame("klient czatu");
        JPanel panel = new JPanel();

        odbiorWiadomosci = new JTextArea(15, 50);
        odbiorWiadomosci.setLineWrap(true);
        odbiorWiadomosci.setWrapStyleWord(true);
        odbiorWiadomosci.setEditable(false);

        JScrollPane przewijanie = new JScrollPane(odbiorWiadomosci);
        przewijanie.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        przewijanie.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        wiadomosc = new JTextField(20);
        JButton przyciskWyslij = new JButton("Wyslij");
        przyciskWyslij.addActionListener(new SluchaczPrzycisku());

        panel.add(przewijanie);
        panel.add(wiadomosc);
        panel.add(przyciskWyslij);
        konfiguruj();

        Thread watekOdbiorcy = new Thread(new Odbiorca());
        watekOdbiorcy.start();
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setSize(new Dimension(600, 400));
        frame.setVisible(true);
    }
    private void konfiguruj() {
        try {
            gniazdo = new Socket("127.0.0.1", 2020);
            InputStreamReader czytelnikStrm = new InputStreamReader(gniazdo.getInputStream());
            czytelnik = new BufferedReader(czytelnikStrm);
            pisarz = new PrintWriter(gniazdo.getOutputStream());
            System.out.println("Zakończono konfiguracje sieci");
        } catch (IOException ex) {
            System.out.println("Konfiguracja sieci nie powiodła się!");
            ex.printStackTrace();
        }
    }
    private class SluchaczPrzycisku implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                pisarz.println(wiadomosc.getText());
                pisarz.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            wiadomosc.setText("");
            wiadomosc.requestFocus();
        }
    }
    public class Odbiorca implements Runnable {
        @Override
        public void run() {
            String wiad;
            try {
                while ((wiad = czytelnik.readLine()) != null) {
                    System.out.println("Odczytano: " + wiad);
                    odbiorWiadomosci.append(wiad + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}