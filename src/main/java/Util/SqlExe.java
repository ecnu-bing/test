package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Dao.Dao;
import Dao.JDBC;

public class SqlExe
{
	static Dao dao;
	private static Connection queryconnection = null;

	static ArrayList<String> stmts = null;
	String outpath = ".\\data\\sqlexe";

	static String fpath = "D:\\edges.sql";

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

	public String getFpath()
	{
		return fpath;
	}

	public void NowFpath()
	{
		outpath = outpath += inputFormat.format((new Date()).getTime())
				.toString();
	}

	public void setFpath(String fpath)
	{
		SqlExe.fpath = fpath;
	}

	static int pos = 0;

	void Output()
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

	static void EXE()
	{
		stmts = new ArrayList<String>();
		String inline = "";
		String path = fpath;
		BufferedReader reader = null;

		int count = 0;
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

				if (inline.charAt(0) != 'I')
					continue;
				stmts.add(inline);

				if (stmts.size() > 5000)
				{
					count += stmts.size();
					runSTMT();

					System.out.println("5000 ran total " + count + " rows");
				}

			}

			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (stmts.size() > 0)
		{
			count += stmts.size();
			runSTMT();
			System.out.println("5000 ran till now, total " + count + " rows");
		}

	}

	public static void runSTMT()
	{
		for (int i = 0; i < stmts.size(); i++)
		{
			String st = stmts.get(i);
			if (dao.isToprint())
				System.out.println(st);
			if (dao.isToexecute())
				dao.longInsert(st);
		}
		stmts.clear();

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
	}

	public static void main(String ags[])
	{
		if (!GetDB())
			return;

		System.out.println("Running");

		EXE();

		System.out.println("Success");
	}

	static boolean GetDB()
	{
		queryconnection = JDBC.getConnection();

		if (queryconnection == null)
		{
			System.out.println("Fail when server get connect to sql");
			return false;
		}

		dao = new Dao();

		dao.setToprint(true);
	//	dao.setToexecute(false);

		return true;
	}

}
