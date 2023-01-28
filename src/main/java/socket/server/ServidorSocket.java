package socket.server;

import socket.Log;
import socket.Utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorSocket {

    public static int limit_upload = 100;
    protected static final String usuario = "root";
    protected static final String senha = "123";

    public ServidorSocket() {
        try {

            ServerSocket serverSocket = new ServerSocket(9999);

            System.out.println(Utils.replace(Utils.msg_socket_server, serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort()));
            while (true) {

                Socket cliente = serverSocket.accept();
                System.out.println(Utils.replace(Utils.msg_socket_cliente_conectado, cliente.getInetAddress().getHostAddress(), cliente.getPort()));

                new Thread(new ClienteConectado(cliente)).start();

            }

        } catch (Exception e) {
            System.out.println(Utils.msg_socket_erro);
        }

    }
}

class ClienteConectado implements Runnable {

    private OutputStream outputStream;
    private BufferedReader bufferedReader;
    private final Socket socket;


    public ClienteConectado(Socket socket) {
        this.socket = socket;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = socket.getOutputStream();

            Utils.sendMessage(outputStream, getCommands());
            Log.save(socket, "{0}:{1} Conectou");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            String senha = null;
            String usuario = null;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] args = line.split(" ");
                if (args[0].equals("exit")) {
                    Utils.sendMessage(outputStream, Utils.msg_bye);
                    System.out.println(Utils.replace(Utils.msg_desconectado, socket.getInetAddress().getHostAddress(), socket.getPort()));
                    bufferedReader.close();
                    outputStream.close();
                    socket.close();
                    Log.save(socket, "{0}:{1} Desconectou");
                } else if (args[0].equals("login")) {
                    if (args.length == 3) {
                        if (check(args[1], args[2])) {
                            usuario = args[1];
                            senha = args[2];
                            Utils.sendMessage(outputStream, Utils.msg_login_realizado);
                            Log.save(socket, "{0}:{1} Realizou login");
                        } else {
                            Utils.sendMessage(outputStream, Utils.msg_login_credenciais_incorretas);
                            Log.save(socket, "{0}:{1} Erro de autenticação");
                        }
                    } else {
                        Utils.sendMessage(outputStream, Utils.replace(Utils.msg_uso_correto, "login [usuario] [senha]"));
                    }
                } else {
                    if (check(usuario, senha)) {
                        if (Utils.checkCommand(line)) {
                            Log.save(socket, "{0}:{1} Usou o comando {2}", line);
                            if (args[0].equalsIgnoreCase("get")) {

                                if (args.length == 2) {
                                    Utils.sendFile(outputStream, new File(args[1]));
                                    Log.save(socket, "{0}:{1} Realizou download do arquivo {2}", args[1]);
                                } else
                                    Utils.sendMessage(outputStream, Utils.replace(Utils.msg_uso_correto, "get [nome_arquivo]"));

                            } else {

                                Process process = Runtime.getRuntime().exec(line);
                                StringBuilder output = new StringBuilder();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                                String lineExec;
                                while ((lineExec = reader.readLine()) != null)
                                    output.append(lineExec).append("\\n");

                                Utils.sendMessage(outputStream, output.toString());

                            }
                        } else {
                            Utils.sendMessage(outputStream, Utils.msg_nao_autorizado);
                            Log.save(socket, "{0}:{1} Comando não permitido");
                        }
                    } else {
                        Utils.sendMessage(outputStream, Utils.msg_sem_autorizacao);
                        Log.save(socket, "{0}:{1} Sem autorização");
                    }
                }
            }


        } catch (Exception e) {
        }

    }

    public String getCommands() {
        try {

            StringBuilder builder = new StringBuilder();
            Scanner scanner = new Scanner(Utils.getFile("commands.txt"));
            scanner.useDelimiter(System.lineSeparator());
            while (scanner.hasNext())
                builder.append(scanner.nextLine()).append("\\n");

            return builder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.msg_nao_ha_comandos;
    }

    private boolean check(String usuario, String senha) {
        if (usuario == null || senha == null)
            return false;
        return usuario.equals(ServidorSocket.usuario) && senha.equals(ServidorSocket.senha);
    }
}


