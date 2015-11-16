package Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import weibo4j.Weibo;

public class TokenManage
{
	static int tokenidletime = 3600000; // use to be 3600000
	static int tosleep = 120000;// !!ms 1 second = 1000, use to be 120000
	static int Maxiumcount = 150;
	static int thresold = 140;
	static int iplimit = 10000;
	static int tokennum = 540;
	static int ipthreshold = 500;//(int) (30.0 / (iplimit +0.0)* tokennum * tokennum) ;
	private static int ipcount = 0;

	static TokensPool tokenspool = null;

	int minpos = -1;
	static LogManager lMgr = LogManager.getLogManager();
	static String thisName = "WeiboTokenLog";
	static Logger log = Logger.getLogger(thisName);
	static MyLogger mylogger = null;

	static SimpleDateFormat inputFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public TokenManage()
	{
		mylogger = new MyLogger();

		try
		{
			tokenspool = new TokensPool();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

	}

	void replaceKey(Token tokenpack)
	{
		Weibo.CONSUMER_KEY = tokenpack.consumer_key;
		Weibo.CONSUMER_SECRET = tokenpack.consumer_secret;
        Weibo.REDIRECT_URI=tokenpack.redirectUrl;
		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);

		System.out.println("---------------Change KEY----------------");

	}

	public void ChekState()
	{
		
		if ((ipcount >= ipthreshold) && (ipcount % ipthreshold == 0))
		{
			System.out.println("======Sleep " + tosleep / 1000
					+ " seconds======");
			try
			{
				Thread.sleep(tosleep);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("============restart============");
		}
	}

	void RefreshAllTime()
	{
		long mintime = 0;
		for (int i = 0; i < tokenspool.getTokenList().size(); i++)
		{
			mintime = Math.max(mintime, tokenspool.getTokenList().get(i)
					.getTimestamp());
		}

		long remain = tokenidletime + mintime - (new Date()).getTime();// ms

		System.out.println("=========token sleep at "
				+ inputFormat.format(new Date()) + " for " + remain / 1000
				+ " seconds==============");

		if (remain > 0)
		{
			try
			{
				Thread.sleep(remain);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			remain = 0;
		}

		System.out.println("=====token awake at "
				+ inputFormat.format(new Date()) + "======");

		for (int i = 0; i < tokenspool.getTokenList().size(); i++)
		{
			tokenspool.getTokenList().get(i).setCount(0);
			tokenspool.getTokenList().get(i).AddTimestamp(remain);

		}
	}

	public void deleteToken()
	{
	    tokenspool.getTokenList().remove(0);
	}
	// if return is null, should continue the loop or sleep the thread
	public Token GetToken()
	{
		Token tokenpack = null;

		minpos = tokenspool.GetMinPos();
		tokenpack = tokenspool.getTokenList().get(minpos);

		if (tokenpack == null)
		{
			System.out.println("Token error");
			mylogger.Write("Token error");
			return null;
		}

		if (tokenpack.getCount() < Maxiumcount)
		{
			//replaceKey(tokenpack);
		}
		else
		{
			RefreshAllTime();
			return null;
		}

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

		if (minpos == -1)
			minpos = tokenspool.GetMinPos();

		if (count == -1)
		{
			tokenspool.getTokenList().get(minpos).setCount(Maxiumcount);
			log.log(Level.SEVERE, "miss uid " + uid);
			mylogger.Write("miss uid " + uid);
			return false;
		}
		
		
		try
		{// frozen KEY?
			tokenspool.getTokenList().get(minpos).addCount(count + 1);

			if (tokenspool.getTokenList().get(minpos).getCount() > thresold)
				tokenspool.getTokenList().get(minpos).setCount(150);

		}
		catch (Exception ioe)
		{
			WriteLog(uid);
			System.out.println("Failed to read the system input.");
			System.exit(-1);
		}

		return ret;
	}
	
	public boolean AddIPCount(int count)
	{
		boolean ret = true;
		ipcount += count;

		if (minpos == -1)
			minpos = tokenspool.GetMinPos();

		if (count == -1)
		{
			tokenspool.getTokenList().get(minpos).setCount(Maxiumcount);
		
			return false;
		}
		
		
		try
		{// frozen KEY?
			tokenspool.getTokenList().get(minpos).addCount(count + 1);

			if (tokenspool.getTokenList().get(minpos).getCount() > thresold)
				tokenspool.getTokenList().get(minpos).setCount(150);

		}
		catch (Exception ioe)
		{
			System.exit(-1);
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
