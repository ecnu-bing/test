package SN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.Response;
import weibo4j.model.User;
import weibo4j.org.json.JSONException;
import Util.MyLogger;
import Util.Rootuid;
import Util.Token;
import Util.TokenManage;
import Util.Tool;

public class GetShortUrl {

	Weibo weibo = null;
	TokenManage tm = null;
	Token tokenpack = null;
	String dataStr = null;
	String outStr = null;
	static Rootuid ioControl = null;
	boolean isok = true;
	Date start = null;
	static SimpleDateFormat inputFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	static LogManager lMgr = LogManager.getLogManager();
	static String thisName = "WeiboLog";
	static Logger log = Logger.getLogger(thisName);
	static MyLogger mylogger = null;
	int startfrom = -1;
	public int getStartfrom()
	{
		return startfrom;
	}

	public void setStartfrom(int startfrom)
	{
		this.startfrom = startfrom;
	}

	static Set<String> listSet;

	int localIpcout;

	public static void main(String[] args) throws weibo4j.model.WeiboException, IOException, WeiboException, JSONException
	{
		String input = ".\\shorturl.txt";
		String output= ".\\longurl.txt";
		
		GetShortUrl getuserThread = new GetShortUrl();
		//getuserThread.setStartfrom(x); //skip from data before x
		getuserThread.SetIO(input, output);
		getuserThread.run();
	}

	public void SetIO(String input, String output)
	{
		this.dataStr = input;
		this.outStr = output;
		isok = true;
	}
	
	void init()
	{
		inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		listSet = new HashSet<String>();

		try
		{
			Tool.refreshToken();
			tm = new TokenManage();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		start = new Date();
		System.out.println("Start Crawling at "
				+ inputFormat.format(start.getTime()));
	}

	public void end()
	{
		System.out.println("All data get succeeded at "
				+ inputFormat.format(new Date().getTime()));

		System.out.println("Using "
				+ ((new Date()).getTime() - start.getTime()) / 60000
				+ " minutes");
	}

	public String GetUserInfo(Weibo weibo, String uid) throws weibo4j.model.WeiboException, WeiboException, JSONException
	{
		String ret = null;
		try
		{
			
			System.out.println(uid);
			//ret = weibo.showUser(uid).toString();
			Response res = weibo.shorturlTolongurl(uid);
			Tool.write(".//jason.txt", res.asString());
			
			//ret = user.getName() + "\t" + user.getFollowersCount() + "\t"
				//	+ user.getFriendsCount() + "\t" + user.getStatusesCount()+"\t"+user.getFavouritesCount();
			//ret=user.getId()+"\t"+user.getFollowersCount()+"|#|"+user.getName()+"|#|"+user.getStatusesCount()+"|#|"+user.getFriendsCount()+"|#|"+user.getFavouritesCount()+"|#|"+user.getGender()+"|#|"+user.getLocation()+"|#|"+user.isVerified()+"|#|"+inputFormat.format(user.getCreatedAt());
		   ret=res.asJSONObject().getJSONArray("urls").getJSONObject(0).getString("url_long");
		  
		}
		catch (weibo4j.model.WeiboException e1)
		{
		   Tool.write(".\\error", uid);
		}
		return ret;
	}

	public void MainCrawl() throws weibo4j.model.WeiboException, IOException, WeiboException, JSONException
	{
		
	
	FileInputStream fis;
    InputStreamReader isr;
    BufferedReader br = null;
    String line = null;
     
    File file = new File(this.dataStr);
     //File file = new File("Z:\\code\\data\\report\\RetweetTree1\\part-00000");
   
    fis = new FileInputStream(file);
    isr = new InputStreamReader(fis,"utf-8");
    br = new BufferedReader(isr);
 
	 
   
		while((line = br.readLine()) != null)
		{
			tokenpack = tm.GetToken();
			
			if (tokenpack == null)
				continue;

			weibo = new Weibo();
			// System.out.println(tokenpack.token+'\t'+tokenpack.secret);
			weibo.setToken(tokenpack.token);
			
			
			String info = GetUserInfo(weibo, line);
			
			if(info!=null){
				Tool.write(this.outStr, info);
			}
			}
			
		}

	public void run() throws weibo4j.model.WeiboException, IOException, WeiboException, JSONException
	{
		if(!isok)
		{
			System.out.println("input and output file not set, use function SetIO to set");
			return;
			
		}
		init();

		MainCrawl();

		end();
	}



}
