package GetStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import Dao.Dao;
import Util.MyLogger;
import Util.Rootuid;
import Util.Token;
import Util.TokenManage;
import Util.Tool;
import weibo4j.http.Response;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.User;
import weibo4j.org.json.JSONException;
import weibo4j.Weibo;
import weibo4j.WeiboException;

public class iffriend
{
	Weibo weibo = null;
	TokenManage tm = null;
	Token tokenpack = null;
	String dataStr = null;
	String outStr = null;
	static Rootuid ioControl = null;
	boolean isok = true;
	Date start = null;
	long startTime = 0l;
	int day = 1;
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

	public static void main(String[] args) throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException, JSONException
	{
		String input = ".//data";
		String output= ".//result.txt";
		
		iffriend getuserThread = new iffriend();
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

		startTime = new Date().getTime();
        System.out.println("Start Crawling at "
                + inputFormat.format(startTime));
	}

	public void end()
	{
		System.out.println("All data get succeeded at "
				+ inputFormat.format(new Date().getTime()));

		System.out.println("Using "
				+ ((new Date()).getTime() - start.getTime()) / 60000
				+ " minutes");
	}

	public String GetUserInfo(Weibo weibo, String source,String target) throws weibo4j.model.WeiboException,JSONException, InterruptedException, weibo4j.WeiboException
	{
		String ret = null;
		int day = 1;
		while(true)
        {
		try
		{
			
			StatusWapper wapper = null;
			//ret = weibo.showUser(uid).toString();
		Response res = weibo.showIffriend(source,target);
	    String result=res.asJSONObject().getString("source");
	   
	    ret=result.substring(result.indexOf("following")+11,result.indexOf("followed_by")-2);
       
			//ret = user.getName() + "\t" + user.getFollowersCount() + "\t"
				//	+ user.getFriendsCount() + "\t" + user.getStatusesCount()+"\t"+user.getFavouritesCount();
			//ret=user.getId()+"\t"+user.getFollowersCount()+"|#|"+user.getName()+"|#|"+user.getStatusesCount()+"|#|"+user.getFriendsCount()+"|#|"+user.getFavouritesCount()+"|#|"+user.getGender()+"|#|"+user.getLocation()+"|#|"+user.isVerified()+"|#|"+inputFormat.format(user.getCreatedAt());
	    //Tool.write(outStr, ret);
	    return ret;
		
		}catch (weibo4j.model.WeiboException e1)
		{
	           if(e1.getStatusCode() == 400 || e1.getStatusCode() == 401)
			  {
				System.out.println("token invalid, change token");
				tm.ChekState();
				tokenpack = tm.GetToken();
				
				tm.AddIPCount(1, source);
				while (tokenpack == null){
					tokenpack = tm.GetToken();
			        
			      
				}
				weibo.setToken(tokenpack.token);
				continue;
				}
	           else if(e1.getStatusCode() == 403)			
			   {
                 tm.ChekState();
                 tokenpack = tm.GetToken();
                 tm.AddIPCount(1, source);
                 while (tokenpack == null )
                 {
                     System.out.println(tm.GetToken());
                     tokenpack = tm.GetToken();
                 }  
                 weibo.setToken(tokenpack.token);
                 System.out.println("request too many times , sleep 5~45s");
                 
                 try
                 {
                     double a = Math.random()*50000;  
                     a = Math.ceil(a);  
                     int randomNum = new Double(a).intValue(); 
                     System.out.println("sleep : " +randomNum/1000 +"s");
                     Thread.sleep(randomNum);
                 }catch (InterruptedException e2)
                 {
                     //e1.printStackTrace();
                 }
                 
                 continue;
             }
			else		
			{
				e1.printStackTrace();
			}
			
			//WriteFailLog();
			//e1.printStackTrace();
		}
		
		
        }
	
		
	}

	public void MainCrawl() throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException, JSONException
	{
		
	
	FileInputStream fis;
    InputStreamReader isr;
    BufferedReader br = null;
    String line = null;
     
    File file = new File(dataStr);
     //File file = new File("Z:\\code\\data\\report\\RetweetTree1\\part-00000");
   
    fis = new FileInputStream(file);
    isr = new InputStreamReader(fis,"utf-8");
    br = new BufferedReader(isr);
 
	 
   
		while((line = br.readLine()) != null)
		{
		    if(new Date().getTime() > startTime+day*12*60*60*1000)
	          {
	              ++day;
	              //从网络上更新token
	              Tool.refreshToken();
	              //更新代码使用token
	              tm = new TokenManage();
	          }
			tokenpack = tm.GetToken();
			
			if (tokenpack == null)
				continue;

			weibo = new Weibo();
			
			weibo.setToken(tokenpack.token);
			
			//String detail[]=line.split("\t");
			double a = Math.random()*5000;  
            a = Math.ceil(a);  
            int randomNum = new Double(a).intValue(); 
            System.out.println("sleep : " +randomNum/1000 +"s");
            Thread.sleep(randomNum);
            System.out.println(line);
            line=line.replaceAll("\t", "");
            String[]user=line.split("\\|#\\|");
			String info = GetUserInfo(weibo, user[0],user[1]);
			if(info!=null){
				Tool.write(outStr, line+"\t"+info);
			}
			}
			
		}

	public void run() throws weibo4j.model.WeiboException, IOException, WeiboException, InterruptedException, JSONException
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
