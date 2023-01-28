package socket.client;

import socket.Utils;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClienteSocket extends Thread {

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private String commands = null;

    public ClienteSocket() {
        connect();
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 9999);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            new Thread(() -> {
                try {

                    System.out.println(Utils.replace(Utils.msg_socket_conectado, socket.getInetAddress().getHostAddress(), socket.getPort()));
                    System.out.println(Utils.msg_comandos);

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {

                        if (commands == null) {
                            commands = line;
                        } else {
                            if (line.equals(Utils.msg_bye)) {
                                inputStream.close();
                                bufferedReader.close();
                                socket.close();
                                System.out.println(Utils.msg_exit);
                                System.out.println(line);
                                System.exit(0);
                            } else if (line.startsWith("DOWNLOAD")) {

                                String[] file = line.split(";");
                                FileReceive fileReceive = new FileReceive(file[1], Long.parseLong(file[2]));
                                fileReceive.receiveFile(inputStream);
                                System.out.println(Utils.msg_download_concluido);
                                continue;
                            }
                        }
                        System.out.println(line.replace("\\n", "\n"));
                        System.out.println(Utils.msg_digite_comando);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                if (commands != null) {
                    String line = scanner.nextLine();
                    Utils.sendMessage(outputStream, line);
                }
            }

        } catch (Exception e) {
            System.out.println(Utils.msg_socket_cliente_erro);
        }
    }

}
