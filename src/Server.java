import java.io.File;

public class Server {
    public int port;//listening port for server

    public Server() {
        this.port = 2019;
    }

    /**
     * 根据命令行参数，调度相应功能
     * @param args
     */
    private void scheduler(String[] args) {
        if (args.length == 0) {
            init();
        } else if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            help();
        } else if (args.length == 1 && args[0].equals("-l")) {
            list();
        } else {
            
        }
    }

    /**
     * 递归列举当前目录下所有文件
     */
    private void list() {
        String root_directory = new String(System.getProperty("user.dir"));//获取当前目录
        File file = new File(root_directory);
        // System.out.println(file);
        // System.out.println(file.getAbsolutePath());
        getAllFiles(file, "");
    }

    private void getAllFiles(File file, String tab) {
        if (tab.length() >= 4) {
            char[] temp = tab.toCharArray();
            for (int i = 1; i < 4; i++) {//修改文件缩进风格
                temp[temp.length - i] = '-';
            }
            String tabStyle = new String(temp);
            System.out.print(tabStyle);
        } else {
            System.out.print(tab);
        }
        if (file.isFile()) {
            // System.out.println(file.getName());
            System.out.printf("%-30s%15s\n", file.getName(), "56.3MB");
        } else {
            System.out.println(file.getName());
            File[] files = file.listFiles();
            for (File f : files) {
                getAllFiles(f, tab + "|   ");
            }
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

    private void init() {
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.scheduler(args);
    }
}