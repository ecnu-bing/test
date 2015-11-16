package Util;

import java.util.Date;

public class Token implements Comparable<Token>
{
	public String consumer_key;
	public String consumer_secret;
	public String token;
	public String secret;
	public String redirectUrl;
	int count;
	long timestamp;

	Token()
	{
		//consumer_key = "";
		//consumer_secret = "";
		token = "";
		//secret = "";
		//redirectUrl="";
		count = 0;
		timestamp = (new Date()).getTime();
	}
/*oauth1.0*/
	Token(String ck, String cs, String t, String url)
	{
		consumer_key = ck;
		consumer_secret = cs;
		token = t;
		//secret = s;
		redirectUrl=url;
		count = 0;
		timestamp = (new Date()).getTime();
	}
	/*oauth2.0*/
	Token(String at)
	{
		//consumer_key = ck;
		//consumer_secret = cs;
		token = at;	
		count = 0;
		timestamp = (new Date()).getTime();
	}
	@Override
	public String toString()
	{
		return consumer_key + " " + consumer_secret + " " + token + " "
				+ secret;

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

	public int compareTo(Token o)
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
