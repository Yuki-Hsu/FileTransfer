import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;

public class Server {
    private static final String BASETAB = "|   ";// base indent for files list
    public int port;// listening port for server

    public Server() {
        this.port = 2019;// 默认的监听端口
    }

    /**
     * 根据命令行参数，调度相应功能
     * 
     * @param args
     */
    private void scheduler(String[] args) {
        if (args.length == 0) {
            // 开启服务
            init();
        } else if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            help();
        } else if (args.length == 1 && args[0].equals("-l")) {
            list();
        } else if (args.length == 2 && args[0].equals("-p")) {
            // 设置新的监听端口
            try {
                int temp = Integer.parseInt(args[1]);
                if (1024 < temp && temp < 49151) {// 注册端口（Registered Ports）：从1024到49151。它们松散地绑定于一些服务
                    this.port = temp;
                } else {
                    System.out.println("The range of port number is incorrect, please try again");
                    System.exit(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
            // 开启服务
            init();
        } else {
            System.out.println("Unrecognized parameters, please try 'java Server -h'");
        }
    }

    /**
     * 递归列举当前目录下所有文件
     */
    private void list() {
        String root_directory = new String(System.getProperty("user.dir"));// 获取当前目录
        File file = new File(root_directory);
        // System.out.println(file);
        // System.out.println(file.getAbsolutePath());
        getAllFiles(file, "");
    }

    private void list(BufferedOutputStream bf_out) {
        String root_directory = new String(System.getProperty("user.dir"));// 获取当前目录
        File file = new File(root_directory);
        // System.out.println(file);
        // System.out.println(file.getAbsolutePath());
        getAllFiles(file, "", bf_out);
    }

    private void getAllFiles(File file, String tab) {
        String tabStyle = null;// 声明缩进样式
        if (tab.length() >= 4) {
            char[] temp = tab.toCharArray();
            for (int i = 1; i < 4; i++) {// 修改文件缩进风格
                temp[temp.length - i] = '-';
            }
            tabStyle = new String(temp);// 新的缩进样式
            // System.out.print(tabStyle);
        } else {
            // System.out.print(tab);
            tabStyle = tab;
        }
        if (file.isFile()) {
            System.out.printf("%-60s%15s\n", tabStyle + file.getName(), fileSize(file));
        } else {
            System.out.printf("%-60s%15s\n", tabStyle + file.getName(), "......");
            File[] files = file.listFiles();
            for (File f : files) {
                getAllFiles(f, tab + BASETAB);
            }
        }
    }

    private void getAllFiles(File file, String tab, BufferedOutputStream bf_out) {
        String tabStyle = null;// 声明缩进样式
        if (tab.length() >= 4) {
            char[] temp = tab.toCharArray();
            for (int i = 1; i < 4; i++) {// 修改文件缩进风格
                temp[temp.length - i] = '-';
            }
            tabStyle = new String(temp);// 新的缩进样式
            // System.out.print(tabStyle);
        } else {
            // System.out.print(tab);
            tabStyle = tab;
        }
        if (file.isFile()) {
            String str = String.format("%-60s%15s\n", tabStyle + file.getName(), fileSize(file));
            try {
                bf_out.write(str.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String str = String.format("%-60s%15s\n", tabStyle + file.getName(), "......");
            try {
                bf_out.write(str.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            File[] files = file.listFiles();
            for (File f : files) {
                getAllFiles(f, tab + BASETAB, bf_out);
            }
        }
    }

    /**
     * 获取文件大小
     * 
     * @param file
     * @return
     */
    private String fileSize(File file) {
        long len = file.length();
        if (len < 1024) {
            return len + "B";
        } else if (1024 <= len & len < 1024 * 1024) {
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(len / 1024.0) + "KB";
        } else if (1024 * 1024 <= len & len < 1024 * 1024 * 1024) {
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(len / 1024.0 / 1024.0) + "MB";
        } else {
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(len / 1024.0 / 1024.0 / 1024.0) + "GB";
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
        System.out.println("                  When no option specified, start service on default port: 2019");
        System.out.println("    -h, --help    Show help and quit");
        // System.out.println(" -4 Make all connections via IPv4");
        // System.out.println(" -6 Make all connections via IPv6");
        System.out.println("    -p <port>     Set the listening port and start service");
        System.out.println("    -l            List sharing files of current directory");
    }

    /**
     * 打印进度条
     * @param progress
     * @param max
     */
    private void progressBar(long progress, long max) {
        int length = 80;
        long blockLength = max / length;
        long finishedBlock = progress / blockLength;
        System.out.print("\r");
        for (long i = 0; i < finishedBlock; i++) {
            System.out.print(">");
        }
        for (long i = finishedBlock; i < length; i++) {
            System.out.print("-");
        }
    }

    /**
     * 开启服务
     */
    private void init() {
        // 显示本机信息
        String host_name = null;
        try {
            host_name = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.printf("%-15s%-45s\n", "Host  Name:", host_name);
        try {
            for (InetAddress it : InetAddress.getAllByName(host_name)) {
                System.out.printf("%-15s%-45s%10s\n", "IP address:", it.getHostAddress(), Integer.toString(port));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // 开启服务端 Socket 并等待用户连接
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Start on the port:" + port + "failure !");
            System.exit(0);
        }
        // 一直监听
        while (true) {
            // 获取客户端连接
            try {
                Socket socket = ss.accept();
                // 多线程接收客户端
                new Thread(){
                    public void run() {
                        try {
                            // 显示 Socket 连接信息
                            InetAddress ipObject = socket.getInetAddress();// 得到IP地址对象
                            String ip = ipObject.getHostAddress();// 得到IP地址字符串
                            int port = socket.getPort();
                            System.out.println("Connecting from   " + ip + ":" + port);
                            // 获取客户端输入参数
                            InputStream in = socket.getInputStream();// 基本流
                            BufferedInputStream bf_in = new BufferedInputStream(in);// 包装成高效缓冲流
                            byte[] buffer = new byte[1024 * 1024];// 设置缓冲区大小为 1MB
                            int len = bf_in.read(buffer);
                            if (len != -1) {
                                String args = new String(buffer, 0, len, "UTF-8");
                                System.out.println("recieve args from client: " + args);// 打印收到的参数
                                // 根据参数调度不同功能
                                if (args.equals("-l")) {// 发送文件列表
                                    // 发送数据
                                    OutputStream out = socket.getOutputStream();// 基本流
                                    BufferedOutputStream bf_out = new BufferedOutputStream(out);// 包装成高效缓冲流
                                    list(bf_out);
                                    // 关闭流
                                    bf_out.close();
                                    bf_in.close();
                                    socket.close();
                                } else if (args.substring(0, 2).equals("-d")) {// 服务端上传文件给客户端
                                    String filename = new String(args.substring(2));
                                    // 发送数据
                                    File file = new File(System.getProperty("user.dir"), filename);// 打开要发送的文件
                                    long max = file.length();
                                    FileInputStream in_file = new FileInputStream(file);// 基本流
                                    BufferedInputStream bf_in_file = new BufferedInputStream(in_file);// 包装成高效缓冲流
                                    OutputStream out = socket.getOutputStream();// 基本流
                                    BufferedOutputStream bf_out = new BufferedOutputStream(out);// 包装成高效缓冲流
                                    // long to byte[]
                                    byte[] bs = ByteBuffer.allocate(8).putLong(0, max).array();
                                    bf_out.write(bs);// 发送文件大小
                                    bf_out.flush();
                                    long progress = 0;
                                    while ((len = bf_in_file.read(buffer)) != -1) {
                                        bf_out.write(buffer, 0, len);
                                        // 写完后刷新，用来确保接收端立马能从该输出流中读到数据
                                        bf_out.flush();
                                        progress += len;
                                        // 进度条显示
                                        progressBar(progress, max);
                                    }
                                    System.out.println("\nSend file success");
                                    // 关闭流
                                    bf_out.close();
                                    bf_in_file.close();
                                    bf_in.close();
                                    socket.close();
                                } else if (args.substring(0, 2).equals("-u")) {// 客户端上传文件给服务端
                                    // https://www.runoob.com/java/java-regular-expressions.html
                                    String[] strs = new String(args.substring(2)).split("\\\\");
                                    String filename = strs[strs.length - 1];
                                    // 发送数据
                                    OutputStream out = socket.getOutputStream();// 基本流
                                    BufferedOutputStream bf_out = new BufferedOutputStream(out);// 包装成高效缓冲流
                                    bf_out.write("OK".getBytes("UTF-8"));
                                    bf_out.flush();
                                    // 接收数据
                                    File file = new File(filename);// 打开要接收的文件
                                    FileOutputStream out_file = new FileOutputStream(file);// 基本流
                                    BufferedOutputStream bf_out_file = new BufferedOutputStream(out_file);// 包装成高效缓冲流
                                    byte[] bs = bf_in.readNBytes(8);// 读取文件大小
                                    // byte[] to long
                                    long max = ByteBuffer.allocate(8).put(bs, 0, bs.length).flip().getLong();
                                    long progress = 0;
                                    while ((len = bf_in.read(buffer)) != -1) {
                                        bf_out_file.write(buffer, 0, len);
                                        bf_out_file.flush();
                                        progress += len;
                                        // 进度条显示
                                        progressBar(progress, max);
                                    }
                                    System.out.println("\nReceive file success");
                                    // 关闭流
                                    bf_out_file.close();
                                    bf_in.close();
                                    bf_out.close();
                                    socket.close();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Listening for a connection occurs error !");
                System.exit(0);
            }
        }
        // ss.close();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.scheduler(args);
    }
}