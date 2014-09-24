package mattmc.mankini.utils;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Project MankiniBot
 * Created by MattMc on 6/2/14.
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 */

public class MinecraftServer {
    private String host;
    private int port;
    public MinecraftServer(String host) {
        this.host = host;
        this.port = 25565;
    }

    public String parseData(Connection connection) {
        try {
            Socket socket = new Socket();
            OutputStream os;
            DataOutputStream dos;
            InputStream is;
            InputStreamReader isr;

            socket.setSoTimeout(2500);
            socket.connect(new InetSocketAddress(host, port), 2500);

            os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            is = socket.getInputStream();
            isr = new InputStreamReader(is, Charset.forName("UTF-16BE"));

            dos.write(new byte[] { (byte) 0xFE, (byte) 0x01 });

            int packetId = is.read();

            if (packetId == -1)
                System.out.println("End Of Stream");

            if (packetId != 0xFF)
                System.out.println("Invalid Packet Id " + packetId);

            int length = isr.read();

            if (length == -1)
                System.out.println("End Of Stream");

            if (length == 0)
                System.out.println("Invalid Length");

            char[] chars = new char[length];

            if (isr.read(chars, 0, length) != length)
                System.out.println("End Of Stream");

            String string = new String(chars);
            String[] data = string.split("\0");

            if (connection == Connection.PLAYERS_ONLINE) {
                return "Players Online: " + Integer.parseInt(data[4]) + "/"
                        + Integer.parseInt(data[5]);

            } else if (connection == Connection.MOTD) {
                return "MOTD: " + data[3];
            } else if (connection == Connection.SERVER_VERSION) {
                return "Version: " + data[2];
            } else if (connection == Connection.PING) {
                return "Ping: " + data[1];
            } else {
                System.out.println("Connection Value Not Handeld");
                return null;
            }

        } catch (Exception e) {
            return "Nothing Found! Please check if the server is on!";
        }
}
    public static enum Connection {
        PLAYERS_ONLINE, SERVER_VERSION, MOTD, PING
    }
}

