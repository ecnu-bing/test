package Util;

import java.util.Date;

public class Proxy implements Comparable<Proxy>{
	public String url;
	public String port;
	int count;
	long timestamp;

	Proxy()
	{
		url = "";
		timestamp = (new Date()).getTime();
	}

	Proxy(String url,String port)
	{
		this.url = url;
		this.port = port;
		count = 0;
		timestamp = (new Date()).getTime();
	}

	@Override
	public String toString()
	{
		return url+":"+port;

	}

	public void addCount(int n)
	{
		count += n;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void AddTimestamp(long n)
	{
		this.timestamp += n;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public int compareTo(Proxy o)
	{

		if (count < o.getCount())
			return -1;
		else if (count > o.getCount())
			return 1;

		if (timestamp < o.getTimestamp())
			return -1;
		else if((timestamp > o.getTimestamp()))
			return 1;
		
		return 0;
	}

}
