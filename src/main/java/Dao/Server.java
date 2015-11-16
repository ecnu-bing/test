package Dao;/*package Dao;

import java.sql.*; 
import java.io.IOException;
import java.net.DatagramSocket;


public class Server

 * server 用于预处理通用信息：用户登录验证，字符串命令解析。
 
{

	// 用于生成 sql语句
	private static Connection queryconnection;
	private static Statement querystatement;
	// 网络
	// private static InetAddress ServerAddr; //没必要维护
	private static int destPort = 4233;

	// 处理 命令的 枚举类 不用
	// server 只需要 处理 登录命令 然后 用户远程连接数据库
	public static void main(String[] args) throws IOException
	{
		boolean succeed = DBCon();
		if (!succeed)
		{
			System.out.println("Fail when server get connect to sql");
			return;
		}
		// 解析命令
		DatagramSocket serverSocket = new DatagramSocket(destPort); // server 端口
		byte[] receiveData = new byte[1024]; // server 接收数据
		
		 * while (succeed) { DatagramPacket receivePacket = new
		 * DatagramPacket(receiveData, receiveData.length); // 接收 数据包
		 * serverSocket.receive(receivePacket); String sentence = new
		 * String(receivePacket.getData()); // 得到数据 InetAddress ClientAddress =
		 * receivePacket.getAddress(); int port = receivePacket.getPort(); //
		 * 得到客户端 位置
		 * 
		 * int position = sentence.indexOf("@"); // 解析 命令 String cmd =
		 * sentence.substring(0, position); String next =
		 * sentence.substring(position + 1);
		 * 
		 * if (cmd.equalsIgnoreCase("login")) Login(ClientAddress, port, next);
		 * else System.out.println("bad command"); }
		 * System.out.println("Client logout!");
		 

	}

	public static boolean DBCon()
	{
		System.out.println("server is connecting to DB");

		String user = "wmd";
		String password = "111";
		// connect to database
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println("数据库驱动程序注册成功！");
			String url = "jdbc:mysql://localhost:3306/graph";
			queryconnection = DriverManager.getConnection(url, user, password);
			System.out.println("数据库连接成功");
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("数据库连接失败");
			return false;
		}
	}

	
	 * public static void Login(InetAddress ClientAddr, int Port, String next) {
	 * byte[] sendData = new byte[1024]; // server 发送数据 char a =
	 * next.charAt(next.length() - 1); int len = next.indexOf(a); next =
	 * next.substring(0, len);
	 * 
	 * int position = next.indexOf("@"); String username = next.substring(0,
	 * position); String password = next.substring(position + 1, next.length());
	 * 
	 * System.out.println("欢迎登录，正在验证！"); // sql语句变量处理 String sql =
	 * "select code from users where name = '" + username + "'";
	 * 
	 * try
	 * 
	 * { querystatement = queryconnection.createStatement(); ResultSet rs =
	 * querystatement.executeQuery(sql); // 执行数据的查询语句(select); String code =
	 * null; if (rs.next()) { code = rs.getString(1); System.out.println(code +
	 * "codecodecodecodeeeeeeeeeeeeeeeeeeeeeeeeeeee"); } else {
	 * System.out.println("没有此用户\n"); String to_send = "false@no_this_user";
	 * sendData = to_send.getBytes(); // 生成 可发送 DatagramSocket mySocket = new
	 * DatagramSocket(); DatagramPacket mySend = new DatagramPacket(sendData,
	 * sendData.length, ClientAddr, Port); mySocket.send(mySend); return; } if
	 * (code.equalsIgnoreCase(password)) { System.out.println("用户" + username +
	 * "登录成功"); String user_id_s = null; String program_id_s = ""; String
	 * program_id_s_node = "";
	 * 
	 * sql = "select user_id from users where name ='" + username + "'";
	 * 
	 * rs = querystatement.executeQuery(sql); if (rs.next()) { user_id_s =
	 * rs.getString(1); } int user_id = Integer.parseInt(user_id_s);
	 * 
	 * sql = "select program_id from user_program where user_id = " + user_id;
	 * rs = querystatement.executeQuery(sql); while (rs.next()) {
	 * program_id_s_node = rs.getString(1); program_id_s = program_id_s +
	 * program_id_s_node + "@"; }
	 * 
	 * String to_send = "true@" + user_id_s + "@" + program_id_s; sendData =
	 * to_send.getBytes(); // 生成 可发送 DatagramSocket mySocket = new
	 * DatagramSocket(); DatagramPacket mySend = new DatagramPacket(sendData,
	 * sendData.length, ClientAddr, Port); mySocket.send(mySend); } else {
	 * System.out.println("密码错误\n"); String to_send = "false@  wrong_code";
	 * sendData = to_send.getBytes(); // 生成 可发送 DatagramSocket mySocket = new
	 * DatagramSocket(); DatagramPacket mySend = new DatagramPacket(sendData,
	 * sendData.length, ClientAddr, Port); mySocket.send(mySend); return; } }
	 * catch (Exception e) { e.printStackTrace(); return; } }
	 
}
*/