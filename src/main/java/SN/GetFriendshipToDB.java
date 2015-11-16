package SN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import Dao.Dao;
import Dao.JDBC;
import Util.MyLogger;
import Util.Rootuid;
import Util.Token;
import Util.TokenManage;
import Util.Tool;

import weibo4j.Friendships;
import weibo4j.Paging;
import weibo4j.Users;
import weibo4j.Weibo;

import weibo4j.WeiboException;
import weibo4j.model.IDs;
import weibo4j.model.User;
import weibo4j.model.UserWapper;

public class GetFriendshipToDB
{

	/**
	 * Usage: java -DWeibo4j.oauth.consumerKey=[consumer key]
	 * -DWeibo4j.oauth.consumerSecret=[consumer secret]
	 * Weibo4j.examples.GetFriends [accessToken] [accessSecret]
	 * 
	 * @param args
	 *            message
	 */
	Weibo weibo = null;
	TokenManage tm = null;
	Token tokenpack = null;
	int day = 1;
	long startTime = 0l;
	// ComTokenManage tm = null;
	static String dir = ".\\data\\";
	static String dataStr = ".\\data\\level1";
	static String outStr = "";

	static int leveldepth = 4;

	static Rootuid rootuid = null;

	private static Connection queryconnection = null;
	static Dao dao;

	static SimpleDateFormat inputFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	static LogManager lMgr = LogManager.getLogManager();
	static String thisName = "WeiboLog";
	static Logger log = Logger.getLogger(thisName);
	static MyLogger mylogger = null;

	static Set<String> listSet;

	int localIpcout;
	//String completeuidPath = "."+File.separator+"data"+File.separator+"uid_UpdateComplete";
	//Set<String> completeUidSet = null;
	boolean flag = false;
	void init()
	{    
		lMgr.addLogger(log);

		mylogger = new MyLogger();
		lMgr.addLogger(log);

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

	
	public int MutedTest(Weibo weibo, String usrid)
	{
		int requirecount = -1;

		System.out.print("Query API...");
		localIpcout++;
		requirecount = 1;

		System.out.println("....Get succeded");

		return requirecount;
	}

	
	public int GetfriendCount(Weibo weibo, String usrid)
	{
		int friendcount = -1;
		try
		{
			localIpcout++;
			Users u = null;
			User rootuser = u.showUserById(usrid);
			friendcount = rootuser.getFriendsCount();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return friendcount;
	}

	public int GetfollowCount(Weibo weibo, String usrid)
	{
		int friendcount = -1;
		try
		{
			localIpcout++;
			Users u = null;
			User rootuser = u.showUserById(usrid);
			friendcount = rootuser.getFollowersCount();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return friendcount;
	}

	public int Getfriends_New(Weibo weibo, String usrid, int level) throws weibo4j.model.WeiboException, WeiboException, InterruptedException
	{
		String timestamp = inputFormat.format(new Date().getTime());
		
		int reqcount = 0;
		Paging paging = new Paging();
		
		paging.setCount(5000);
		int page = 1;

		int count = 0;
		boolean isCom = true;
	
		Vector<String> oldList = dao.GetUserFriendsID(usrid);
		Set<String> oldlistSet = new HashSet<String>(oldList);
		Vector<String> newList = new Vector<String>();
		
		paging.setPage(page);
		reqcount++;
		localIpcout++;
		IDs ids = null;
		long cc = 0;
		
		while(true)
		{
			try
			{   
			    ids = weibo.getFriendsIDSByUserId(usrid,5000);
				
			}
			catch (weibo4j.model.WeiboException e1)
			{
		           if(e1.getStatusCode() == 400 || e1.getStatusCode() == 401)
				  {
					System.out.println("token invalid, change token");
					tm.ChekState();
					tokenpack = tm.GetToken();
					
					tm.AddIPCount(localIpcout, usrid);
					while (tokenpack == null)
						tokenpack = tm.GetToken();
					System.out.println(tokenpack.token);
					weibo.setToken(tokenpack.token);
					continue;
					}
		           else if(e1.getStatusCode() == 403)			
				   {					
					System.out.println("request too many times , sleep 5~45s");
					try
					{						
					    double a =Math.random()%10*1000;  
	                    a = Math.ceil(a);  
	                    int randomNum = new Double(a).intValue(); 
	                    System.out.println("sleep : " +randomNum/1000 +"s");
	                    Thread.sleep(randomNum);
	                    tm.ChekState();
						tokenpack = tm.GetToken();
						tm.AddIPCount(localIpcout, usrid);
						while (tokenpack == null)
							tokenpack = tm.GetToken();
						System.out.println(tokenpack.token);
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
		
	
        
        if(cc >= 5000)
        {
            isCom = false;
        }
        else
        {
            isCom = true;
        }
		for (long id : ids.getIDs())
		{
			//System.out.println(id);
		
			if(!oldlistSet.contains(String.valueOf(id)))
			{
				if (dao.InsertFriendslist(usrid, String.valueOf(id), String
						.valueOf(cc)) == true)
				{
					cc--;
					count++;
					newList.add(String.valueOf(id));
				}
				else
				// write log
				{
					mylogger.Write("friendship_writing_failed:" + usrid + " "
							+ String.valueOf(id));
				}
			}
			if (dao.IsInuserlist(String.valueOf(String.valueOf(id))) == false)
			{
				if (dao.InsertUserlist(String.valueOf(id), "", level) == false)
					mylogger.Write("uselist_insert_failed:" + usrid + " "
							+ String.valueOf(id));
				AddtoListSet(id, level);
			}
			else
			{
				int flag = dao.GetFlagFromUserlist(String.valueOf(id));
				if (level < flag)
				{
					if(dao.UpdateFlagUserlist(String.valueOf(id), level) == false)
						mylogger.Write("flag_update_failed:" + usrid + " "
								+ String.valueOf(id));
					AddtoListSet(id, level);
				}
			}
		}
		CompareList(usrid,oldList, newList, timestamp,isCom);
		System.out.println("Finished Get " + count + " friends");
		/*try
		{
		    double a = Math.random()*1000;  
            a = Math.ceil(a);  
            int randomNum = new Double(a).intValue(); 
            Thread.sleep(randomNum);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}*/
		return reqcount;
	}

	private void CompareList(String uid, Vector<String> vOld, Vector<String> vNew, String timestamp,boolean isCom)
	{
	    if(isCom)
	    {
	        Set<String> NewlistSet = new HashSet<String>(vNew);
	        
	        for(String fuid:vOld)
	        {
	            if(!NewlistSet.contains(fuid))
	            {
	                dao.SetFriendlistFlag( uid,fuid, true, timestamp);
	            }
	        }
	    }
		
	}
	
	/*public int Getfollows_New(Weibo weibo, String usrid, int friendcount,int level) throws WeiboException, weibo4j.model.WeiboException
	{
		int reqcount = 0;
		Paging paging = new Paging();
		paging.setCount(5000);
		int page = 1;

		int count = 0;
		int cc = friendcount;
		while (cc > 0)
		{
			paging.setPage(page++);
			reqcount++;
			localIpcout++;
			Friendships fm = new Friendships();
			UserWapper ids = fm.getFriendsByID(usrid);

			for (User u : ids.getUsers())
			{
				/*if (dao.InsertFollowerslist(usrid, String.valueOf(id), String
						.valueOf(cc)) == true)
				{
					cc--;
					count++;
				}
				else
				// write log
				{
					mylogger.Write("followship_writing_failed:" + usrid + " "+ String.valueOf(id));
				}

				if (cc < 0)
					break;*/
				//System.out.println(u.toString());
			//}
		//}
		//System.out.println("Finished Get " + count + " followers");
	//	return reqcount;
	//}

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

	void CrawlData(int level,int pos) throws weibo4j.model.WeiboException, WeiboException, InterruptedException
	{
		  if(new Date().getTime() > startTime+day*12*60*60*1000)
          {
              ++day;
              //从网络上更新token
              Tool.refreshToken();
              //更新代码使用token
              tm = new TokenManage();
          }
		  
		if(pos!=0){

		System.out.println("Start at pos: "+pos);
		if(pos != -1);
		rootuid.setPos(pos);
	}
		
		while (!rootuid.IsOver())
		{
			localIpcout = 0;
			String uid = rootuid.GetNewIDDontPlus();

			if(dao.IsInUpdateUidList(uid))
			if(dao.IsInFriendslist(uid))
           {
              System.out.println(uid + " has been crawled");
              rootuid.idOK();
               continue;
            }
			tm.ChekState();
			tokenpack = tm.GetToken();
			
			/*keyCount++;
			if(keyCount % 46 == 0 || keyCount ==1)
			{
				continue;
			}*/
			if (tokenpack == null)
				continue;

			System.out.println("Start getting " + uid);

			weibo = new Weibo();
			
			weibo.setToken(tokenpack.token);
			//weibo.setToken("2.00unksIC0m1qpE614977baddvdvq1D");

			// -----------------------------------------------------------------
			
			 int responsecount = Getfriends_New(weibo, uid,level+1);
			
			tm.AddIPCount(localIpcout, uid);
			System.out.println("Now ipcount is " + TokenManage.getIpcount());

			// -----------------------------------------------------------------
			dao.InsertUpdateUidlist(uid,level);
			rootuid.idOK();
		}
		mylogger.Close();
		System.out.println("====================Level " + level + " is completed=======================");
		mylogger.Write("====================Level " + level + " is completed=======================");
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
	public void GetPredata(int level)
	{
		System.out.println("Getting level " + level + " data from File");
		dataStr = dir + "level" + String.valueOf(level);
		outStr = dir + "level" + String.valueOf(level + 1);

		rootuid = new Rootuid(dataStr, outStr, true);

		Vector<String> v = null;
		if (level < 2)
		{
			ArrayList<String> level1List = Rootuid.getIds();
			for(String uid: level1List)
			{			
				dao.InsertUserlist(uid, " ", 1);
				
			}
			v = dao.GetLevelUID(level + 1);
		}
		else if(level<3 )
		{
			v = dao.GetLevelUID(level+1);
		}else
		{
		    v = new Vector<String>();
		}

		listSet = new HashSet<String>(v);
	}

	boolean GetDB()
	{
		// !!JDBC.setURL();
		JDBC.setURL("jdbc:mysql://127.0.0.1:3306/test");
		
		queryconnection = JDBC.getConnection();

		if (queryconnection == null)
		{
			System.out.println("Fail when server get connect to sql");
			return false;
		}

		dao = new Dao();

		 //dao.setToprint(true);
		//dao.setToexecute(false);

		return true;
	}

	public void IDOutput(int level)
	{
		if (level > 4)
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
		GetFriendshipToDB friendsCrawler = new GetFriendshipToDB();
		friendsCrawler.run();
		  
	}

	public void run() throws weibo4j.model.WeiboException, WeiboException, InterruptedException
	{
	    init();

		if (!GetDB())
		return;

		String cracklevel=dao.GetLatestCrawledLevel();
		String crackuid=dao.GetLatestCrawledUid();
		System.out.println(cracklevel);
		if(cracklevel.equals("0")){
	    
		DailyCrawl();
		}
		else{	
		
         int crackLevel=Integer.valueOf(cracklevel);	
		 ErrorRecover(crackLevel, crackuid);
		 }

		end();

	}

	void end()
	{
		System.out.println("All data get succeeded at "
				+ inputFormat.format(new Date().getTime()));

		System.out.println("Using "
				+ ((new Date()).getTime() - startTime) / 60000
				+ " minutes");
	}

	public void DailyCrawl() throws weibo4j.model.WeiboException, WeiboException, InterruptedException
	{
		for (int i = 1; i < leveldepth && i < 4; i++)
		{
			GetPredata(i);

			CrawlData(i,0);

			IDOutput(i);
		}
	}

	public void ErrorRecover(int level, String uid) throws weibo4j.model.WeiboException, WeiboException, InterruptedException
	{
		for (int i = level; i < leveldepth && i < 4; i++)
		{
			GetPredata(i);
			int crackpos = rootuid.IDGetPos(uid)+1;
			if (rootuid.setPos(crackpos))
			{
				System.out.println("Recover from level " + level + "...");
				System.out.println("Recover from data " + crackpos);
				CrawlData(i,crackpos);
				IDOutput(i);

			}
			else
			{
				System.out.println("Fail to recover because of a wrong pos");
			}
		}
	}

}
