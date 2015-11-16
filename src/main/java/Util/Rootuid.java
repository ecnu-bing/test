package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Rootuid
{
	static ArrayList<String> ids = null;
	public static ArrayList<String> getIds() {
		return ids;
	}

	String outpath = ".\\data\\UidOut";
	String fpath = ".\\data\\timelinetest";
	static BufferedWriter writer = null;

	static SimpleDateFormat inputFormat = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	public String getOutpath()
	{
		return outpath;
	}

	public void setOutpath(String outpath)
	{
		this.outpath = outpath;
	}

	public int IDGetPos(String id)
	{
		int ret = ids.indexOf(id);
		return ret;
	}
	
	public String getFpath()
	{
		return fpath;
	}

	public Rootuid()
	{
		NowFpath();
		run(false);
	}

	public void NowFpath()
	{
		outpath = outpath += inputFormat.format((new Date()).getTime())
				.toString();
	}

	public Rootuid(String path)
	{
		NowFpath();
		setFpath(path);
		run(false);
	}

	public Rootuid(String path, String outpath)
	{
		setFpath(path);
		setOutpath(outpath);
		run(false);
	}

	public Rootuid(boolean b)
	{
		NowFpath();
		run(b);
	}

	public Rootuid(String path, boolean b)
	{
		NowFpath();
		setFpath(path);
		run(b);
	}

	public Rootuid(String path, String outpath, boolean b)
	{
		setFpath(path);
		setOutpath(outpath);
		run(b);
	}

	public void setFpath(String fpath)
	{
		this.fpath = fpath;
	}

	public int getPos()
	{
		return pos;
	}

	public static boolean setPos(int pos)
	{
		if (ids != null && pos < ids.size())
		{
			Rootuid.pos = pos;
			return true;
		}
		return false;
	}

	static int pos = 0;

	public boolean IsOver()
	{
		return (pos >= ids.size());
	}

	public void run(boolean b)
	{

		ids = new ArrayList<String>();

		Input();
		Output(b);
	}

	public String GetNewID()
	{
		if (pos < ids.size())
			return ids.get(pos++);
		else
			return null;

	}

	public String GetNewIDDontPlus()
	{
		if (pos < ids.size())
			return ids.get(pos);
		else
			return null;

	}

	public void idOK()
	{
		pos++;
	}

	void Output(boolean b)
	{
		try
		{
			if (b)
				writer = new BufferedWriter(new FileWriter(outpath, b));
			else
				writer = new BufferedWriter(new FileWriter(outpath));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				inline = inline.trim();
				if (inline.equals(""))
					continue;
				if (inline.charAt(0) == '/')
					continue;
				inline = inline.trim();
				ids.add(inline);

				/*if(!ids.contains(inline))
				{
				    ids.add(inline);
				}*/
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
		try
		{
			writer.append(str);
			writer.newLine();
			writer.flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeWrite()
	{
		try
		{
			writer.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
	}
}
