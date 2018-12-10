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
import java.text.DecimalFormat;
import java.util.Scanner;

public class FileTransfer {
	
	static int choose;

	public static void main(String[] args) {
		init();
		if(choose == 1) {
			try {
				receive();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(choose == 2) {
			try {
				send();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//初始化,生成欢迎界面,并选择功能
	private static void init() {
		System.out.println();
		System.out.println("==========================================");
		System.out.println("==========================================");
		System.out.println("===            FileTransfer            ===");
		System.out.println("===                                    ===");
		System.out.println("===     选择相应数字并按回车           ===");
		System.out.println("===                                    ===");
		System.out.println("===     1.接收者          2.发送者     ===");
		System.out.println("==========================================");
		System.out.println("==========================================");
		System.out.println();
		System.out.print("==>>:");
		Scanner sc = new Scanner(System.in);
		try{
			choose = sc.nextInt();
//			sc.close();
			if(choose != 1 && choose != 2) {
				throw new Exception();
			}
		} catch(Exception e) {
			System.out.println();
			System.out.print("输入错误，程序退出");
			System.exit(0);
		}
	}

	//接收者模块
	private static void receive() throws IOException {
		System.out.println();
		System.out.print("请输入目标服务器的IP地址并按回车：");
		Scanner sc = new Scanner(System.in);
		String ip = sc.next();
//		sc.close();
		//连接服务器
		Socket socket = new Socket(ip, 2018);
		//获取输入流
		InputStream in = socket.getInputStream();
		//文件输出流
//		String fileString = "C:\\Users\\Administrator\\Desktop\\22.txt";
		byte[] data = new byte[1024];
		int len = -1;
		len = in.read(data);
		String filename = new String(data, 0, len);
		BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(filename.split(" ")[0]));
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println("文件：" + filename);
		System.out.println("------------------------------------------");
		//写数据
		len = in.read(data);
		long roll = Long.parseLong(new String(data, 0, len));
		double count = 0.0;
		System.out.print("Downloading:");
		while((len = in.read(data)) != -1) {
			file.write(data, 0, len);
			count++;
			if(roll == 0) {
				System.out.print("███ ███ ███ ███ ███ ███ ███ ███ ███ ███");
			} else if(0.1<=(count/roll)) {
				System.out.print("███ ");
				count = 0.0;
			}
		}
		System.out.println();
		System.out.println("Successful!");
		//写反馈信息
		OutputStream out = socket.getOutputStream();
		out.write("分享成功".getBytes());
		//关闭流
		out.close();
		file.close();
		in.close();
		socket.close();
	}

	//发送者模块
	private static void send() throws IOException, InterruptedException {
		System.out.println();
		System.out.print("启动Server服务器中 ......");
		Thread.sleep(500);
		System.out.print(" ......");
		Thread.sleep(500);
		System.out.println(" ......");
		//文件输入流
		System.out.println();
		System.out.println("输入要发送的文件or文件夹绝对路径（拖入即可）：");
		System.out.println();
		System.out.print("==>>:");
		Scanner sc = new Scanner(System.in);
		String fileString = sc.next();
		fileString = fileString.replace("\\", "\\\\");
		File file = new File(fileString);
		//打开端口,显示本机IP
		ServerSocket server = new ServerSocket(2018);
		String host = InetAddress.getLocalHost().getHostName();
		System.out.println();
		for(InetAddress it : InetAddress.getAllByName(host)) {
			System.out.printf("%-12s%-40s%15s","本服务器IP:",it.getHostAddress(),"Port:2018");
			System.out.println();
		}
		if(file.isFile()) {
			BufferedInputStream bufferfile = new BufferedInputStream(new FileInputStream(file));
			System.out.println();
			System.out.println("------------------------------------------");
			System.out.printf("%-30s%10s",file.getName(),fileSize(file));
			System.out.println();
			System.out.println("------------------------------------------");
			//获取输出流
			System.out.println("等待用户连入中：");
			Socket socket = server.accept();
			System.out.println();
			System.out.println("来自 " + socket.getInetAddress().getHostAddress() + "：" + socket.getPort() + " 的用户已接入...");
			OutputStream out = socket.getOutputStream();
			//发送文件名
			out.write((file.getName() + "          大小：" + fileSize(file)).getBytes());
			out.flush();
			//写数据
			System.out.println();
			System.out.print("分享中：");
			long roll = file.length()/1024;
			double count = 0.0;
			out.write(Long.toString(roll).getBytes());
			out.flush();
			byte[] data = new byte[1024];
			int len = -1;
			Thread.sleep(1000);
			while((len = bufferfile.read(data)) != -1) {
				out.write(data, 0, len);
				count++;
				if(roll == 0) {
					System.out.print("███ ███ ███ ███ ███ ███ ███ ███ ███ ███");
				} else if(0.1<=(count/roll)) {
					System.out.print("███ ");
					count = 0.0;
				}
			}
			//数据发送完毕,结束输出流的写入操作
			socket.shutdownOutput();
			System.out.println();
			//读反馈信息
			InputStream in = socket.getInputStream();
			len = in.read(data);
			System.out.println(new String(data, 0, len));
			//关闭流
			in.close();
			bufferfile.close();
			out.close();
			socket.close();
			System.out.println();
		}
		server.close();
	}

	//获取文件大小
	private static String fileSize(File file) {
		long len = file.length();
		if(len < 1024) {
			return len + "B";
		} else if(1024 <= len & len < 1024*1024) {
			DecimalFormat df = new DecimalFormat("#.00");
			return df.format(len/1024.0) + "KB";
		} else if(1024*1024 <= len & len < 1024*1024*1024) {
			DecimalFormat df = new DecimalFormat("#.00");
			return df.format(len/1024.0/1024.0) + "MB";
		} else {
			DecimalFormat df = new DecimalFormat("#.00");
			return df.format(len/1024.0/1024.0/1024.0) + "GB";
		}
	}
}
