package Util;

import java.util.*;
import java.io.*;

public class ShutdownThread
{
	public ShutdownThread()
	{
		doShutDownWork(null);
	}

	public ShutdownThread(Set<String> uidSet)
	{
		doShutDownWork(uidSet);

	}

	/***************************************************************************
	 * This is the right work that will do before the system shutdown
	 * 这里为了演示，为应用程序的退出增加了一个事件处理，
	 * 当应用程序退出时候，将程序退出的日期写入 d:\t.log文件
	 **************************************************************************/

	private void doShutDownWork(final Set<String> uidSet)
	{
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					FileWriter fw = new FileWriter(".\\logs\\t.log");
					System.out.println("Im going to end");

					if (uidSet != null)
					{
						String uiddir = ".\\realestate\\uid\\";
						myWriter uidOutput = new myWriter(uiddir + "alluid",
								true);
						Iterator it = uidSet.iterator();
						while (it.hasNext())
						{
							String outStr = (String) it.next();
							uidOutput.Write(outStr);
						}
					}

					fw.write("the application ended! "
							+ (new Date()).toString());
					fw.close();
				}
				catch (IOException ex)
				{
				}
			}
		});

	}
}
