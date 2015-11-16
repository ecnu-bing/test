package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ProxyPool {
	static int pos = 0;
	// PriorityQueue<Token> tokenList = null;
	ArrayList<Proxy> proxyList = null;
	String fpath = "."+File.separator+"Tokens"+File.separator+"proxy.txt";
	int allcount = 0;

	public String getFpath()
	{
		return fpath;
	}

	public void setFpath(String fpath)
	{
		this.fpath = fpath;
	}

	public int getPos()
	{
		return pos;
	}

	public void ResetPos()
	{
		pos = 0;
	}

	public boolean IsOver()
	{
		return (pos >= proxyList.size());
	}

	public Proxy GetNewProxy()
	{
		Collections.sort(proxyList);
		return proxyList.get(0);
	}

	public void SortList()
	{
		Collections.sort(proxyList);
	}

	public int GetMinPos()
	{
		return proxyList.indexOf(Collections.min(proxyList));
	}

	public void AddBackProxy(Proxy proxy)
	{
		proxyList.add(proxy);
	}

	public ProxyPool()
	{
		run();
	}

	public void run()
	{
		proxyList = null;
		proxyList = new ArrayList<Proxy>();
		Input();
	}
	
	public ProxyPool(String path)
	{
		setFpath(path);
		run();
	}

	void Input()
	{
		String inline = "";
		String path = fpath;
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(path));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		try
		{
			while ((inline = reader.readLine()) != null)
			{
				if (inline.equals(""))
					continue;
				String splits[] = inline.split(":");
				//System.out.println(inline);
				proxyList.add(new Proxy(splits[0],splits[1]));
			}

			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void Write(String str)
	{
		BufferedWriter writer = null;

		try
		{
			writer = new BufferedWriter(new FileWriter(".\\Tokens\\ProxyOut.txt"));

			writer.write(str);

			writer.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Proxy> getProxyList()
	{
		return proxyList;
	}

}
