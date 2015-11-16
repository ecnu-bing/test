package Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ProxyManage {
	static int tokenidletime = 3600000; // use to be 3600000
	static int tosleep = 120000;// !!ms 1 second = 1000, use to be 120000
	static int Maxiumcount = 150000;
	static int thresold = 140;
	static int iplimit = 100000000;
	static int tokennum = 540;
	static int ipthreshold = 500;//(int) (30.0 / (iplimit +0.0)* tokennum * tokennum) ;
	private static int ipcount = 0;

	static ProxyPool proxypool = null;

	int minpos = -1;
	static LogManager lMgr = LogManager.getLogManager();
	static String thisName = "WeiboTokenLog";
	static Logger log = Logger.getLogger(thisName);
	static MyLogger mylogger = null;

	static SimpleDateFormat inputFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public ProxyManage()
	{
		mylogger = new MyLogger();

		try
		{
			proxypool = new ProxyPool();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

	}

	void replaceKey(Proxy proxy)
	{
		
		System.setProperty("http.proxySet", "true"); 	
		System.setProperty("http.proxyHost", proxy.url);
		System.setProperty("http.proxyPort", proxy.port);

		//System.out.println("---------------Change port----------------");

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
		for (int i = 0; i < proxypool.getProxyList().size(); i++)
		{
			mintime = Math.max(mintime, proxypool.getProxyList().get(i)
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

		for (int i = 0; i < proxypool.getProxyList().size(); i++)
		{
			proxypool.getProxyList().get(i).setCount(0);
			proxypool.getProxyList().get(i).AddTimestamp(remain);

		}
	}

	// if return is null, should continue the loop or sleep the thread
	public Proxy GetProxy()
	{
		Proxy proxypack = null;

		minpos = proxypool.GetMinPos();
		proxypack = proxypool.getProxyList().get(minpos);

		if (proxypack == null)
		{
			System.out.println("Proxy error");
			mylogger.Write("Proxy error");
			return null;
		}

		if (proxypack.getCount() < Maxiumcount)
		{
			replaceKey(proxypack);
		}
		else
		{
			RefreshAllTime();
			return null;
		}
		return proxypack;
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
			minpos = proxypool.GetMinPos();

		if (count == -1)
		{
			proxypool.getProxyList().get(minpos).setCount(Maxiumcount);
			log.log(Level.SEVERE, "miss uid " + uid);
			mylogger.Write("miss uid " + uid);
			return false;
		}
		
		
		try
		{// frozen KEY?
			proxypool.getProxyList().get(minpos).addCount(count + 1);

			if (proxypool.getProxyList().get(minpos).getCount() > thresold)
				proxypool.getProxyList().get(minpos).setCount(150);

		}
		catch (Exception ioe)
		{
			WriteLog(uid);
			System.out.println("Failed to read the system input.");
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
				+ " Get Token at token pos " + proxypool.getPos());
		mylogger.Write(inputFormat.format(new Date())
				+ " Get Token at token pos " + proxypool.getPos());
	}

	public void CloseLogger()
	{
		mylogger.Close();
	}

	public int getPos()
	{
		return proxypool.getPos();
	}
}
