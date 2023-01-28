package socket.client;

import socket.Utils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Collections;

public class FileReceive {

    private final long size;
    private long downloaded;
    private final String filePath;

    public FileReceive(String filePath, long size) {
        this.size = size;
        this.downloaded = 0;
        this.filePath = "download" + File.separator + filePath;
        File download = new File("download");
        if (!download.exists())
            download.mkdir();

        System.out.println(Utils.replace(Utils.msg_recebendo_arquivo, filePath, getFileSize()));
    }

    public boolean receiveFile(InputStream inputStream) throws IOException {

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(filePath));

        byte[] buffer = new byte[2048];
        int bytesRead;
        while ((bytesRead = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
            fileOutput.write(buffer, 0, bytesRead);
            downloaded += bytesRead;

            int percent = getDownloadPercent();
            String progress_concluido = String.join("", Collections.nCopies(percent, "="));
            String progress_restante = String.join("", Collections.nCopies(100 - percent, " "));
            System.out.print("\r" + Utils.replace(
                    Utils.msg_progresso,
                    progress_concluido,
                    progress_restante,
                    percent,
                    getDownloadedFormat(),
                    getFileSize()
            ));
            if (downloaded >= size) {
                System.out.println("");
                fileOutput.close();
                return true;
            }

        }
        fileOutput.close();
        return true;
    }

    public long getSize() {
        return this.size;
    }

    public long getDownloaded() {
        return this.downloaded;
    }

    public String getDownloadedFormat() {
        return sizeFormat(downloaded);
    }

    public String getFileSize() {
        return sizeFormat(size);
    }

    public int getDownloadPercent() {
        return (int) (((double) 100 / this.size) * downloaded);
    }

    public String sizeFormat(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
