package Util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import weibo4j.Weibo;

public class ComTokenManage
{
	static int tokenidletime = 3600000; // use to be 3600000
	static int tosleep = 120000;// !!ms 1 second = 1000, use to be 120000
	static int Maxiumcount = 150;
	static int thresold = 140;
	static int ipthresold = 280;
	private static int ipcount = 0;
	String fpath = ".\\Tokens\\companyToken.txt";

	static TokensPool tokenspool = null;

	int minpos = -1;
	static LogManager lMgr = LogManager.getLogManager();
	static String thisName = "WeiboTokenLog";
	static Logger log = Logger.getLogger(thisName);
	static MyLogger mylogger = null;

	static SimpleDateFormat inputFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	Token tokenpack = null;

	
	int tosleep1 = 3000;
	int tosleep2 = 4 * 60000;
	int tosleep3 = 15 * 60000;
	
	public ComTokenManage()
	{
		mylogger = new MyLogger();

		try
		{
			tokenspool = new TokensPool(fpath);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		tokenpack = tokenspool.GetNewToken();
		replaceKey(tokenpack);
	}

	void replaceKey(Token tokenpack)
	{
		Weibo.CONSUMER_KEY = tokenpack.consumer_key;
		Weibo.CONSUMER_SECRET = tokenpack.consumer_secret;

		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);

		System.out.println("---------------Change KEY----------------");

	}

	public void ChekState()
	{
	}
	

	// if return is null, should continue the loop or sleep the thread
	public Token GetToken()
	{
		return tokenpack;
	}

	public static int getIpcount()
	{
		return ipcount;
	}

	// if return is false, should continue the loop or sleep the thread
	public boolean AddIPCount(int count, String uid)
	{
		boolean ret = true;
		ipcount += count;

		int tosleep = tosleep1;

		if (ipcount % 200 == 0)
		{
			tosleep = tosleep2;
		}
		else if (ipcount % 1000 == 0)
		{
			tosleep = tosleep3;
		}

		try
		{
			System.out.println("Sleep "+tosleep/1000+" seconds");
			Thread.sleep(tosleep);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	public void WriteLog(String uid)
	{
		System.out
				.println(inputFormat.format(new Date()) + " Get id at " + uid);
		mylogger.Write(inputFormat.format(new Date()) + " Get id at " + uid);
		System.out.println(inputFormat.format(new Date())
				+ " Get Token at token pos " + tokenspool.getPos());
		mylogger.Write(inputFormat.format(new Date())
				+ " Get Token at token pos " + tokenspool.getPos());
	}

	public void CloseLogger()
	{
		mylogger.Close();
	}

	public int getPos()
	{
		return tokenspool.getPos();
	}
}
