package socket;

import java.io.File;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private static final File file = Utils.getFile("log.txt");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

    public static void save(Socket socket, String message, String... variaveis) {
        String date = simpleDateFormat.format(new Date());
        try {
            if (!file.exists())
                file.createNewFile();
            String ip = socket.getInetAddress().getHostAddress();
            String port = socket.getPort() + "";
            Object[] strings = new Object[variaveis.length + 2];
            strings[0] = ip;
            strings[1] = port;
            System.arraycopy(variaveis, 0, strings, 2, variaveis.length);
            byte[] bytes = (date + " " + Utils.replace(message, strings) + "\n").getBytes();
            Files.write(file.getAbsoluteFile().toPath(), bytes, StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
