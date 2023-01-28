package socket;

import socket.client.ClienteSocket;
import socket.server.ServidorSocket;

public class Main {

    public static void main(String[] args) {

        Utils.extractFile("commands.txt");
        Utils.extractFile("log.txt");

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("server")) {
                new ServidorSocket();
                return;
            }

            if (args[0].equalsIgnoreCase("client")) {
                new ClienteSocket();
                return;
            }

        }

        System.out.println("Parametro incorreto, parametros validos");
        System.out.println("server, client");
    }

}
