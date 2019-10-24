import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    public String address;// remote ip of server
    public int port;// remote port of server

    public Client() {
        this.address = "192.168.1.203";
        this.port = 2019;
    }

    /**
     * 根据命令行参数，调度相应功能
     * 
     * @param args
     */
    private void scheduler(String[] args) {
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            help();
        } else if (args.length == 1 && args[0].equals("-l")) {
            list();
        } else if (args.length == 0) {
            list();
        } else {
            
        }
    }

    /**
     * 获取远程服务器共享文件列表
     */
    private void list() {
        Socket socket = null;
        try {
            socket = new Socket(address, port);// 连接服务器
            // 发送数据
            OutputStream out = socket.getOutputStream();// 基本流
            BufferedOutputStream bf_out = new BufferedOutputStream(out);// 包装成高效缓冲流
            bf_out.write("-l".getBytes("UTF-8"));
            bf_out.flush();
            // 接收数据
            InputStream in = socket.getInputStream();// 基本流
            BufferedInputStream bf_in = new BufferedInputStream(in);// 包装成高效缓冲流
            byte[] buffer = new byte[1024*1024];// 设置缓冲区大小为 1MB
            int len = bf_in.read(buffer);
            String args = new String(buffer, 0, len, "UTF-8");
            System.out.print(args);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connecting to server occurs error !");
            System.exit(0);
        }
    }

    /**
     * 打印帮助信息
     */
    private void help() {
        System.out.println("A tool of starting share files in current directory");
        System.out.println("Usage:");
        System.out.println("    java Server [options]");
        System.out.println("Options:");
        System.out.println("    -h, --help    Show help");
        System.out.println("    -4            Make all connections via IPv4");
        System.out.println("    -6            Make all connections via IPv6");
        System.out.println("    -p <port>     Set the listening port");
        System.out.println("    -c            Use specified conf.properties file to start");
        System.out.println("    -l            List sharing files of current directory");
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.scheduler(args);
    }
}