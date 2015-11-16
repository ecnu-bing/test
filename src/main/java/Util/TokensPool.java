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

public class TokensPool
{
	static int pos = 0;
	// PriorityQueue<Token> tokenList = null;
	ArrayList<Token> tokenList = null;
	String fpath = "."+File.separator+"Tokens"+File.separator+"tokens.txt";
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
		return (pos >= tokenList.size());
	}

	public Token GetNewToken()
	{
		Collections.sort(tokenList);
		return tokenList.get(0);
	}

	public void SortList()
	{

		Collections.sort(tokenList);
	}

	public int GetMinPos()
	{
		return tokenList.indexOf(Collections.min(tokenList));
	}

	public void AddBackToken(Token token)
	{
		tokenList.add(token);
	}

	public TokensPool()
	{
		run();
	}

	public void run()
	{
		tokenList = null;
		tokenList = new ArrayList<Token>();
		Input();
	}
	
	public TokensPool(String path)
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
			int c = 0;
			while ((inline = reader.readLine()) != null)
			{
				if (inline.equals(""))
					continue;
				tokenList.add(new Token(inline));
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
			writer = new BufferedWriter(new FileWriter(
					".\\Tokens\\TokensOut.txt"));

			writer.write(str);

			writer.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Token> getTokenList()
	{
		return tokenList;
	}

}
