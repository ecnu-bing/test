package Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class MyLogger
{
	 SimpleDateFormat inputFormat = new SimpleDateFormat(
	"yyyy-MM-dd-HH-mm-ss");

	String outpath = "."+File.separator+"logs"+File.separator+"runninglog.txt";
	String dirpath = "."+File.separator+"logs"+File.separator;

	 BufferedWriter writer = null;

	public String getOutpath()
	{
		return outpath;
	}

	public void setOutpath(String outpath)
	{
		this.outpath = outpath;
	}

	public MyLogger()
	{
		this.outpath = dirpath + inputFormat.format((new Date()).getTime()).toString()+"runninglog.txt";
		run();
	}

	public MyLogger(String start)
	{
		this.outpath = dirpath + start +"runninglog.txt";
		setOutpath(this.outpath);
		run();
	}

	public void run()
	{
		Output();
	}

	void Output()
	{
		try
		{
			writer = new BufferedWriter(new FileWriter(outpath,true));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
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

	public void Close()
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
