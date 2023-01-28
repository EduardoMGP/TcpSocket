package socket;

import socket.server.ServidorSocket;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Utils {

    public static String msg_nao_autorizado = Colors.RED_BRIGHT + "[x] Comando não permitido" + Colors.RESET;
    public static String msg_uso_correto = Colors.YELLOW_BRIGHT + "[!] Uso correto: " + Colors.RESET + "{0}" + Colors.RESET;
    public static String msg_envio_concluidp = Colors.GREEN_BRIGHT + "Envio concluido!" + Colors.RESET;
    public static String msg_arquivo_nao_existe = Colors.RED_BRIGHT + "O arquivo {0} não existe" + Colors.RESET;
    public static String msg_progresso = "[{0}>{1} ] {2}% [{3}/{4}]" + Colors.RESET;
    public static String msg_comandos = Colors.BLUE_BRIGHT + "Buscando comandos disponíveis para uso" + Colors.RESET;
    public static String msg_nao_ha_comandos = Colors.RED_BRIGHT + "Não há comandos disponíveis";
    public static String msg_recebendo_arquivo = Colors.GREEN_BRIGHT + "Recebendo arquivo " + Colors.RESET + "{0} " + Colors.GREEN_BRIGHT + " tamanho: " + Colors.RESET + "{1}" + Colors.RESET;
    public static String msg_download_concluido = Colors.GREEN_BRIGHT + "Download concluido!" + Colors.RESET;
    public static String msg_digite_comando = Colors.YELLOW_BRIGHT + "> Digite um comando abaixo" + Colors.RESET;
    public static String msg_sem_autorizacao = Colors.RED_BRIGHT + "Realize login primeiro utilizando" + Colors.RESET + " login [usuario] [senha]!" + Colors.RESET;
    public static String msg_login_realizado = Colors.GREEN_BRIGHT + "Login realizado com sucesso!" + Colors.RESET;
    public static String msg_login_credenciais_incorretas = Colors.RED_BRIGHT + "[x] Usuário ou senha incorreta" + Colors.RESET;
    public static String msg_bye = Colors.GREEN_BRIGHT + "Bye!";
    public static String msg_exit = Colors.BLUE_BRIGHT + "Encerrando sua conexão" + Colors.RESET;
    public static String msg_desconectado = Colors.YELLOW_BRIGHT + "Usuário" + Colors.RESET + " {0}:{1} " + Colors.YELLOW_BRIGHT + " desconectado" + Colors.RESET;

    public static String msg_socket_cliente_erro = Colors.RED_BRIGHT + "Ocorreu um erro ao se conectar com o socket" + Colors.RESET;
    public static String msg_socket_erro = Colors.RED_BRIGHT + "Ocorreu um erro com o servidor socket" + Colors.RESET;
    public static String msg_socket_server = Colors.GREEN_BRIGHT + "Socket iniciado " + Colors.RESET + "{0}:{1}" + Colors.RESET;
    public static String msg_socket_cliente_conectado = Colors.RESET + "Novo cliente conectado " + Colors.GREEN_BRIGHT + "{0}:{1}" + Colors.RESET;

    public static String msg_socket_conectado = Colors.RESET + "Conectado com sucesso " + Colors.GREEN_BRIGHT + "{0}:{1}" + Colors.RESET;

    public static void sendMessage(OutputStream outputStream, String next) throws IOException {
        outputStream.write((next + "\n").getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    public static void sendFile(OutputStream outputStream, File file) throws IOException {
        if (file.exists()) {
            sendMessage(outputStream, "DOWNLOAD;" + file.getName() + ";" + file.length());
            BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            byte[] buffer = new byte[ServidorSocket.limit_upload];
            int bytesRead;
            while ((bytesRead = fileInput.read(buffer, 0, buffer.length)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
                bufferedOutputStream.flush();
            }
            fileInput.close();
            System.out.println(msg_envio_concluidp);
        } else
            sendMessage(outputStream, replace(msg_arquivo_nao_existe, file.getName()));

    }

    public static String replace(String texto, Object... args) {
        for (int i = 0; i < args.length; i++)
            texto = texto.replace("{" + i + "}", args[i] + "");
        return texto;
    }

    public static boolean checkCommand(String command) {
        try {

            String cmd = command;
            String[] args = new String[0];
            if (command.contains(" ")) {
                cmd = command.substring(0, command.indexOf(" "));
                args = command.substring(command.indexOf(" ") + 1).split(" ");
            }

            Scanner scanner = new Scanner(Utils.getFile("commands.txt"));
            scanner.useDelimiter(System.lineSeparator());
            while (scanner.hasNext()) {
                String fileFullCommand = scanner.nextLine();
                String fileCmd = fileFullCommand;
                String[] fileArgs = new String[0];
                if (fileFullCommand.contains(" ")) {
                    fileCmd = fileFullCommand.substring(0, fileFullCommand.indexOf(" "));
                    fileArgs = fileFullCommand.substring(fileFullCommand.indexOf(" ") + 1).split(" ");
                }
                if (cmd.equalsIgnoreCase(fileCmd) && fileArgs.length == args.length) {
                    if (replace(fileFullCommand, args).equalsIgnoreCase(command)) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File getFile(String name) {
        try {
            String path = new File(".").getCanonicalPath();
            return new File(path, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(name);
    }

    public static boolean extractFile(String name) {
        System.out.println(Colors.YELLOW_BRIGHT + "Extraindo arquivo " + Colors.RESET + name);
        InputStream inputStream = Utils.class.getResourceAsStream("/" + name);
        if (inputStream != null) {
            try {
                if (getFile(name).exists()) {
                    System.out.println(Colors.YELLOW_BRIGHT + "O arquivo " + Colors.RESET + name + Colors.YELLOW_BRIGHT + " já existe");
                    return true;
                } else {
                    FileOutputStream outputStream = new FileOutputStream("." + File.separator + name);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0)
                        outputStream.write(buffer, 0, length);
                    inputStream.close();
                    outputStream.close();
                    return true;
                }
            } catch (Exception ignored) {
            }
        }

        System.out.println(Colors.RED_BRIGHT + "Erro ao extrair o arquivo " + Colors.RESET + name);
        System.exit(1);
        return false;
    }
}
