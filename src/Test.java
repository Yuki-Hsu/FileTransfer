import java.net.InetAddress;
import java.nio.ByteBuffer;

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
        System.out.println("[█████=========================]█████=========================█████=========================");

        long max = 15126265656266L;
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, max);
        byte[] bs = buffer.array();
        System.out.println(bs.length);
    }
}
