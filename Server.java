package serv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


public class Server {
    ArrayList strumienieWyjsciowe;
    public class ObslugaKlienta implements Runnable {
        BufferedReader czytelnik;
        Socket gniazdo;
        String adres;
        int port;
        public ObslugaKlienta(Socket gniazdo) {
            try {
                adres = (gniazdo.getLocalAddress()).toString();
                port = gniazdo.getLocalPort();
                this.gniazdo = gniazdo;
                InputStreamReader reader = new InputStreamReader(gniazdo.getInputStream());
                czytelnik = new BufferedReader(reader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        @Override
        public void run() {
            String wiadomosc;
            try {
                while ((wiadomosc = czytelnik.readLine()) !=
                        null) {
                    System.out.println("Odczytano: " + wiadomosc);
                    rozeslijDoWszystkich(wiadomosc, adres, port);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new Server().doRoboty();
    }


    public void doRoboty() {
        strumienieWyjsciowe = new ArrayList();
        try {
            ServerSocket gniazdoSerwera = new ServerSocket(2020);
            while (true) {
                Socket gniazdoKlienta = gniazdoSerwera.accept();
                PrintWriter pisarz = new PrintWriter(gniazdoKlienta.getOutputStream());
                strumienieWyjsciowe.add(pisarz);
                Thread watekKlienta = new Thread(new ObslugaKlienta(gniazdoKlienta));
                watekKlienta.start();
                System.out.println("Nawiązano połączenie!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void rozeslijDoWszystkich(String wiadomosc, String adres, int port) {
        Iterator it = strumienieWyjsciowe.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter pisarz = (PrintWriter) it.next();
                pisarz.print("Klient "+ adres+ "/" +port+":");
                pisarz.flush();
                pisarz.println(wiadomosc);
                pisarz.flush();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }}}
