import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public InetAddress remote_host;// remote information of server
    public int port;// remote port of server

    public Client() {
        this.remote_host = null;
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
            init();
            list();
        } else if (args.length == 2 && args[0].equals("-d")) {
            init();
            download(args);
        } else if (args.length == 2 && args[0].equals("-u")) {
            init();
            upload(args);
        } else {
            System.out.println("Unrecognized parameters, please try 'java Client -h'");
        }
    }

    /**
     * 初始化要连接的远程服务器的信息
     */
    private void init() {
        System.out.println("Input the ip or host name of server: ");
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        sc.close();
        // 原文链接：https://blog.csdn.net/u012806692/article/details/50635590"
        String regex_ipv4 = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        if (str.matches(regex_ipv4)) {// 输入为 IPv4 地址
            String[] ipStr = str.split("\\.");
            byte[] ipBuf = new byte[4];
            for (int i = 0; i < 4; i++) {
                ipBuf[i] = (byte) (Integer.parseInt(ipStr[i]) & 0xff);
            }
            try {
                this.remote_host = InetAddress.getByAddress(ipBuf);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.out.println("Input ip address is wrong, please try again");
                System.exit(0);
            }
        } else {// 输入为 host name
            try {
                this.remote_host = InetAddress.getByName(str);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.out.println("Input host name is wrong, please try again");
                System.exit(0);
            }
        }
        System.out.println("Start connecting to " + remote_host.getHostAddress() + ":" + port);
    }

    /**
     * 向远程服务器上传文件
     * 
     * @param args
     */
    private void upload(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket(remote_host, port);// 连接服务器
            // 发送数据
            OutputStream out = socket.getOutputStream();// 基本流
            BufferedOutputStream bf_out = new BufferedOutputStream(out);// 包装成高效缓冲流
            bf_out.write("-u".getBytes("UTF-8"));// -u 表示上传数据
            bf_out.write(args[1].getBytes("UTF-8"));
            bf_out.flush();
/* // 临时关闭，避免两次发送的内容粘包
bf_out.close();
out.close();
// 再次开启 [[[--- 此方法无效，关闭流即导致 socket 也关闭了 ---]]]
out = socket.getOutputStream();// 基本流
bf_out = new BufferedOutputStream(out);// 包装成高效缓冲流 */
            // 接收数据
            InputStream in = socket.getInputStream();// 基本流
            BufferedInputStream bf_in = new BufferedInputStream(in);// 包装成高效缓冲流
            byte[] buffer = new byte[1024 * 1024];// 设置缓冲区大小为 1MB
            int len = bf_in.read(buffer);
            if (new String(buffer, 0, len, "UTF-8").equals("OK")) {
                // 发送数据
                File file = new File(args[1]);// 打开要发送的文件
                FileInputStream in_file = new FileInputStream(file);// 基本流
                BufferedInputStream bf_in_file = new BufferedInputStream(in_file);// 包装成高效缓冲流
                while ((len = bf_in_file.read(buffer)) != -1) {
                    bf_out.write(buffer, 0, len);
                    bf_out.flush();
                }
                System.out.println("Send file success");
                bf_in_file.close();
            } else {
                System.out.println("Send file failure, remote host ");
            }
            // 关闭流
            bf_out.close();
            bf_in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connecting to server occurs error !");
            System.exit(0);
        }
    }

    /**
     * 从远程服务器下载文件
     * 
     * @param args
     */
    private void download(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket(remote_host, port);// 连接服务器
            // 发送数据
            OutputStream out = socket.getOutputStream();// 基本流
            BufferedOutputStream bf_out = new BufferedOutputStream(out);// 包装成高效缓冲流
            bf_out.write("-d".getBytes("UTF-8"));// -d 表示下载数据
            bf_out.write(args[1].getBytes("UTF-8"));
            bf_out.flush();
            // 接收数据
            File file = new File(args[1]);// 打开要接收的文件
            FileOutputStream out_file = new FileOutputStream(file);// 基本流
            BufferedOutputStream bf_out_file = new BufferedOutputStream(out_file);// 包装成高效缓冲流
            InputStream in = socket.getInputStream();// 基本流
            BufferedInputStream bf_in = new BufferedInputStream(in);// 包装成高效缓冲流
            byte[] buffer = new byte[1024 * 1024];// 设置缓冲区大小为 1MB
            int len = -1;
            while ((len = bf_in.read(buffer)) != -1) {
                bf_out_file.write(buffer, 0, len);
                // 输出流写完就刷新，确保立即把数据写入文件，及时查看文件变化
                bf_out_file.flush();
            }
            // 关闭流
            bf_out_file.close();
            bf_in.close();
            bf_out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connecting to server occurs error !");
            System.exit(0);
        }
    }

    /**
     * 获取远程服务器共享文件列表
     */
    private void list() {
        Socket socket = null;
        try {
            socket = new Socket(remote_host, port);// 连接服务器
            // 发送数据
            OutputStream out = socket.getOutputStream();// 基本流
            BufferedOutputStream bf_out = new BufferedOutputStream(out);// 包装成高效缓冲流
            bf_out.write("-l".getBytes("UTF-8"));
            bf_out.flush();
            // 接收数据
            InputStream in = socket.getInputStream();// 基本流
            BufferedInputStream bf_in = new BufferedInputStream(in);// 包装成高效缓冲流
            byte[] buffer = new byte[1024*1024];// 设置缓冲区大小为 1MB
            int len = -1;
            while ((len = bf_in.read(buffer)) != -1) {
                String args = new String(buffer, 0, len, "UTF-8");
                System.out.print(args);
            }
            // 关闭流
            bf_in.close();
            bf_out.close();
            socket.close();
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
        System.out.println("A tool for download/upload files from remote sharing directory");
        System.out.println("Usage:");
        System.out.println("    java Client <options>");
        System.out.println("Options:");
        System.out.println("    -h, --help    Show help");
        System.out.println("    -4            Make all connections via IPv4");
        System.out.println("    -6            Make all connections via IPv6");
        // System.out.println("    -p <port>     Set the listening port");
        System.out.println("    -c            Use conf.properties file of current directory to start");
        System.out.println("    -l            List sharing files of remote host");
        System.out.println("    -d <file>     Download specified sharing file from remote host");
        System.out.println("    -u <file>     Upload specified file to remote host (you can drag the file to command prompt)");
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.scheduler(args);
    }
}