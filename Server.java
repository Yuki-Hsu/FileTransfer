public class Server {
    public int port;//listening port for server

    public Server() {
        this.port = 2019;
    }

    private void scheduler(String[] args) {
        if (args.length == 0) {
            init();
        } else if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            help();
        } else {
            
        }
    }

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