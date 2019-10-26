import java.net.InetAddress;

public class Test {
    public static void main(String[] args) throws Exception {
        InetAddress local = InetAddress.getLocalHost();
        InetAddress remote = InetAddress.getByName("www.itcast.cn");
        System.out.println("本机的IP地址：" + local.getHostAddress());
        System.out.println(local.getHostName());
        for (InetAddress it : InetAddress.getAllByName(local.getHostName())) {
            System.out.println(it.getHostAddress());
        }
        


        System.out.println("itcast的IP地址：" + remote.getHostAddress());
        System.out.println("itcast的主机名为：" + remote.getHostName());
    }
}
