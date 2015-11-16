package Util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class myWriter
{
	String outDir = ".\\Output";
	String outpath = ".\\data\\UidOut";
	BufferedWriter writer = null;
	OutputStream outputStream = null;
	OutputStreamWriter outputStreamWriter = null;
	SimpleDateFormat inputFormat = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");
	

	public myWriter()
	{
		outpath = outDir
				+ inputFormat.format((new Date()).getTime()).toString();
		allocWriter();
	}

	public myWriter(String aoutpath)
	{
		outpath = aoutpath;
		allocWriter();
	}

	public myWriter(String dir, boolean now)
	{
		if(now)
			outpath = dir +inputFormat.format((new Date()).getTime()).toString()+".txt";
		else
			outpath = dir+".txt";
		
		allocWriter();
	}

	public void allocWriter()
	{
		try
		{
			outputStream = new FileOutputStream (outpath,true);
			//writer = new BufferedWriter(new FileWriter(outpath, true));
			outputStreamWriter = new OutputStreamWriter(outputStream,"GBK");
			writer = new BufferedWriter(outputStreamWriter);
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