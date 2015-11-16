package SN;

import Dao.Dao;
import Dao.JDBC;
import Util.*;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.model.IDs;
import weibo4j.model.Paging;
import weibo4j.model.User;

import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class GetFollowshipToDB
{

	/**
	 * Usage: java -DWeibo4j.oauth.consumerKey=[consumer key]
	 * -DWeibo4j.oauth.consumerSecret=[consumer secret]
	 * Weibo4j.examples.GetFriends [accessToken] [accessSecret]
	 * 
	 * @param args
	 *            message
	 */

	TokenManage tm = null;
	Token tokenpack = null;
	Weibo weibo = null;
	int day = 1;
	long startTime = 0l;
	// ComTokenManage tm = null;
	static String dir = ".\\data\\";
	static String dataStr = ".\\data\\level1";
	String completeuidPath = "."+File.separator+"data"+File.separator+"uid_UpdateComplete";
	static String outStr = "";

	static int leveldepth = 4;

	static Rootuid rootuid = null;

	private static Connection queryconnection = null;
	static Dao dao;

	Date start = null;
	static SimpleDateFormat inputFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	static LogManager lMgr = LogManager.getLogManager();
	static String thisName = "WeiboLog";
	static Logger log = Logger.getLogger(thisName);
	static MyLogger mylogger = null;

	static Set<String> listSet;

	int getFollowerCount = 0;
	long followcursour = 0;
	int localIpcout;

	int FollowShresold = 5000;
	
	Set<String> completeUidSet = null;

	void init()
	{
		lMgr.addLogger(log);

		mylogger = new MyLogger();
		lMgr.addLogger(log);

		inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		listSet = new HashSet<String>();
		completeUidSet = new HashSet<String>();

		try
		{
			// tm = new ComTokenManage();
			tm = new TokenManage();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		readCompleteUid();
		start = new Date();
		System.out.println("Start Crawling at "+ inputFormat.format(start.getTime()));

	}

	void readCompleteUid()
    {
        File file = new File(completeuidPath);
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis,"utf-8");
            br = new BufferedReader(isr);
            while((line = br.readLine()) != null)
            {
                if (!completeUidSet.contains(line))
                {
                    completeUidSet.add(line);
                }
            }
            fis.close();
            isr.close();
            br.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
	public int MutedTest(Weibo weibo, String usrid)
	{
		int requirecount = -1;

		System.out.print("Query API...");
		localIpcout++;
		requirecount = 1;

		System.out.println("....Get succeded");

		return requirecount;
	}

	public int GetfollowCount(Weibo weibo, String usrid)
	{
		int friendcount = -1;
		try
		{
			localIpcout++;
            User rootuser = weibo.showUserById(usrid);
			friendcount = rootuser.getFollowersCount();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return friendcount;
	}



	public int CheckData(String usrid)
	{
		int requirecount = -1;
		try
		{
			if (dao.IsInuserlist(String.valueOf(usrid)) == false)
			{
				System.out.println("Missing uid: " + usrid);
			}
			else
			{

			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return requirecount;
	}

	void WriteFailLog()
	{
		System.out.println(inputFormat.format(new Date())
				+ " #Fail Get id at rootuid pos of " + rootuid.getPos());
		mylogger.Write(inputFormat.format(new Date())
				+ " #Fail Get id at rootuid pos of " + rootuid.getPos());
		System.out.println(inputFormat.format(new Date())
				+ " #Fail Get Token at token pos " + tm.getPos());
		mylogger.Write(inputFormat.format(new Date())
				+ " #Fail Get Token at token pos " + tm.getPos());
	}

	void myGetFollows(Weibo weibo,String uid) throws weibo4j.model.WeiboException, WeiboException, InterruptedException
	{
		String timestamp = inputFormat.format(new Date().getTime());
		Long myCursor = null;
		IDs ids = null;
		int count = 0;
		int count1=0;
		
		Paging paging = new Paging();
		paging.setCount(5000);
		int page = 1;
		Vector<String> oldList = dao.GetUserFollowersID(uid);
		
		Set<String> oldlistSet = new HashSet<String>(oldList);
		Vector<String> newList = new Vector<String>();
		getFollowerCount = 0;
		boolean flag = false;
  
		//for(int page=1;;page++)
		//{
		while(true)
		{
			try
			{
				paging.setCount(5000);
				paging.setPage(page);
				ids =weibo.getFollowsIDSByUserId(uid,paging);  
				//System.out.print(page);
			}
			catch(weibo4j.model.WeiboException e1){
		           if(e1.getStatusCode() == 400 || e1.getStatusCode() == 401)
				  {
					System.out.println("token invalid, change token");
					tm.ChekState();
					tokenpack = tm.GetToken();
					
					tm.AddIPCount(localIpcout, uid);
					while (tokenpack == null)
						tokenpack = tm.GetToken();
					
					weibo.setToken(tokenpack.token);
					continue;
					}
		           else if(e1.getStatusCode() == 403)			
				   {					
					System.out.println("request too many times , sleep 5~45s");
					try
					{						
					    double a = Math.random()*50000;  
	                    a = Math.ceil(a);  
	                    int randomNum = new Double(a).intValue(); 
	                    System.out.println("sleep : " +randomNum/1000 +"s");
	                    Thread.sleep(randomNum);
	                    tm.ChekState();
						tokenpack = tm.GetToken();
						tm.AddIPCount(localIpcout, uid);
						while (tokenpack == null)
							tokenpack = tm.GetToken();
						
						weibo.setToken(tokenpack.token);
						continue;
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
		           else		
					{
						 double a = Math.random()*50000;  
		                 a = Math.ceil(a);
						 int randomNum = new Double(a).intValue(); 
		                 System.out.println("sleep : " +randomNum/1000 +"s");
		                 Thread.sleep(randomNum);
		                 continue;
					}
				
				//WriteFailLog();
				//e1.printStackTrace();
			}
	
                break;
		}
			
		
			followcursour = ids.getSize();

			if(followcursour >=5000)
			{
			    flag = false;
			}
			else
			{
			    flag = true;
			}
			
			for (long id : ids.getIDs())
	        {
	            if(!oldlistSet.contains(String.valueOf(id)))
	            {   
	             
	                if (dao.InsertFollowerslist(uid, String.valueOf(id), String.valueOf(followcursour)) == true)
	                {      
	                    
	                    count ++;
	                    getFollowerCount ++;
	                    followcursour--;
	                    newList.add(String.valueOf(id));
	                }
	                else
	                // write log
	                {
	                    
	                    mylogger.Write("followship_writing_failed:" + uid + " "
	                            + String.valueOf(id));
	                }
	            
	            }
					
			}

        if(flag&&oldList.size()!=0){
        CompareList(uid,oldList, newList, timestamp);
        }
	}
	
	
	private void CompareList(String uid, Vector<String> vOld, Vector<String> vNew, String timestamp)
	{
		Set<String> NewlistSet = new HashSet<String>(vNew);
		
	
		
		    for(String fuid:vOld)
	        {
	            if(!NewlistSet.contains(fuid))
	            {
	                dao.SetFollowerslistFlag( uid,fuid,timestamp);
	            }
	        }
		
	}
	
	void CrawlData()throws weibo4j.model.WeiboException, WeiboException, InterruptedException
	{
		if(new Date().getTime() > startTime+day*12*60*60*1000)
        {
            ++day;
            //从网络上更新token
            Tool.refreshToken();
            //更新代码使用token
            tm = new TokenManage();
        }
		while (!rootuid.IsOver())
		{
			localIpcout = 0;
			String uid = rootuid.GetNewIDDontPlus();

			if(completeUidSet.contains(uid))
            {
                System.out.println(uid + " has been crawled");
                rootuid.idOK();
                continue;
            }
			
			tm.ChekState();
			tokenpack = tm.GetToken();

			if (tokenpack == null)
				continue;

			System.out.println("Start getting " + uid);
			weibo = new Weibo();
			
			weibo.setToken(tokenpack.token);

			//Integer followcount = GetfollowCount(weibo, uid);
			
			// -----------------------------------------------------------------
			myGetFollows(weibo,uid);
            System.out.println("Finished Get " + this.getFollowerCount + " followers");
            
            rootuid.idOK();
            tm.AddIPCount(1, uid.toString());
            write(completeuidPath,uid);
            completeUidSet.add(uid);
            System.out.println("Now ipcount is " + TokenManage.getIpcount());
            
			}
		mylogger.Close();
		System.out.println("====================completed=======================");
		
	}

	private void write(String filepath,String str)
    {
        FileWriter fileWriter = null;
        BufferedWriter bw= null;
        try {
            fileWriter = new FileWriter(filepath,true);
            bw = new BufferedWriter(fileWriter);
            bw.append(str);
            bw.newLine();    
            bw.close();
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }


	boolean GetDB()
	{
		// !!JDBC.setURL();
		JDBC.setURL("jdbc:mysql://localhost:3306/test");
		queryconnection = JDBC.getConnection();
		
		if (queryconnection == null)
		{
			System.out.println("Fail when server get connect to sql");
			return false;
		}

		dao = new Dao();

		// dao.setToprint(true);
		// dao.setToexecute(false);

		return true;
	}

	public void IDOutput(int level)
	{
		if (level > 3)
			return;

		if (listSet.size() <= 0)
			return;

		String[] uidlist = new String[listSet.size()];
		listSet.toArray(uidlist);

		for (int j = 0; j < listSet.size(); j++)
		{
			rootuid.Write(uidlist[j]);
		}

		rootuid.closeWrite();

		System.out.println("Getting from DB Store in " + outStr);
	}

	public void AddtoListSet(long id, int level)
	{
		if (level > 3)
			return;
		if (listSet.contains(String.valueOf(id)) == false)
			listSet.add(String.valueOf(id));
	}

	// ---------------------------------------------------------------------------------------------
	public static void main(String[] args) throws weibo4j.model.WeiboException, WeiboException, InterruptedException
	{
		GetFollowshipToDB friendsCrawler = new GetFollowshipToDB();
		friendsCrawler.run();
	}

	public void run() throws weibo4j.model.WeiboException, WeiboException, InterruptedException
	{
		init();

		if (!GetDB())
			return;
		
		DailyCrawl();
		end();

	}

	void end()
	{
		System.out.println("All data get succeeded at "
				+ inputFormat.format(new Date().getTime()));

		System.out.println("Using "
				+ ((new Date()).getTime() - start.getTime()) / 60000
				+ " minutes");
	}

	public void DailyCrawl() throws weibo4j.model.WeiboException, WeiboException, InterruptedException
	{
		CrawlData();

	}



}

