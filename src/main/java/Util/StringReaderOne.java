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

public class StringReaderOne
{
	ArrayList<String> ids = null;
	String outpath = ".\\data\\UidOut";
	String fpath = ".\\data\\timelinetest";
	BufferedWriter writer = null;
	int pos = 0;
	SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	public int IDGetPos(String id)
	{
		int ret = ids.indexOf(id);
		return ret;
	}
	
	public String getOutpath()
	{
		return outpath;
	}

	public void setOutpath(String outpath)
	{
		this.outpath = outpath;
	}

	public String getFpath()
	{
		return fpath;
	}

	public StringReaderOne()
	{
		NowFpath();
		run();
	}

	public void NowFpath()
	{
		outpath = outpath += inputFormat.format((new Date()).getTime())
				.toString();
	}

	public StringReaderOne(String path)
	{
		NowFpath();
		System.out.println("Read " + path);
		setFpath(path);
		run();
	}

	public StringReaderOne(String path, String outpath)
	{
		setFpath(path);
		setOutpath(outpath);
		run();
		allocWriter();
	}

	public void setFpath(String fpath)
	{
		this.fpath = fpath;
		System.out.println(this.fpath);
	}

	public int getPos()
	{
		return pos;
	}

	public boolean setPos(int pos)
	{
		if (ids != null && pos < ids.size())
		{
			this.pos = pos;
			return true;
		}
		return false;
	}

	

	public boolean IsOver()
	{
		return (pos >= ids.size());
	}

	public void run()
	{

		ids = new ArrayList<String>();
		Input();
	}

	public String GetNewID()
	{
		if (pos < ids.size())
			return ids.get(pos++);
		else
			return null;

	}

	public String GetStrictNewID()
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
			    if(inline.length()==0)
			    {
			        continue;
			    }
				if (inline.charAt(0) == '/')
					continue;
				inline = inline.trim();
				if (inline.equals(""))
					continue;
				if(!ids.contains(inline))
				{
					ids.add(inline);
				}
			}

			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void allocWriter()
	{
		try
		{
			writer = new BufferedWriter(new FileWriter(outpath));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void addId(String id)
	{
		ids.add(id);
	}
	
	public void removeOldID(String id)
	{
		ids.remove(id);
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
